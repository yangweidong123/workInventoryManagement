package com.inventory.adapter;

import com.inventory.entity.PlatformConfig;
import com.inventory.mapper.PlatformConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPlatformAdapter implements PlatformAdapter {

    @Autowired
    protected PlatformConfigMapper platformConfigMapper;

    protected PlatformConfig getConfig() {
        return platformConfigMapper.selectByPlatform(getPlatformType());
    }

    protected boolean isEnabled() {
        PlatformConfig config = getConfig();
        return config != null && config.getEnabled() == 1;
    }
}
