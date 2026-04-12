package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.AuditLogQuery;
import com.inventory.entity.AuditLog;
import com.inventory.mapper.AuditLogMapper;
import com.inventory.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public IPage<AuditLog> page(AuditLogQuery query) {
        AuditLogMapper.AuditLogQuery q = new AuditLogMapper.AuditLogQuery();
        q.setStartDate(query.getStartDate());
        q.setEndDate(query.getEndDate());
        q.setUsername(query.getUsername());
        q.setOperation(query.getOperation());
        q.setKeyword(query.getKeyword());
        return auditLogMapper.selectPageList(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getCurrent(), query.getSize()),
            q
        );
    }

    @Override
    public java.util.List<AuditLog> listByDateRange(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusWeeks(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        return auditLogMapper.selectByDateRange(start, end);
    }

    @Override
    public void log(Long userId, String username, String operation, String content, String method, String ip, String userAgent) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setContent(content);
        log.setMethod(method);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        auditLogMapper.insert(log);
    }
}
