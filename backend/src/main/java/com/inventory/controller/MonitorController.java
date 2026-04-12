package com.inventory.controller;

import com.inventory.annotation.RequirePermission;
import com.inventory.dto.Result;
import com.inventory.dto.SystemMetrics;
import com.inventory.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/metrics")
    @RequirePermission("system:monitor:view")
    public Result<SystemMetrics> getMetrics() {
        return Result.success(monitorService.getMetrics());
    }
}
