package com.inventory.dto;

import lombok.Data;

@Data
public class MenuDTO {
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
}
