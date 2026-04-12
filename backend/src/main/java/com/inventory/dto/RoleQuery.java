package com.inventory.dto;

import lombok.Data;

@Data
public class RoleQuery {
    private String keyword;
    private Integer current = 1;
    private Integer size = 20;
}
