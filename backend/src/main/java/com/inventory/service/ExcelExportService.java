package com.inventory.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ExcelExportService {

    void exportInventory(HttpServletResponse response, List<Long> ids) throws Exception;

    void exportPackage(HttpServletResponse response, List<Long> ids) throws Exception;
}
