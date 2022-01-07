package com.vladimirjanjanin.orderapp.data.dtos;

public class LoginBody {
    private String email;
    private String password;

    public LoginBody(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
