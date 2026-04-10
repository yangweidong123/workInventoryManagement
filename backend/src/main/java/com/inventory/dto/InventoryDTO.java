package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InventoryDTO {

    private Long id;

    private String styleNo;

    private String name;

    private String sizeMm;

    private BigDecimal weightKg;

    private Integer boxSpec;

    private BigDecimal priceExclTax;

    private BigDecimal guidePrice;

    private BigDecimal minPrice;

    private Integer quantity;

    private List<InventoryImageDTO> images;

    private BigDecimal goodsValue;

    private BigDecimal totalWeight;
}
