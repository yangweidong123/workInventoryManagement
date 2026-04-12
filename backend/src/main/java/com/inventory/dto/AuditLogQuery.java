package com.inventory.dto;

import lombok.Data;

@Data
public class AuditLogQuery {
    private String startDate;
    private String endDate;
    private String username;
    private String operation;
    private String keyword;
    private Integer current = 1;
    private Integer size = 20;
}
