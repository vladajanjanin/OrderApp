package com.vladimirjanjanin.orderapp.data.models;

public class SmsVerificationRequestBody {
    private Identity            identity;
    private String              method = "sms";

    public SmsVerificationRequestBody() {
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
