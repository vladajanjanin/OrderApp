package com.vladimirjanjanin.orderapp.data.models;

public class UpdateFcmTokenBody {
    private String userId;
    private String token;

    public UpdateFcmTokenBody() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
