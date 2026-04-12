package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String name;

    private String type;

    private String path;

    private String component;

    private String icon;

    private Integer sort;

    private String permission;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<Menu> children;
}
