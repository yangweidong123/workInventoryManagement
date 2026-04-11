package com.inventory.service.impl;

import com.inventory.dto.DailyStatsDTO;
import com.inventory.dto.InventoryOutDTO;
import com.inventory.entity.DailyStats;
import com.inventory.entity.Inventory;
import com.inventory.mapper.DailyStatsMapper;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.InventoryOutMapper;
import com.inventory.service.InventoryOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryOutServiceImpl implements InventoryOutService {

    @Autowired
    private InventoryOutMapper inventoryOutMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private DailyStatsMapper dailyStatsMapper;

    @Override
    @Transactional
    public void createOut(InventoryOutDTO dto) {
        Inventory inventory = inventoryMapper.selectById(dto.getInventoryId());
        if (inventory == null) {
            throw new RuntimeException("商品不存在");
        }

        if (inventory.getQuantity() < dto.getQuantity()) {
            throw new RuntimeException("库存不足，当前库存：" + inventory.getQuantity());
        }

        BigDecimal totalAmount = inventory.getPriceExclTax().multiply(new BigDecimal(dto.getQuantity()));

        InventoryOutDTO outRecord = new InventoryOutDTO();
        outRecord.setInventoryId(dto.getInventoryId());
        outRecord.setStyleNo(inventory.getStyleNo());
        outRecord.setName(inventory.getName());
        outRecord.setQuantity(dto.getQuantity());
        outRecord.setPrice(inventory.getPriceExclTax());
        outRecord.setTotalAmount(totalAmount);
        outRecord.setOutTime(LocalDateTime.now());
        outRecord.setOperator(dto.getOperator());
        outRecord.setRemark(dto.getRemark());

        inventoryOutMapper.insert(outRecord);

        inventory.setQuantity(inventory.getQuantity() - dto.getQuantity());
        inventoryMapper.updateById(inventory);

        updateDailyStats(LocalDate.now(), dto.getQuantity(), totalAmount);
    }

    @Override
    public List<InventoryOutDTO> listByDateRange(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        return inventoryOutMapper.selectByDateRange(start, end).stream().map(out -> {
            InventoryOutDTO dto = new InventoryOutDTO();
            dto.setId(out.getId());
            dto.setInventoryId(out.getInventoryId());
            dto.setStyleNo(out.getStyleNo());
            dto.setName(out.getName());
            dto.setQuantity(out.getQuantity());
            dto.setPrice(out.getPrice());
            dto.setTotalAmount(out.getTotalAmount());
            dto.setOutTime(out.getOutTime());
            dto.setOperator(out.getOperator());
            dto.setRemark(out.getRemark());
            return dto;
        }).collect(Collectors.toList());
    }

    private void updateDailyStats(LocalDate date, Integer outCount, BigDecimal outAmount) {
        DailyStats stats = dailyStatsMapper.selectByDate(date);
        if (stats == null) {
            stats = new DailyStats();
            stats.setStatDate(date);
            stats.setInventoryInCount(0);
            stats.setInventoryInAmount(BigDecimal.ZERO);
            stats.setInventoryOutCount(outCount);
            stats.setInventoryOutAmount(outAmount);
            stats.setPackageSoldCount(0);
            stats.setPackageSoldAmount(BigDecimal.ZERO);
            dailyStatsMapper.insert(stats);
        } else {
            stats.setInventoryOutCount(stats.getInventoryOutCount() + outCount);
            stats.setInventoryOutAmount(stats.getInventoryOutAmount().add(outAmount));
            dailyStatsMapper.updateById(stats);
        }
    }
}
