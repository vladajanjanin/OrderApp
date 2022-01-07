package com.vladimirjanjanin.orderapp.data.dtos;

import com.vladimirjanjanin.orderapp.data.models.User;

public class RegisterResponse {
    private int code;
    private User user;

    public RegisterResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
