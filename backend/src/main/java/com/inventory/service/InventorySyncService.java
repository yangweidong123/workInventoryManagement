package com.inventory.service;

import com.inventory.dto.SyncResult;
import com.inventory.entity.SkuMapping;

import java.util.List;

public interface InventorySyncService {

    void syncToAllPlatforms(Long inventoryId, Integer newQuantity);

    SyncResult syncToPlatform(SkuMapping mapping, Integer quantity);

    void batchSync(List<Long> inventoryIds);
}
