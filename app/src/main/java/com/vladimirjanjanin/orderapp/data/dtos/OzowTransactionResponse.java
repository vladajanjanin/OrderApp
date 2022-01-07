package com.vladimirjanjanin.orderapp.data.dtos;

import com.vladimirjanjanin.orderapp.data.models.OzowTransaction;

import java.util.List;

public class OzowTransactionResponse {
    private List<OzowTransaction> transactions;

    public OzowTransactionResponse() {
    }

    public List<OzowTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<OzowTransaction> transactions) {
        this.transactions = transactions;
    }
}
