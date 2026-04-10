package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inventory_image")
public class InventoryImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long inventoryId;

    private String imageUrl;

    private Integer sort;

    private LocalDateTime createTime;
}
