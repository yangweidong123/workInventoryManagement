package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.PackageDTO;
import com.inventory.dto.PackageQuery;

import java.math.BigDecimal;
import java.util.List;

public interface PackageService {

    IPage<PackageDTO> page(PackageQuery query);

    PackageDTO getById(Long id);

    Long create(PackageDTO dto);

    void update(Long id, PackageDTO dto);

    void delete(Long id);

    BigDecimal calculateProfit(List<Long> inventoryIds, List<Integer> quantities, BigDecimal totalPrice);

    BigDecimal getInventoryPrice(Long inventoryId);
}
