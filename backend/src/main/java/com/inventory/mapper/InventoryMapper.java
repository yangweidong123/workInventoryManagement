package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.InventoryDTO;
import com.inventory.dto.InventoryQuery;
import com.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Param;

public interface InventoryMapper extends BaseMapper<Inventory> {

    IPage<InventoryDTO> selectPageList(Page<?> page, @Param("query") InventoryQuery query);

    InventoryDTO selectDetailById(@Param("id") Long id);
}
