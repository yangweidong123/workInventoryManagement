package com.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.inventory.dto.InventoryDTO;
import com.inventory.dto.InventoryQuery;
import com.inventory.dto.InventoryImageDTO;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryImage;
import com.inventory.mapper.InventoryImageMapper;
import com.inventory.mapper.InventoryMapper;
import com.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventoryImageMapper inventoryImageMapper;

    @Override
    public IPage<InventoryDTO> page(InventoryQuery query) {
        Page<InventoryDTO> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<InventoryDTO> result = inventoryMapper.selectPageList(page, query);
        for (InventoryDTO record : result.getRecords()) {
            fillImages(record);
        }
        return result;
    }

    @Override
    public InventoryDTO getById(Long id) {
        InventoryDTO dto = inventoryMapper.selectDetailById(id);
        if (dto != null) {
            fillImages(dto);
        }
        return dto;
    }

    @Override
    @Transactional
    public Long create(InventoryDTO dto) {
        Inventory entity = new Inventory();
        entity.setStyleNo(dto.getStyleNo());
        entity.setName(dto.getName());
        entity.setSizeMm(dto.getSizeMm());
        entity.setWeightKg(dto.getWeightKg());
        entity.setBoxSpec(dto.getBoxSpec() != null ? dto.getBoxSpec() : 1);
        entity.setPriceExclTax(dto.getPriceExclTax());
        entity.setGuidePrice(dto.getGuidePrice());
        entity.setMinPrice(dto.getMinPrice());
        entity.setPackagePrice(dto.getPackagePrice());
        entity.setQuantity(dto.getQuantity());
        inventoryMapper.insert(entity);

        saveImages(entity.getId(), dto.getImages());
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, InventoryDTO dto) {
        Inventory entity = inventoryMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("商品不存在");
        }

        entity.setName(dto.getName());
        entity.setSizeMm(dto.getSizeMm());
        entity.setWeightKg(dto.getWeightKg());
        entity.setBoxSpec(dto.getBoxSpec());
        entity.setPriceExclTax(dto.getPriceExclTax());
        entity.setGuidePrice(dto.getGuidePrice());
        entity.setMinPrice(dto.getMinPrice());
        entity.setPackagePrice(dto.getPackagePrice());
        entity.setQuantity(dto.getQuantity());
        inventoryMapper.updateById(entity);

        inventoryImageMapper.deleteByInventoryId(id);
        saveImages(id, dto.getImages());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inventoryImageMapper.deleteByInventoryId(id);
        inventoryMapper.deleteById(id);
    }

    private void fillImages(InventoryDTO dto) {
        List<InventoryImage> images = inventoryImageMapper.selectByInventoryId(dto.getId());
        dto.setImages(images.stream().map(img -> {
            InventoryImageDTO imgDto = new InventoryImageDTO();
            imgDto.setId(img.getId());
            imgDto.setInventoryId(img.getInventoryId());
            imgDto.setImageUrl(img.getImageUrl());
            imgDto.setSort(img.getSort());
            return imgDto;
        }).collect(Collectors.toList()));
    }

    private void saveImages(Long inventoryId, List<InventoryImageDTO> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        for (int i = 0; i < images.size(); i++) {
            InventoryImageDTO imgDto = images.get(i);
            InventoryImage img = new InventoryImage();
            img.setInventoryId(inventoryId);
            img.setImageUrl(imgDto.getImageUrl());
            img.setSort(imgDto.getSort() != null ? imgDto.getSort() : i);
            inventoryImageMapper.insert(img);
        }
    }
}
