package com.inventory.controller;

import com.inventory.annotation.RequirePermission;
import com.inventory.dto.AuditLogQuery;
import com.inventory.dto.Result;
import com.inventory.entity.AuditLog;
import com.inventory.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    @RequirePermission("system:audit:list")
    public Result<List<AuditLog>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(auditLogService.listByDateRange(startDate, endDate));
    }

    @PostMapping("/page")
    @RequirePermission("system:audit:list")
    public Result<?> page(@RequestBody AuditLogQuery query) {
        return Result.success(auditLogService.page(query));
    }
}
