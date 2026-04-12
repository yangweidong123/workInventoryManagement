package com.inventory.service.impl;

import com.inventory.adapter.PlatformAdapter;
import com.inventory.dto.SyncResult;
import com.inventory.entity.Inventory;
import com.inventory.entity.SkuMapping;
import com.inventory.entity.SyncLog;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.SkuMappingMapper;
import com.inventory.service.InventorySyncService;
import com.inventory.service.SkuMappingService;
import com.inventory.service.SyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventorySyncServiceImpl implements InventorySyncService {

    @Autowired
    private SkuMappingService skuMappingService;

    @Autowired
    private SkuMappingMapper skuMappingMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private SyncLogService syncLogService;

    @Autowired
    private Map<String, PlatformAdapter> adapterMap;

    @Override
    public void syncToAllPlatforms(Long inventoryId, Integer newQuantity) {
        List<SkuMapping> mappings = skuMappingService.getByInventoryId(inventoryId);
        for (SkuMapping mapping : mappings) {
            try {
                syncToPlatform(mapping, newQuantity);
            } catch (Exception e) {
                SyncLog log = new SyncLog();
                log.setPlatform(mapping.getPlatform());
                log.setInventoryId(inventoryId);
                log.setPlatformSku(mapping.getPlatformSku());
                log.setOperationType("SYNC");
                log.setQuantityChange(0);
                log.setQuantityBefore(newQuantity);
                log.setQuantityAfter(newQuantity);
                log.setSyncStatus("FAILED");
                log.setErrorMessage(e.getMessage());
                syncLogService.createLog(log);
            }
        }
    }

    @Override
    public SyncResult syncToPlatform(SkuMapping mapping, Integer quantity) {
        PlatformAdapter adapter = adapterMap.get(mapping.getPlatform() + "Adapter");
        if (adapter == null) {
            return SyncResult.failure("未找到平台适配器: " + mapping.getPlatform());
        }

        SyncResult result = adapter.syncInventory(mapping.getPlatformSku(), quantity);

        SkuMapping updateMapping = new SkuMapping();
        updateMapping.setId(mapping.getId());
        updateMapping.setLastSyncQuantity(quantity);
        updateMapping.setLastSyncTime(java.time.LocalDateTime.now());
        skuMappingMapper.updateById(updateMapping);

        SyncLog log = new SyncLog();
        log.setPlatform(mapping.getPlatform());
        log.setInventoryId(mapping.getInventoryId());
        log.setPlatformSku(mapping.getPlatformSku());
        log.setOperationType("SYNC");
        log.setQuantityChange(quantity);
        log.setQuantityBefore(mapping.getLastSyncQuantity() != null ? mapping.getLastSyncQuantity() : 0);
        log.setQuantityAfter(quantity);
        log.setApiRequestId(result.getApiRequestId());
        log.setSyncStatus(result.isSuccess() ? "SUCCESS" : "FAILED");
        log.setErrorMessage(result.isSuccess() ? null : result.getMessage());
        syncLogService.createLog(log);

        return result;
    }

    @Override
    public void batchSync(List<Long> inventoryIds) {
        for (Long inventoryId : inventoryIds) {
            Inventory inventory = inventoryMapper.selectById(inventoryId);
            if (inventory != null) {
                syncToAllPlatforms(inventoryId, inventory.getQuantity());
            }
        }
    }
}
