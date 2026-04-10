package com.inventory.dto;

import lombok.Data;

@Data
public class PackageItemDTO {

    private Long id;

    private Long inventoryId;

    private String inventoryName;

    private String styleNo;

    private java.math.BigDecimal priceExclTax;

    private Integer quantity;

    private java.math.BigDecimal subtotal;
}
