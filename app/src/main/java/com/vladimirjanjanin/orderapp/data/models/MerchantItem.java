package com.vladimirjanjanin.orderapp.data.models;

public class MerchantItem {
    private String UserId;
    private InventoryItem InventoryItem;
    private double Quantity;
    private int orderQuantity = 0;

    public MerchantItem() {
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public com.vladimirjanjanin.orderapp.data.models.InventoryItem getInventoryItem() {
        return InventoryItem;
    }

    public void setInventoryItem(com.vladimirjanjanin.orderapp.data.models.InventoryItem inventoryItem) {
        InventoryItem = inventoryItem;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
}
