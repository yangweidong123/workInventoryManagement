package com.inventory.controller;

import com.inventory.dto.Result;
import com.inventory.dto.SyncLogQuery;
import com.inventory.entity.SyncLog;
import com.inventory.service.SyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync-log")
public class SyncLogController {

    @Autowired
    private SyncLogService syncLogService;

    @GetMapping
    public Result<List<SyncLog>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(syncLogService.listByDateRange(startDate, endDate));
    }

    @PostMapping("/page")
    public Result<?> page(@RequestBody SyncLogQuery query) {
        return Result.success(syncLogService.page(query));
    }
}
