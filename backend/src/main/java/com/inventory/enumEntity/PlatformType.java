package com.inventory.enumEntity;

public enum PlatformType {
    XIAOHONGSHU("小红书"),
    DOUYIN("抖音"),
    KUAISHOU("快手"),
    JD("京东"),
    TAOBAO("淘宝");

    private final String description;

    PlatformType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
