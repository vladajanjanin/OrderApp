package com.vladimirjanjanin.orderapp.data.dtos;

import com.vladimirjanjanin.orderapp.utils.Utils;

public class OzowPaymentRequestBody {
    private String SiteCode = "CYC-CYC-003";
    private String CountryCode = "ZA";
    private String CurrencyCode = "ZAR";
    private double Amount = 0.0;
    private String TransactionReference  = "";
    private String BankReference = "";
    private boolean IsTest = true;
    private String HashCheck = "";

    private String ozowKey = "7971aced500146489d5386c27aa8c4ea";

    public OzowPaymentRequestBody() {
    }

    public String getSiteCode() {
        return SiteCode;
    }

    public void setSiteCode(String siteCode) {
        SiteCode = siteCode;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getTransactionReference() {
        return TransactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        TransactionReference = transactionReference;
    }

    public String getBankReference() {
        return BankReference;
    }

    public void setBankReference(String bankReference) {
        BankReference = bankReference;
    }

    public boolean isTest() {
        return IsTest;
    }

    public void setTest(boolean test) {
        IsTest = test;
    }

    public String getHashCheck() {
        return HashCheck;
    }

    public void setHashCheck(String hashCheck) {
        HashCheck = hashCheck;
    }

    public String getOzowKey() {
        return ozowKey;
    }

    public void setOzowKey(String ozowKey) {
        this.ozowKey = ozowKey;
    }

    public void generateHash() {
        String amountString = String.valueOf(Utils.roundToTwoDecimal(Amount));

        String concatenatedString = SiteCode + CountryCode + CurrencyCode + amountString + TransactionReference + BankReference + IsTest + ozowKey;
        concatenatedString = concatenatedString.toLowerCase();

        HashCheck =  Utils.getSha512(concatenatedString);
    }
}
