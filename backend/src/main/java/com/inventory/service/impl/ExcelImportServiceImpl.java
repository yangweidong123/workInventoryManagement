package com.inventory.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.inventory.dto.ImportResultDTO;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryImage;
import com.inventory.mapper.InventoryImageMapper;
import com.inventory.mapper.InventoryMapper;
import com.inventory.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventoryImageMapper inventoryImageMapper;

    @Override
    @Transactional
    public ImportResultDTO importExcel(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        List<ImportResultDTO.ImportErrorDTO> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try {
            List<ImportData> dataList = EasyExcel.read(file.getInputStream())
                    .head(ImportData.class)
                    .sheet()
                    .doReadSync();

            for (int i = 0; i < dataList.size(); i++) {
                ImportData data = dataList.get(i);
                int rowNum = i + 2;

                try {
                    if (data.getStyleNo() == null || data.getStyleNo().trim().isEmpty()) {
                        addError(errors, rowNum, "款号不能为空");
                        failCount++;
                        continue;
                    }

                    LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(Inventory::getStyleNo, data.getStyleNo().trim());
                    if (inventoryMapper.selectCount(wrapper).intValue() > 0) {
                        addError(errors, rowNum, "款号 " + data.getStyleNo() + " 已存在");
                        failCount++;
                        continue;
                    }

                    Inventory inventory = new Inventory();
                    inventory.setStyleNo(data.getStyleNo().trim());
                    inventory.setName(data.getName() != null ? data.getName().trim() : "");
                    inventory.setSizeMm(data.getSizeMm());
                    inventory.setWeightKg(data.getWeightKg());
                    inventory.setBoxSpec(data.getBoxSpec() != null ? data.getBoxSpec() : 1);
                    inventory.setPriceExclTax(data.getPriceExclTax() != null ? data.getPriceExclTax() : BigDecimal.ZERO);
                    inventory.setGuidePrice(data.getGuidePrice() != null ? data.getGuidePrice() : BigDecimal.ZERO);
                    inventory.setMinPrice(data.getMinPrice() != null ? data.getMinPrice() : BigDecimal.ZERO);
                    inventory.setQuantity(data.getQuantity() != null ? data.getQuantity() : 0);
                    inventoryMapper.insert(inventory);

                    Long inventoryId = inventory.getId();
                    saveImages(inventoryId, data, rowNum);

                    successCount++;
                } catch (Exception e) {
                    addError(errors, rowNum, e.getMessage());
                    failCount++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("读取Excel文件失败");
        }

        result.setSuccessCount(successCount);
        result.setFailCount(failCount);
        result.setErrors(errors);
        return result;
    }

    @Override
    public byte[] generateTemplate() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        EasyExcel.write(out)
                .head(ImportData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("商品导入模板")
                .doWrite(new ArrayList<>());
        return out.toByteArray();
    }

    private void saveImages(Long inventoryId, ImportData data, int rowNum) {
        if (data.getImage1() != null && !data.getImage1().isEmpty()) {
            saveImage(inventoryId, data.getImage1(), 1);
        }
        if (data.getImage2() != null && !data.getImage2().isEmpty()) {
            saveImage(inventoryId, data.getImage2(), 2);
        }
        if (data.getImage3() != null && !data.getImage3().isEmpty()) {
            saveImage(inventoryId, data.getImage3(), 3);
        }
    }

    private void saveImage(Long inventoryId, String base64Data, int sort) {
        if (base64Data.startsWith("data:")) {
            InventoryImage image = new InventoryImage();
            image.setInventoryId(inventoryId);
            image.setImageUrl(base64Data);
            image.setSort(sort);
            inventoryImageMapper.insert(image);
        }
    }

    private void addError(List<ImportResultDTO.ImportErrorDTO> errors, int row, String message) {
        ImportResultDTO.ImportErrorDTO error = new ImportResultDTO.ImportErrorDTO();
        error.setRow(row);
        error.setMessage(message);
        errors.add(error);
    }

    public static class ImportData {
        private String styleNo;
        private String name;
        private String sizeMm;
        private BigDecimal weightKg;
        private Integer boxSpec;
        private BigDecimal priceExclTax;
        private BigDecimal guidePrice;
        private BigDecimal minPrice;
        private Integer quantity;
        private String image1;
        private String image2;
        private String image3;

        public String getStyleNo() { return styleNo; }
        public void setStyleNo(String styleNo) { this.styleNo = styleNo; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSizeMm() { return sizeMm; }
        public void setSizeMm(String sizeMm) { this.sizeMm = sizeMm; }
        public BigDecimal getWeightKg() { return weightKg; }
        public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
        public Integer getBoxSpec() { return boxSpec; }
        public void setBoxSpec(Integer boxSpec) { this.boxSpec = boxSpec; }
        public BigDecimal getPriceExclTax() { return priceExclTax; }
        public void setPriceExclTax(BigDecimal priceExclTax) { this.priceExclTax = priceExclTax; }
        public BigDecimal getGuidePrice() { return guidePrice; }
        public void setGuidePrice(BigDecimal guidePrice) { this.guidePrice = guidePrice; }
        public BigDecimal getMinPrice() { return minPrice; }
        public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getImage1() { return image1; }
        public void setImage1(String image1) { this.image1 = image1; }
        public String getImage2() { return image2; }
        public void setImage2(String image2) { this.image2 = image2; }
        public String getImage3() { return image3; }
        public void setImage3(String image3) { this.image3 = image3; }
    }
}
