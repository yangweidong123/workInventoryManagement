package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.PlatformOrder;
import org.apache.ibatis.annotations.Param;

public interface PlatformOrderMapper extends BaseMapper<PlatformOrder> {

    PlatformOrder selectByPlatformAndOrderId(@Param("platform") String platform, @Param("orderId") String orderId);
}
