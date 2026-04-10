package com.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class PackageDTO {

    private Long id;

    private String name;

    private java.math.BigDecimal totalPrice;

    private java.math.BigDecimal costPrice;

    private java.math.BigDecimal profitRate;

    private List<PackageItemDTO> items;
}
