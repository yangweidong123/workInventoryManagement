package com.inventory.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.inventory.dto.InventoryDTO;
import com.inventory.dto.PackageDTO;
import com.inventory.entity.Inventory;
import com.inventory.entity.Package;
import com.inventory.mapper.InventoryMapper;
import com.inventory.mapper.PackageMapper;
import com.inventory.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private PackageMapper packageMapper;

    @Override
    public void exportInventory(HttpServletResponse response, List<Long> ids) throws Exception {
        List<InventoryExportData> dataList = new ArrayList<>();

        List<Inventory> inventories;
        if (ids == null || ids.isEmpty()) {
            inventories = inventoryMapper.selectList(null);
        } else {
            inventories = inventoryMapper.selectBatchIds(ids);
        }

        for (Inventory inv : inventories) {
            InventoryExportData data = new InventoryExportData();
            data.setStyleNo(inv.getStyleNo());
            data.setName(inv.getName());
            data.setSizeMm(inv.getSizeMm());
            data.setWeightKg(inv.getWeightKg());
            data.setBoxSpec(inv.getBoxSpec());
            data.setPriceExclTax(inv.getPriceExclTax());
            data.setGuidePrice(inv.getGuidePrice());
            data.setMinPrice(inv.getMinPrice());
            data.setQuantity(inv.getQuantity());
            data.setGoodsValue(inv.getPriceExclTax().multiply(new BigDecimal(inv.getQuantity())));
            dataList.add(data);
        }

        String fileName = URLEncoder.encode("商品列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), InventoryExportData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("商品列表")
                .doWrite(dataList);
    }

    @Override
    public void exportPackage(HttpServletResponse response, List<Long> ids) throws Exception {
        List<PackageExportData> dataList = new ArrayList<>();

        List<Package> packages;
        if (ids == null || ids.isEmpty()) {
            packages = packageMapper.selectList(null);
        } else {
            packages = packageMapper.selectBatchIds(ids);
        }

        for (Package pkg : packages) {
            PackageExportData data = new PackageExportData();
            data.setName(pkg.getName());
            data.setTotalPrice(pkg.getTotalPrice());
            data.setCostPrice(pkg.getCostPrice());
            data.setProfitRate(pkg.getProfitRate());
            dataList.add(data);
        }

        String fileName = URLEncoder.encode("套餐列表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), PackageExportData.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("套餐列表")
                .doWrite(dataList);
    }

    public static class InventoryExportData {
        private String styleNo;
        private String name;
        private String sizeMm;
        private BigDecimal weightKg;
        private Integer boxSpec;
        private BigDecimal priceExclTax;
        private BigDecimal guidePrice;
        private BigDecimal minPrice;
        private Integer quantity;
        private BigDecimal goodsValue;

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
        public BigDecimal getGoodsValue() { return goodsValue; }
        public void setGoodsValue(BigDecimal goodsValue) { this.goodsValue = goodsValue; }
    }

    public static class PackageExportData {
        private String name;
        private BigDecimal totalPrice;
        private BigDecimal costPrice;
        private BigDecimal profitRate;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
        public BigDecimal getCostPrice() { return costPrice; }
        public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
        public BigDecimal getProfitRate() { return profitRate; }
        public void setProfitRate(BigDecimal profitRate) { this.profitRate = profitRate; }
    }
}
