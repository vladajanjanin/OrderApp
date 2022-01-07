package com.vladimirjanjanin.orderapp.data.dtos;

import com.vladimirjanjanin.orderapp.data.models.InventoryTransaction;

import java.util.ArrayList;
import java.util.List;

public class MerchantTransactionBody {
    private String merchantId;
    private String timestamp;
    private double totalPrice;
    private List<InventoryTransaction> transactionItems = new ArrayList<>();

    public MerchantTransactionBody() {
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<InventoryTransaction> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(List<InventoryTransaction> transactionItems) {
        this.transactionItems = transactionItems;
    }
}
