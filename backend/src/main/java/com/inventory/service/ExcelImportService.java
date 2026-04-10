package com.inventory.service;

import com.inventory.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelImportService {

    ImportResultDTO importExcel(MultipartFile file);

    byte[] generateTemplate();
}
