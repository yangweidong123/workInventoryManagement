package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.SyncLogQuery;
import com.inventory.entity.SyncLog;

import java.util.List;

public interface SyncLogService {

    IPage<SyncLog> page(SyncLogQuery query);

    List<SyncLog> listByDateRange(String startDate, String endDate);

    void createLog(SyncLog log);

    void updateStatus(Long id, String status, String errorMessage);
}
