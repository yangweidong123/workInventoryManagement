package com.inventory.service;

import com.inventory.entity.SkuMapping;

import java.util.List;

public interface SkuMappingService {

    SkuMapping getById(Long id);

    SkuMapping createMapping(Long inventoryId, String platform, String platformSku, String platformProductId);

    void updateMapping(Long id, SkuMapping mapping);

    void deleteMapping(Long id);

    SkuMapping getByPlatformSku(String platform, String platformSku);

    List<SkuMapping> getByInventoryId(Long inventoryId);

    List<SkuMapping> getByPlatform(String platform);

    void updateSyncInfo(Long id, Integer quantity);
}
