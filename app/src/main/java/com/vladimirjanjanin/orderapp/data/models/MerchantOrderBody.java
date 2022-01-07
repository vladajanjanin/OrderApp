package com.vladimirjanjanin.orderapp.data.models;

import java.util.List;

public class MerchantOrderBody {
    private String                  created;
    private String                  merchantId;
    private List<OrderItem>         orderItems;

    public MerchantOrderBody() {
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
