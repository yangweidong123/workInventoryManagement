package com.inventory.dto;

import lombok.Data;

@Data
public class InventoryQuery {

    private Integer current = 1;

    private Integer size = 10;

    private String styleNo;

    private String name;
}
