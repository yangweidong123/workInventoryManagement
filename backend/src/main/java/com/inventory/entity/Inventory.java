package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Inventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String styleNo;

    private String name;

    private String sizeMm;

    private BigDecimal weightKg;

    private Integer boxSpec;

    private BigDecimal priceExclTax;

    private BigDecimal guidePrice;

    private BigDecimal minPrice;

    private BigDecimal packagePrice;

    private Integer quantity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
