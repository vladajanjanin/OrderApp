package com.vladimirjanjanin.orderapp.data.models;

public class Category {
    private String Name;
    private String Id;

    public Category() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
