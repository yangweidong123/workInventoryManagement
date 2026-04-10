package com.inventory.service;

import com.inventory.dto.InventoryImageDTO;

public interface ImageService {

    InventoryImageDTO upload(Long inventoryId, byte[] fileData, String fileName);

    void delete(Long inventoryId, Long imageId);

    void setCover(Long inventoryId, Long imageId);
}
