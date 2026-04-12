package com.inventory.service.impl;

import com.inventory.dto.StandardOrder;
import com.inventory.entity.Inventory;
import com.inventory.entity.PlatformOrder;
import com.inventory.entity.SkuMapping;
import com.inventory.entity.SyncLog;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.PlatformOrderMapper;
import com.inventory.service.InventorySyncService;
import com.inventory.service.OrderProcessService;
import com.inventory.service.SkuMappingService;
import com.inventory.service.SyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderProcessServiceImpl implements OrderProcessService {

    @Autowired
    private SkuMappingService skuMappingService;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private PlatformOrderMapper platformOrderMapper;

    @Autowired
    private SyncLogService syncLogService;

    @Autowired
    private InventorySyncService inventorySyncService;

    @Override
    @Transactional
    public void processOrder(StandardOrder order) {
        PlatformOrder existingOrder = platformOrderMapper.selectByPlatformAndOrderId(order.getPlatform(), order.getOrderId());
        if (existingOrder != null && existingOrder.getProcessed() == 1) {
            return;
        }

        SkuMapping mapping = skuMappingService.getByPlatformSku(order.getPlatform(), order.getPlatformSku());
        if (mapping == null) {
            createSyncLog(order, null, "FAILED", "SKU未映射");
            return;
        }

        Inventory inventory = inventoryMapper.selectById(mapping.getInventoryId());
        if (inventory == null) {
            createSyncLog(order, mapping, "FAILED", "系统商品不存在");
            return;
        }

        int quantityBefore = inventory.getQuantity();
        int quantityChange = order.getQuantity();
        int quantityAfter;

        try {
            if ("REFUND".equals(order.getType())) {
                quantityAfter = quantityBefore + quantityChange;
            } else {
                if (quantityBefore < quantityChange) {
                    createSyncLog(order, mapping, "FAILED", "库存不足");
                    return;
                }
                quantityAfter = quantityBefore - quantityChange;
            }

            inventory.setQuantity(quantityAfter);
            inventoryMapper.updateById(inventory);

            saveOrUpdatePlatformOrder(order, mapping);

            createSyncLog(order, mapping, "SUCCESS", null);

            inventorySyncService.syncToAllPlatforms(mapping.getInventoryId(), quantityAfter);

        } catch (Exception e) {
            createSyncLog(order, mapping, "FAILED", e.getMessage());
            throw e;
        }
    }

    private void saveOrUpdatePlatformOrder(StandardOrder order, SkuMapping mapping) {
        PlatformOrder platformOrder = platformOrderMapper.selectByPlatformAndOrderId(order.getPlatform(), order.getOrderId());
        if (platformOrder == null) {
            platformOrder = new PlatformOrder();
            platformOrder.setPlatform(order.getPlatform());
            platformOrder.setOrderId(order.getOrderId());
            platformOrder.setSkuMappingId(mapping.getId());
            platformOrder.setOrderType(order.getType());
            platformOrder.setQuantity(order.getQuantity());
            platformOrder.setAmount(order.getAmount());
            platformOrder.setStatus(order.getStatus());
            platformOrder.setPaidTime(order.getPaidTime());
            platformOrder.setRefundTime(order.getRefundTime());
            platformOrder.setProcessed(1);
            platformOrder.setRawData(order.getRawData());
            platformOrderMapper.insert(platformOrder);
        } else {
            platformOrder.setProcessed(1);
            platformOrderMapper.updateById(platformOrder);
        }
    }

    private void createSyncLog(StandardOrder order, SkuMapping mapping, String status, String errorMessage) {
        SyncLog log = new SyncLog();
        log.setPlatform(order.getPlatform());
        log.setOrderId(order.getOrderId());
        if (mapping != null) {
            log.setInventoryId(mapping.getInventoryId());
            log.setPlatformSku(mapping.getPlatformSku());
        }
        log.setOperationType("SALE_OUT");
        log.setSyncStatus(status);
        log.setErrorMessage(errorMessage);
        log.setRawRequest(order.getRawData());
        syncLogService.createLog(log);
    }
}
