package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryOutDTO {

    private Long id;

    private Long inventoryId;

    private String styleNo;

    private String name;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal totalAmount;

    private LocalDateTime outTime;

    private String operator;

    private String remark;
}
