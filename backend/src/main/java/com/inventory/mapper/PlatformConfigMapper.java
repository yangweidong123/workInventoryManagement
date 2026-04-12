package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.PlatformConfig;
import org.apache.ibatis.annotations.Param;

public interface PlatformConfigMapper extends BaseMapper<PlatformConfig> {

    PlatformConfig selectByPlatform(@Param("platform") String platform);
}
