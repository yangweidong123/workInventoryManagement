package com.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImportResultDTO {

    private Integer successCount;

    private Integer failCount;

    private List<ImportErrorDTO> errors;

    @Data
    public static class ImportErrorDTO {
        private Integer row;
        private String message;
    }
}
