package com.inventory.controller;

import com.inventory.dto.Result;
import com.inventory.entity.SkuMapping;
import com.inventory.service.SkuMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sku-mapping")
public class SkuMappingController {

    @Autowired
    private SkuMappingService skuMappingService;

    @GetMapping("/{id}")
    public Result<SkuMapping> getById(@PathVariable Long id) {
        return Result.success(skuMappingService.getById(id));
    }

    @GetMapping("/platform/{platform}")
    public Result<List<SkuMapping>> getByPlatform(@PathVariable String platform) {
        return Result.success(skuMappingService.getByPlatform(platform));
    }

    @GetMapping("/inventory/{inventoryId}")
    public Result<List<SkuMapping>> getByInventoryId(@PathVariable Long inventoryId) {
        return Result.success(skuMappingService.getByInventoryId(inventoryId));
    }

    @PostMapping
    public Result<SkuMapping> create(@RequestBody SkuMapping mapping) {
        SkuMapping result = skuMappingService.createMapping(
            mapping.getInventoryId(),
            mapping.getPlatform(),
            mapping.getPlatformSku(),
            mapping.getPlatformProductId()
        );
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SkuMapping mapping) {
        skuMappingService.updateMapping(id, mapping);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        skuMappingService.deleteMapping(id);
        return Result.success(null);
    }
}
