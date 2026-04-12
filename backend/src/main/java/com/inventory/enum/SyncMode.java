package com.inventory.enum;

public enum SyncMode {
    IMMEDIATE("即时同步"),
    SCHEDULED("定时同步");

    private final String description;

    SyncMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
