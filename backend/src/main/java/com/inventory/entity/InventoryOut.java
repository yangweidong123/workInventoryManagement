package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inventory_out")
public class InventoryOut {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inventoryId;

    private String styleNo;

    private String name;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalAmount;

    private LocalDateTime outTime;

    private String operator;

    private String remark;
}
