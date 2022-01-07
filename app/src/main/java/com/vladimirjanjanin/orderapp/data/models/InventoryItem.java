package com.vladimirjanjanin.orderapp.data.models;

public class InventoryItem {
    private String Name;
    private Category Category;
    private double Price;
    private String Icon;
    private String Id;

    public InventoryItem() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public com.vladimirjanjanin.orderapp.data.models.Category getCategory() {
        return Category;
    }

    public void setCategory(com.vladimirjanjanin.orderapp.data.models.Category category) {
        Category = category;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
