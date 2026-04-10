package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.InventoryImage;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface InventoryImageMapper extends BaseMapper<InventoryImage> {

    List<InventoryImage> selectByInventoryId(@Param("inventoryId") Long inventoryId);

    void deleteByInventoryId(@Param("inventoryId") Long inventoryId);
}
