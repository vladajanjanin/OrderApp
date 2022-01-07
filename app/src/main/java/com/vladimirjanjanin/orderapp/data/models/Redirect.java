package com.vladimirjanjanin.orderapp.data.models;

public class Redirect {
    private String NotifyUrl;
    private String ReturnUrl;

    public Redirect() {
    }

    // Getter Methods

    public String getNotifyUrl() {
        return NotifyUrl;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    // Setter Methods

    public void setNotifyUrl(String NotifyUrl) {
        this.NotifyUrl = NotifyUrl;
    }

    public void setReturnUrl(String ReturnUrl) {
        this.ReturnUrl = ReturnUrl;
    }

    public String toXml() {
        return "            <ns1:Redirect>" +
                "               <ns1:NotifyUrl>" + getNotifyUrl() + "</ns1:NotifyUrl>" +
                "               <ns1:ReturnUrl>" + getReturnUrl() + "</ns1:ReturnUrl>" +
                "            </ns1:Redirect>";
    }
}
