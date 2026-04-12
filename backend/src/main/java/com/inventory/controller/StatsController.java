package com.inventory.controller;

import com.inventory.dto.DailyStatsDTO;
import com.inventory.dto.DailyStatsQuery;
import com.inventory.dto.Result;
import com.inventory.service.DailyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private DailyStatsService dailyStatsService;

    @GetMapping
    public Result<List<DailyStatsDTO>> getStats(DailyStatsQuery query) {
        return Result.success(dailyStatsService.getStats(query));
    }
}
