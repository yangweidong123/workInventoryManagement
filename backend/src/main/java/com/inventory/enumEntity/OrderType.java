package com.inventory.enumEntity;

public enum OrderType {
    SALE("销售订单"),
    REFUND("退货订单");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
