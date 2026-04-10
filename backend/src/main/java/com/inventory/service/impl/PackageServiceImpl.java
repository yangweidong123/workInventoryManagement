package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.*;
import com.inventory.entity.Inventory;
import com.inventory.entity.Package;
import com.inventory.entity.PackageItem;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.PackageItemMapper;
import com.inventory.mapper.PackageMapper;
import com.inventory.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
}
