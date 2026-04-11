package com.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyStatsDTO {

    private LocalDate statDate;

    private Integer inventoryInCount;

    private BigDecimal inventoryInAmount;

    private Integer inventoryOutCount;

    private BigDecimal inventoryOutAmount;

    private Integer packageSoldCount;

    private BigDecimal packageSoldAmount;
}
