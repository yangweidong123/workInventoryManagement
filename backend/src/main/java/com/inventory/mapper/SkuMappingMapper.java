package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.SkuMapping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuMappingMapper extends BaseMapper<SkuMapping> {

    SkuMapping selectByPlatformAndSku(@Param("platform") String platform, @Param("platformSku") String platformSku);

    List<SkuMapping> selectByInventoryId(@Param("inventoryId") Long inventoryId);

    List<SkuMapping> selectByPlatform(@Param("platform") String platform);
}
