package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.entity.PlatformConfig;
import com.inventory.mapper.PlatformConfigMapper;
import com.inventory.service.PlatformConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlatformConfigServiceImpl implements PlatformConfigService {

    @Autowired
    private PlatformConfigMapper platformConfigMapper;

    @Override
    public IPage<PlatformConfig> page(int current, int size) {
        Page<PlatformConfig> page = new Page<>(current, size);
        return platformConfigMapper.selectPage(page, new LambdaQueryWrapper<>());
    }

    @Override
    public List<PlatformConfig> list() {
        return platformConfigMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public PlatformConfig getById(Long id) {
        return platformConfigMapper.selectById(id);
    }

    @Override
    public PlatformConfig getByPlatform(String platform) {
        return platformConfigMapper.selectByPlatform(platform);
    }

    @Override
    public Long create(PlatformConfig config) {
        platformConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    public void update(Long id, PlatformConfig config) {
        config.setId(id);
        platformConfigMapper.updateById(config);
    }

    @Override
    public void delete(Long id) {
        platformConfigMapper.deleteById(id);
    }

    @Override
    public boolean testConnection(PlatformConfig config) {
        return false;
    }

    @Override
    public void enable(Long id) {
        PlatformConfig config = platformConfigMapper.selectById(id);
        if (config != null) {
            config.setEnabled(1);
            config.setStatus("CONNECTED");
            platformConfigMapper.updateById(config);
        }
    }

    @Override
    public void disable(Long id) {
        PlatformConfig config = platformConfigMapper.selectById(id);
        if (config != null) {
            config.setEnabled(0);
            config.setStatus("DISCONNECTED");
            platformConfigMapper.updateById(config);
        }
    }

    @Override
    public void updateHeartbeat(String platform) {
        PlatformConfig config = platformConfigMapper.selectByPlatform(platform);
        if (config != null) {
            config.setLastHeartbeat(LocalDateTime.now());
            platformConfigMapper.updateById(config);
        }
    }
}
