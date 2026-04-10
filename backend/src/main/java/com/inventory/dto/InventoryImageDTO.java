package com.inventory.dto;

import lombok.Data;

@Data
public class InventoryImageDTO {

    private Long id;

    private Long inventoryId;

    private String imageUrl;

    private Integer sort;
}
