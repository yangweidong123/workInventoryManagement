package com.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.inventory.dto.*;
import com.inventory.service.ExcelImportService;
import com.inventory.service.ImageService;
import com.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ExcelImportService excelImportService;

    @GetMapping
    public Result<IPage<InventoryDTO>> list(InventoryQuery query) {
        return Result.success(inventoryService.page(query));
    }

    @GetMapping("/{id}")
    public Result<InventoryDTO> getById(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    @PostMapping
    public Result<Long> create(@RequestBody InventoryDTO dto) {
        return Result.success(inventoryService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody InventoryDTO dto) {
        inventoryService.update(id, dto);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return Result.success("删除成功", null);
    }

    @PostMapping("/import")
    public Result<ImportResultDTO> importExcel(@RequestParam("file") MultipartFile file) {
        ImportResultDTO result = excelImportService.importExcel(file);
        String message = "导入成功 " + result.getSuccessCount() + " 条，失败 " + result.getFailCount() + " 条";
        return Result.success(message, result);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = excelImportService.generateTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory_template.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(template);
    }

    @PostMapping("/{inventoryId}/images")
    public Result<InventoryImageDTO> uploadImage(
            @PathVariable Long inventoryId,
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            InventoryImageDTO result = imageService.upload(inventoryId, fileData, file.getOriginalFilename());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{inventoryId}/images/{imageId}")
    public Result<Void> deleteImage(
            @PathVariable Long inventoryId,
            @PathVariable Long imageId) {
        imageService.delete(inventoryId, imageId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/{inventoryId}/images/{imageId}/cover")
    public Result<Void> setCover(
            @PathVariable Long inventoryId,
            @PathVariable Long imageId) {
        imageService.setCover(inventoryId, imageId);
        return Result.success("设置成功", null);
    }
}
