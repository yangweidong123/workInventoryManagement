package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("platform_config")
public class PlatformConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String platform;

    private String shopName;

    private String appKey;

    private String appSecret;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime tokenExpireTime;

    private String webhookUrl;

    private Integer enabled;

    private String syncMode;

    private Integer syncInterval;

    private LocalDateTime lastSyncTime;

    private LocalDateTime lastHeartbeat;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
