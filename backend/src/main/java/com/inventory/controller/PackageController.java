package com.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.PackageDTO;
import com.inventory.dto.PackageQuery;
import com.inventory.dto.PackageItemDTO;
import com.inventory.dto.Result;
import com.inventory.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/package")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @GetMapping
    public Result<IPage<PackageDTO>> list(PackageQuery query) {
        return Result.success(packageService.page(query));
    }

    @GetMapping("/{id}")
    public Result<PackageDTO> getById(@PathVariable Long id) {
        return Result.success(packageService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody PackageDTO dto) {
        return Result.success(packageService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PackageDTO dto) {
        packageService.update(id, dto);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        packageService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/calculate-profit")
    public Result<Map<String, BigDecimal>> calculateProfit(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> inventoryIds = (List<Long>) request.get("inventoryIds");
        @SuppressWarnings("unchecked")
        List<Integer> quantities = (List<Integer>) request.get("quantities");
        BigDecimal totalPrice = new BigDecimal(request.get("totalPrice").toString());

        BigDecimal costPrice = BigDecimal.ZERO;
        for (int i = 0; i < inventoryIds.size(); i++) {
            var inventory = packageService.getInventoryPrice(inventoryIds.get(i));
            if (inventory != null) {
                costPrice = costPrice.add(
                    inventory.multiply(new BigDecimal(quantities.get(i)))
                );
            }
        }

        BigDecimal profitRate = BigDecimal.ZERO;
        if (costPrice.compareTo(BigDecimal.ZERO) > 0) {
            profitRate = totalPrice.subtract(costPrice)
                .divide(costPrice, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        }

        Map<String, BigDecimal> result = Map.of(
            "costPrice", costPrice,
            "totalPrice", totalPrice,
            "profitRate", profitRate
        );
        return Result.success(result);
    }
}
