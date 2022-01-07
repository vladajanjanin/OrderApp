package com.vladimirjanjanin.orderapp.data.dtos;

import com.vladimirjanjanin.orderapp.data.models.User;

public class LoginResponse {
    private String token;
    private User flattenUser;

    public LoginResponse() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getFlattenUser() {
        return flattenUser;
    }

    public void setFlattenUser(User user) {
        this.flattenUser = user;
    }
}
