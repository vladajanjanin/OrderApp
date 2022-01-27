package com.vladimirjanjanin.orderapp.data.models;

public class UpdateCustomItemPriceBody {
    private String itemId;
    private double price;

    public UpdateCustomItemPriceBody() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
