package com.vladimirjanjanin.orderapp.data.models;

public class SmsVerificationBody {
    private String method = "sms";
    private Sms sms;

    public SmsVerificationBody() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Sms getSms() {
        return sms;
    }

    public void setSms(Sms sms) {
        this.sms = sms;
    }
}
