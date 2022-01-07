package com.vladimirjanjanin.orderapp.data.models;

import com.vladimirjanjanin.orderapp.utils.Utils;

public class BillingDetails {
    Customer customer;
    Address address;

    public BillingDetails(Customer customer) {
        this.customer = customer;
    }

    // Getter Methods
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String toXml() {
        return "               <ns1:BillingDetails>" +
                (customer == null ? "" : customer.toXml()) +
                (address == null ? "" : address.toXml()) +
                "               </ns1:BillingDetails>";
    }
}
