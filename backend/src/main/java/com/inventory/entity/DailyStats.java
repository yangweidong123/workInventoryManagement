package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_stats")
public class DailyStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate statDate;

    private Integer inventoryInCount;

    private BigDecimal inventoryInAmount;

    private Integer inventoryOutCount;

    private BigDecimal inventoryOutAmount;

    private Integer packageSoldCount;

    private BigDecimal packageSoldAmount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
