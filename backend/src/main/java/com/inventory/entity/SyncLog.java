package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sync_log")
public class SyncLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String platform;

    private String orderId;

    private Long inventoryId;

    private String platformSku;

    private String operationType;

    private Integer quantityChange;

    private Integer quantityBefore;

    private Integer quantityAfter;

    private String apiRequestId;

    private String syncStatus;

    private String errorMessage;

    private String rawRequest;

    private LocalDateTime createTime;
}
