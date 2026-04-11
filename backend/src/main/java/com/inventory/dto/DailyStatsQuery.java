package com.inventory.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyStatsQuery {

    private LocalDate startDate;

    private LocalDate endDate;
}
