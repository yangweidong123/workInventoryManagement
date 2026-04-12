package com.inventory.controller;

import com.inventory.dto.Result;
import com.inventory.entity.PlatformConfig;
import com.inventory.service.PlatformConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform-config")
public class PlatformConfigController {

    @Autowired
    private PlatformConfigService platformConfigService;

    @GetMapping
    public Result<List<PlatformConfig>> list() {
        return Result.success(platformConfigService.list());
    }

    @GetMapping("/{id}")
    public Result<PlatformConfig> getById(@PathVariable Long id) {
        return Result.success(platformConfigService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody PlatformConfig config) {
        return Result.success(platformConfigService.create(config));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PlatformConfig config) {
        platformConfigService.update(id, config);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        platformConfigService.delete(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        platformConfigService.enable(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        platformConfigService.disable(id);
        return Result.success(null);
    }

    @PostMapping("/test-connection")
    public Result<Boolean> testConnection(@RequestBody PlatformConfig config) {
        return Result.success(platformConfigService.testConnection(config));
    }
}
