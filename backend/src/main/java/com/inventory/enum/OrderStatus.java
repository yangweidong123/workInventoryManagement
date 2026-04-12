package com.inventory.enum;

public enum OrderStatus {
    PENDING("待处理"),
    PAID("已支付"),
    SHIPPED("已发货"),
    COMPLETED("已完成"),
    REFUNDING("退款中"),
    REFUNDED("已退款");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
