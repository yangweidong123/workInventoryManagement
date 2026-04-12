package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("platform_order")
public class PlatformOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String platform;

    private String orderId;

    private Long skuMappingId;

    private String orderType;

    private Integer quantity;

    private BigDecimal amount;

    private String status;

    private LocalDateTime paidTime;

    private LocalDateTime refundTime;

    private Integer processed;

    private String rawData;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
