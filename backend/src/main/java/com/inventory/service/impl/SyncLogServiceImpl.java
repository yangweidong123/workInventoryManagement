package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.SyncLogQuery;
import com.inventory.entity.SyncLog;
import com.inventory.mapper.SyncLogMapper;
import com.inventory.service.SyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class SyncLogServiceImpl implements SyncLogService {

    @Autowired
    private SyncLogMapper syncLogMapper;

    @Override
    public IPage<SyncLog> page(SyncLogQuery query) {
        return syncLogMapper.selectPageList(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(query.getCurrent(), query.getSize()),
            query
        );
    }

    @Override
    public List<SyncLog> listByDateRange(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.now().minusMonths(1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);
        return syncLogMapper.selectByDateRange(start, end);
    }

    @Override
    public void createLog(SyncLog log) {
        syncLogMapper.insert(log);
    }

    @Override
    public void updateStatus(Long id, String status, String errorMessage) {
        SyncLog log = syncLogMapper.selectById(id);
        if (log != null) {
            log.setSyncStatus(status);
            log.setErrorMessage(errorMessage);
            syncLogMapper.updateById(log);
        }
    }
}
