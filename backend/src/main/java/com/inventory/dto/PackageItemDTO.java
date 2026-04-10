package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PackageItemDTO {

    private Long id;

    private Long inventoryId;

    private String inventoryName;

    private String styleNo;

    private BigDecimal priceExclTax;

    private BigDecimal guidePrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
