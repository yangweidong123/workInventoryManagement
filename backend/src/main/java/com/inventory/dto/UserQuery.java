package com.inventory.dto;

import lombok.Data;

@Data
public class UserQuery {
    private String keyword;
    private String status;
    private Integer current = 1;
    private Integer size = 20;
}
