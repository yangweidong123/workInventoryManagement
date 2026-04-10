package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inventory.dto.InventoryImageDTO;
import com.inventory.entity.InventoryImage;
import com.inventory.mapper.InventoryImageMapper;
import com.inventory.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private InventoryImageMapper imageMapper;

    @Override
    public InventoryImageDTO upload(Long inventoryId, byte[] fileData, String fileName) {
        String imageUrl = "data:image/" + getExt(fileName) + ";base64," + java.util.Base64.getEncoder().encodeToString(fileData);

        int maxSort = 0;
        List<InventoryImage> existing = imageMapper.selectByInventoryId(inventoryId);
        if (existing != null && !existing.isEmpty()) {
            maxSort = existing.stream().mapToInt(InventoryImage::getSort).max().orElse(0);
        }

        InventoryImage image = new InventoryImage();
        image.setInventoryId(inventoryId);
        image.setImageUrl(imageUrl);
        image.setSort(maxSort + 1);
        imageMapper.insert(image);

        InventoryImageDTO dto = new InventoryImageDTO();
        dto.setId(image.getId());
        dto.setInventoryId(image.getInventoryId());
        dto.setImageUrl(image.getImageUrl());
        dto.setSort(image.getSort());
        return dto;
    }

    @Override
    @Transactional
    public void delete(Long inventoryId, Long imageId) {
        imageMapper.deleteById(imageId);

        List<InventoryImage> remaining = imageMapper.selectByInventoryId(inventoryId);
        for (int i = 0; i < remaining.size(); i++) {
            InventoryImage img = remaining.get(i);
            if (img.getSort() != i + 1) {
                img.setSort(i + 1);
                imageMapper.updateById(img);
            }
        }
    }

    @Override
    @Transactional
    public void setCover(Long inventoryId, Long imageId) {
        InventoryImage targetImage = imageMapper.selectById(imageId);
        if (targetImage == null || !targetImage.getInventoryId().equals(inventoryId)) {
            throw new RuntimeException("图片不存在");
        }

        List<InventoryImage> images = imageMapper.selectByInventoryId(inventoryId);
        int targetSort = targetImage.getSort();

        for (InventoryImage img : images) {
            if (img.getId().equals(imageId)) {
                img.setSort(0);
            } else if (img.getSort() < targetSort) {
                img.setSort(img.getSort() + 1);
            }
            imageMapper.updateById(img);
        }
    }

    private String getExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpeg";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
