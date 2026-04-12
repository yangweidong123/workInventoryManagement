package com.inventory.service;

import com.inventory.dto.PackageSoldRecordDTO;
import java.util.List;

public interface PackageSoldRecordService {

    void recordSale(Long packageId, String packageName, Integer quantity, java.math.BigDecimal unitPrice, java.math.BigDecimal totalAmount, String operator);

    List<PackageSoldRecordDTO> listByDateRange(String startDate, String endDate);
}
