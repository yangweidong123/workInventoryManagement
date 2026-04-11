package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.*;
import com.inventory.entity.DailyStats;
import com.inventory.entity.Inventory;
import com.inventory.entity.Package;
import com.inventory.entity.PackageItem;
import com.inventory.mapper.DailyStatsMapper;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.PackageItemMapper;
import com.inventory.mapper.PackageMapper;
import com.inventory.service.PackageService;
import com.inventory.service.PackageSoldRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageMapper packageMapper;

    @Autowired
    private PackageItemMapper packageItemMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private DailyStatsMapper dailyStatsMapper;

    @Autowired
    private PackageSoldRecordService packageSoldRecordService;

    @Override
    public IPage<PackageDTO> page(PackageQuery query) {
        Page<PackageDTO> page = new Page<>(query.getCurrent(), query.getSize());
        return packageMapper.selectPageList(page, query);
    }

    @Override
    public PackageDTO getById(Long id) {
        PackageDTO dto = packageMapper.selectDetailById(id);
        if (dto == null) {
            return null;
        }

        List<PackageItem> items = packageItemMapper.selectByPackageId(id);
        List<PackageItemDTO> itemDTOs = new ArrayList<>();

        for (PackageItem item : items) {
            Inventory inventory = inventoryMapper.selectById(item.getInventoryId());
            if (inventory != null) {
                PackageItemDTO itemDto = new PackageItemDTO();
                itemDto.setId(item.getId());
                itemDto.setInventoryId(item.getInventoryId());
                itemDto.setInventoryName(inventory.getName());
                itemDto.setStyleNo(inventory.getStyleNo());
                itemDto.setPriceExclTax(inventory.getPriceExclTax());
                itemDto.setGuidePrice(inventory.getGuidePrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setSubtotal(inventory.getPriceExclTax().multiply(new BigDecimal(item.getQuantity())));
                itemDTOs.add(itemDto);
            }
        }
        dto.setItems(itemDTOs);
        return dto;
    }

    @Override
    @Transactional
    public Long create(PackageDTO dto) {
        validatePackage(dto);

        BigDecimal costPrice = calculateCostPrice(dto.getItems());
        BigDecimal profitRate = calculateProfitRate(dto.getTotalPrice(), costPrice);

        Package entity = new Package();
        entity.setName(dto.getName());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setCostPrice(costPrice);
        entity.setProfitRate(profitRate);
        packageMapper.insert(entity);

        saveItems(entity.getId(), dto.getItems());
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, PackageDTO dto) {
        Package existing = packageMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("套餐不存在");
        }
        validatePackage(dto);

        BigDecimal costPrice = calculateCostPrice(dto.getItems());
        BigDecimal profitRate = calculateProfitRate(dto.getTotalPrice(), costPrice);

        existing.setName(dto.getName());
        existing.setTotalPrice(dto.getTotalPrice());
        existing.setCostPrice(costPrice);
        existing.setProfitRate(profitRate);
        packageMapper.updateById(existing);

        packageItemMapper.deleteByPackageId(id);
        saveItems(id, dto.getItems());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        packageItemMapper.deleteByPackageId(id);
        packageMapper.deleteById(id);
    }

    @Override
    public BigDecimal getInventoryPrice(Long inventoryId) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        return inventory != null ? inventory.getPriceExclTax() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateProfit(List<Long> inventoryIds, List<Integer> quantities, BigDecimal totalPrice) {
        if (inventoryIds == null || quantities == null || totalPrice == null) {
            return null;
        }

        BigDecimal costPrice = BigDecimal.ZERO;
        for (int i = 0; i < inventoryIds.size(); i++) {
            Inventory inventory = inventoryMapper.selectById(inventoryIds.get(i));
            if (inventory != null) {
                costPrice = costPrice.add(
                    inventory.getPriceExclTax().multiply(new BigDecimal(quantities.get(i)))
                );
            }
        }

        return calculateProfitRate(totalPrice, costPrice);
    }

    private BigDecimal calculateCostPrice(List<PackageItemDTO> items) {
        BigDecimal costPrice = BigDecimal.ZERO;
        for (PackageItemDTO item : items) {
            Inventory inventory = inventoryMapper.selectById(item.getInventoryId());
            if (inventory != null) {
                costPrice = costPrice.add(
                    inventory.getPriceExclTax().multiply(new BigDecimal(item.getQuantity()))
                );
            }
        }
        return costPrice;
    }

    private BigDecimal calculateProfitRate(BigDecimal totalPrice, BigDecimal costPrice) {
        if (costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return totalPrice.subtract(costPrice)
            .divide(costPrice, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .setScale(2, RoundingMode.HALF_UP);
    }

    private void saveItems(Long packageId, List<PackageItemDTO> items) {
        for (PackageItemDTO itemDto : items) {
            PackageItem item = new PackageItem();
            item.setPackageId(packageId);
            item.setInventoryId(itemDto.getInventoryId());
            item.setQuantity(itemDto.getQuantity());
            packageItemMapper.insert(item);
        }
    }

    @Override
    @Transactional
    public void sell(Long id, Integer quantity) {
        Package pkg = packageMapper.selectById(id);
        if (pkg == null) {
            throw new RuntimeException("套餐不存在");
        }

        List<PackageItem> items = packageItemMapper.selectByPackageId(id);
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("套餐内没有商品");
        }

        for (PackageItem pkgItem : items) {
            Inventory inventory = inventoryMapper.selectById(pkgItem.getInventoryId());
            if (inventory == null) {
                throw new RuntimeException("商品不存在");
            }

            int requiredQty = pkgItem.getQuantity() * quantity;
            if (inventory.getQuantity() < requiredQty) {
                throw new RuntimeException("商品【" + inventory.getName() + "】库存不足，需要" + requiredQty + "，实际库存" + inventory.getQuantity());
            }

            inventory.setQuantity(inventory.getQuantity() - requiredQty);
            inventoryMapper.updateById(inventory);
        }

        pkg.setSoldQuantity((pkg.getSoldQuantity() == null ? 0 : pkg.getSoldQuantity()) + quantity);
        packageMapper.updateById(pkg);

        packageSoldRecordService.recordSale(pkg.getId(), pkg.getName(), quantity, pkg.getTotalPrice(), pkg.getTotalPrice().multiply(new BigDecimal(quantity)), null);

        updateDailyStats(LocalDate.now(), quantity, pkg.getTotalPrice().multiply(new BigDecimal(quantity)));
    }

    private void updateDailyStats(LocalDate date, Integer soldCount, BigDecimal soldAmount) {
        DailyStats stats = dailyStatsMapper.selectByDate(date);
        if (stats == null) {
            stats = new DailyStats();
            stats.setStatDate(date);
            stats.setInventoryInCount(0);
            stats.setInventoryInAmount(BigDecimal.ZERO);
            stats.setInventoryOutCount(0);
            stats.setInventoryOutAmount(BigDecimal.ZERO);
            stats.setPackageSoldCount(soldCount);
            stats.setPackageSoldAmount(soldAmount);
            dailyStatsMapper.insert(stats);
        } else {
            stats.setPackageSoldCount(stats.getPackageSoldCount() + soldCount);
            stats.setPackageSoldAmount(stats.getPackageSoldAmount().add(soldAmount));
            dailyStatsMapper.updateById(stats);
        }
    }
}
