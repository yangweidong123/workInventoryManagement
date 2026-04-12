package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PackageSoldRecordDTO {

    private Long id;

    private Long packageId;

    private String packageName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime soldTime;

    private String operator;
}
