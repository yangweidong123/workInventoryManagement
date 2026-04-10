package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.InventoryDTO;
import com.inventory.dto.InventoryQuery;

public interface InventoryService {

    IPage<InventoryDTO> page(InventoryQuery query);

    InventoryDTO getById(Long id);

    Long create(InventoryDTO dto);

    void update(Long id, InventoryDTO dto);

    void delete(Long id);
}
