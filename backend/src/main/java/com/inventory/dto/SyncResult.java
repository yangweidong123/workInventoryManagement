package com.inventory.dto;

import lombok.Data;

@Data
public class SyncResult {
    private boolean success;
    private String message;
    private String apiRequestId;

    public static SyncResult success(String apiRequestId) {
        SyncResult result = new SyncResult();
        result.setSuccess(true);
        result.setMessage("同步成功");
        result.setApiRequestId(apiRequestId);
        return result;
    }

    public static SyncResult failure(String message) {
        SyncResult result = new SyncResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
