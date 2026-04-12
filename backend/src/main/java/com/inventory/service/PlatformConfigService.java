package com.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.PlatformCredential;
import com.inventory.entity.PlatformConfig;

import java.util.List;

public interface PlatformConfigService {

    IPage<PlatformConfig> page(int current, int size);

    List<PlatformConfig> list();

    PlatformConfig getById(Long id);

    PlatformConfig getByPlatform(String platform);

    Long create(PlatformConfig config);

    void update(Long id, PlatformConfig config);

    void delete(Long id);

    boolean testConnection(PlatformConfig config);

    void enable(Long id);

    void disable(Long id);

    void updateHeartbeat(String platform);
}
