package com.inventory.service;

import com.inventory.dto.DailyStatsDTO;
import com.inventory.dto.DailyStatsQuery;
import java.util.List;

public interface DailyStatsService {

    List<DailyStatsDTO> getStats(DailyStatsQuery query);
}
