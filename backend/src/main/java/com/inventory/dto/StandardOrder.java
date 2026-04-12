package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StandardOrder {
    private String orderId;
    private String platform;
    private String type;
    private String platformSku;
    private String systemSku;
    private Integer quantity;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidTime;
    private LocalDateTime refundTime;
    private String rawData;
}
