package com.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.AuditLogQuery;
import com.inventory.entity.AuditLog;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogMapper extends BaseMapper<AuditLog> {

    IPage<AuditLog> selectPageList(Page<?> page, @Param("query") AuditLogQuery query);

    List<AuditLog> selectByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
