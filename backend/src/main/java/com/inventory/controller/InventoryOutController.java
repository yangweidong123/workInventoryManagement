package com.inventory.controller;

import com.inventory.dto.InventoryOutDTO;
import com.inventory.dto.Result;
import com.inventory.service.InventoryOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-out")
public class InventoryOutController {

    @Autowired
    private InventoryOutService inventoryOutService;

    @PostMapping
    public Result<Void> create(@RequestBody InventoryOutDTO dto) {
        inventoryOutService.createOut(dto);
        return Result.success("出库成功", null);
    }

    @GetMapping
    public Result<List<InventoryOutDTO>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(inventoryOutService.listByDateRange(startDate, endDate));
    }

    @GetMapping("/by-date")
    public Result<List<InventoryOutDTO>> listByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(inventoryOutService.listByDate(date));
    }
}
