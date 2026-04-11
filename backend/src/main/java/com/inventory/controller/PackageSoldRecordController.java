package com.inventory.controller;

import com.inventory.dto.PackageSoldRecordDTO;
import com.inventory.dto.Result;
import com.inventory.service.PackageSoldRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package-sold-record")
public class PackageSoldRecordController {

    @Autowired
    private PackageSoldRecordService packageSoldRecordService;

    @GetMapping
    public Result<List<PackageSoldRecordDTO>> list(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(packageSoldRecordService.listByDateRange(startDate, endDate));
    }
}
