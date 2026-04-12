package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("package")
public class Package {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal totalPrice;

    private BigDecimal costPrice;

    private BigDecimal profitRate;

    private Integer soldQuantity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
