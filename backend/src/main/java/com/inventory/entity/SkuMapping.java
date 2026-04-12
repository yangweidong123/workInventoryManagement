package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sku_mapping")
public class SkuMapping {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inventoryId;

    private String platform;

    private String platformSku;

    private String platformProductId;

    private Integer lastSyncQuantity;

    private LocalDateTime lastSyncTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
