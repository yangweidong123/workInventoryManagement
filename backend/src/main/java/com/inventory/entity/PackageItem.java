package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("package_item")
public class PackageItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;

    private Long inventoryId;

    private Integer quantity;
}
