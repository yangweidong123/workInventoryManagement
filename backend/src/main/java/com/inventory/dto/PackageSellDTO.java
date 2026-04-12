package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PackageSellDTO {

    private Long packageId;

    private Integer quantity;

    private BigDecimal totalPrice;
}
