package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.AuditLogQuery;
import com.inventory.entity.AuditLog;

import java.util.List;

public interface AuditLogService {

    IPage<AuditLog> page(AuditLogQuery query);

    List<AuditLog> listByDateRange(String startDate, String endDate);

    void log(Long userId, String username, String operation, String content, String method, String ip, String userAgent);
}
