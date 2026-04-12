package com.inventory.dto;

import lombok.Data;

@Data
public class SyncLogQuery {
    private String startDate;
    private String endDate;
    private String platform;
    private String operationType;
    private String orderId;
    private String syncStatus;
    private Integer current = 1;
    private Integer size = 20;
}
