package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.SyncLogQuery;
import com.inventory.entity.SyncLog;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SyncLogMapper extends BaseMapper<SyncLog> {

    IPage<SyncLog> selectPageList(Page<?> page, @Param("query") SyncLogQuery query);

    List<SyncLog> selectByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
