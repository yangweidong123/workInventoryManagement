package com.inventory.dto;

import lombok.Data;

@Data
public class PackageQuery {

    private Integer current = 1;

    private Integer size = 10;

    private String name;
}
