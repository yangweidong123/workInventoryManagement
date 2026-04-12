package com.inventory.service.impl;

import com.inventory.dto.PackageSoldRecordDTO;
import com.inventory.entity.PackageSoldRecord;
import com.inventory.mapper.PackageSoldRecordMapper;
import com.inventory.service.PackageSoldRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageSoldRecordServiceImpl implements PackageSoldRecordService {

    @Autowired
    private PackageSoldRecordMapper recordMapper;

    @Override
    public void recordSale(Long packageId, String packageName, Integer quantity, BigDecimal unitPrice, BigDecimal totalAmount, String operator) {
        PackageSoldRecord record = new PackageSoldRecord();
        record.setPackageId(packageId);
        record.setPackageName(packageName);
        record.setQuantity(quantity);
        record.setUnitPrice(unitPrice);
        record.setTotalAmount(totalAmount);
        record.setSoldTime(LocalDateTime.now());
        record.setOperator(operator);
        recordMapper.insert(record);
    }

    @Override
    public List<PackageSoldRecordDTO> listByDateRange(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        
        return recordMapper.selectByDateRange(start, end).stream().map(record -> {
            PackageSoldRecordDTO dto = new PackageSoldRecordDTO();
            dto.setId(record.getId());
            dto.setPackageId(record.getPackageId());
            dto.setPackageName(record.getPackageName());
            dto.setQuantity(record.getQuantity());
            dto.setUnitPrice(record.getUnitPrice());
            dto.setTotalAmount(record.getTotalAmount());
            dto.setSoldTime(record.getSoldTime());
            dto.setOperator(record.getOperator());
            return dto;
        }).collect(Collectors.toList());
    }
}
