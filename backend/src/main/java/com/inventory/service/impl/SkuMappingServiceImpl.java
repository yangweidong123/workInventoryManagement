package com.inventory.service.impl;

import com.inventory.entity.SkuMapping;
import com.inventory.mapper.SkuMappingMapper;
import com.inventory.service.SkuMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SkuMappingServiceImpl implements SkuMappingService {

    @Autowired
    private SkuMappingMapper skuMappingMapper;

    @Override
    public SkuMapping getById(Long id) {
        return skuMappingMapper.selectById(id);
    }

    @Override
    public SkuMapping createMapping(Long inventoryId, String platform, String platformSku, String platformProductId) {
        SkuMapping mapping = new SkuMapping();
        mapping.setInventoryId(inventoryId);
        mapping.setPlatform(platform);
        mapping.setPlatformSku(platformSku);
        mapping.setPlatformProductId(platformProductId);
        mapping.setLastSyncQuantity(0);
        skuMappingMapper.insert(mapping);
        return mapping;
    }

    @Override
    public void updateMapping(Long id, SkuMapping mapping) {
        mapping.setId(id);
        skuMappingMapper.updateById(mapping);
    }

    @Override
    public void deleteMapping(Long id) {
        skuMappingMapper.deleteById(id);
    }

    @Override
    public SkuMapping getByPlatformSku(String platform, String platformSku) {
        return skuMappingMapper.selectByPlatformAndSku(platform, platformSku);
    }

    @Override
    public List<SkuMapping> getByInventoryId(Long inventoryId) {
        return skuMappingMapper.selectByInventoryId(inventoryId);
    }

    @Override
    public List<SkuMapping> getByPlatform(String platform) {
        return skuMappingMapper.selectByPlatform(platform);
    }

    @Override
    public void updateSyncInfo(Long id, Integer quantity) {
        SkuMapping mapping = skuMappingMapper.selectById(id);
        if (mapping != null) {
            mapping.setLastSyncQuantity(quantity);
            mapping.setLastSyncTime(LocalDateTime.now());
            skuMappingMapper.updateById(mapping);
        }
    }
}
