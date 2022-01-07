package com.vladimirjanjanin.orderapp.data.models;

public class Identity {
    private String type = "number";
    private String endpoint;

    public Identity() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
