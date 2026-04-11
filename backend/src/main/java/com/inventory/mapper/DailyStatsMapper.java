package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.inventory.entity.DailyStats;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

public interface DailyStatsMapper extends BaseMapper<DailyStats> {

    List<DailyStats> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    DailyStats selectByDate(@Param("statDate") LocalDate statDate);
}
