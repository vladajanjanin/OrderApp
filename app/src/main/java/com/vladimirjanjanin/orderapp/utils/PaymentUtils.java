package com.vladimirjanjanin.orderapp.utils;

import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;
import com.vladimirjanjanin.orderapp.data.models.Address;
import com.vladimirjanjanin.orderapp.data.models.BillingDetails;
import com.vladimirjanjanin.orderapp.data.models.Customer;
import com.vladimirjanjanin.orderapp.data.models.Order;
import com.vladimirjanjanin.orderapp.data.models.Redirect;
import com.vladimirjanjanin.orderapp.data.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentUtils {

    public static OzowPaymentRequestBody createOzowPaymentRequest(double totalPrice) {

        OzowPaymentRequestBody body = new OzowPaymentRequestBody();
        String reference = System.currentTimeMillis() + Utils.getRandomString(3);
        body.setAmount(totalPrice);
        body.setBankReference(reference);
        body.setTransactionReference(reference);
        body.generateHash();

        return body;
    }

    public static String createPayGateData(double amount, User user) {

        String payGateId = "10011072130";
        String password = "test";
        String notifyUrl = "http://192.168.0.12/A/PayHost/notify.php";
        String returnUrl = "http://116.203.109.203/merchant/api/PayGateTransactions/AddNewPayGateTransaction";

        Redirect redirect = new Redirect();
        redirect.setNotifyUrl(notifyUrl);
        redirect.setReturnUrl(returnUrl);

        Customer customer = new Customer();
        customer.setFirstName(user.getFirstName());
        customer.setLastName(user.getLastName());
        customer.setEmail(user.getEmail());
        customer.setDateOfBirth("1992-07-31");

        Order order = new Order();
        BillingDetails billingDetails = new BillingDetails(customer);
        Address address = new Address();

        address.setAddressLine1("Test Address 1/1");
        address.setCountry("ZAF");

        billingDetails.setAddress(address);
        customer.setAddress(address);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat date1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String merchantOrderId = date1.format(date);

        SimpleDateFormat date2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String transactionDate = date2.format(date);

        if (user.getRole().equals("Merchant")) {
            merchantOrderId = "999" + merchantOrderId;
        }

        order.setMerchantOrderId(merchantOrderId);
        order.setCurrency("ZAR");
        order.setAmount(String.valueOf((int)(amount * 100)));
        order.setTransactionDate(transactionDate);
        order.setBillingDetails(billingDetails);

        return "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://www.paygate.co.za/PayHOST\">" +
                "   <SOAP-ENV:Body>" +
                "      <ns1:SinglePaymentRequest>" +
                "         <ns1:WebPaymentRequest>" +
                "            <!-- Account Details -->" +
                "            <ns1:Account>" +
                "               <ns1:PayGateId>" + payGateId + "</ns1:PayGateId>" +
                "               <ns1:Password>" + password + "</ns1:Password>" +
                "            </ns1:Account>" +
                "            <!-- Customer Details -->" +
                                customer.toXml() +
                "            <!-- Payment Type Details -->" +
                "            <!-- Redirect Details -->" +
                                redirect.toXml() +
                "            <!-- Order Details -->" +
                                order.toXml() +
                "            <!-- User Fields -->" +
                "         </ns1:WebPaymentRequest>" +
                "      </ns1:SinglePaymentRequest>" +
                "   </SOAP-ENV:Body>" +
                "</SOAP-ENV:Envelope>";
    }
}
