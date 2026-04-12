package com.inventory.enumEntity;

public enum SyncStatus {
    SUCCESS("成功"),
    FAILED("失败"),
    PENDING("处理中"),
    RETRYING("重试中");

    private final String description;

    SyncStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
