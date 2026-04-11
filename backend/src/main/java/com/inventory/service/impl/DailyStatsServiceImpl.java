package com.inventory.service.impl;

import com.inventory.dto.DailyStatsDTO;
import com.inventory.dto.DailyStatsQuery;
import com.inventory.entity.DailyStats;
import com.inventory.mapper.DailyStatsMapper;
import com.inventory.service.DailyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyStatsServiceImpl implements DailyStatsService {

    @Autowired
    private DailyStatsMapper dailyStatsMapper;

    @Override
    public List<DailyStatsDTO> getStats(DailyStatsQuery query) {
        List<DailyStats> list = dailyStatsMapper.selectByDateRange(query.getStartDate(), query.getEndDate());
        return list.stream().map(stats -> {
            DailyStatsDTO dto = new DailyStatsDTO();
            dto.setStatDate(stats.getStatDate());
            dto.setInventoryInCount(stats.getInventoryInCount());
            dto.setInventoryInAmount(stats.getInventoryInAmount());
            dto.setInventoryOutCount(stats.getInventoryOutCount());
            dto.setInventoryOutAmount(stats.getInventoryOutAmount());
            dto.setPackageSoldCount(stats.getPackageSoldCount());
            dto.setPackageSoldAmount(stats.getPackageSoldAmount());
            return dto;
        }).collect(Collectors.toList());
    }
}
