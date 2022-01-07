package com.vladimirjanjanin.orderapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.vladimirjanjanin.orderapp.data.Repository;
import com.vladimirjanjanin.orderapp.data.dtos.MerchantTransactionBody;
import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;
import com.vladimirjanjanin.orderapp.data.dtos.OzowTransactionResponse;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.data.models.OzowPaymentRequestResponse;
import com.vladimirjanjanin.orderapp.data.models.OzowTransaction;
import com.vladimirjanjanin.orderapp.data.models.User;
import com.vladimirjanjanin.orderapp.utils.PaymentUtils;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class CheckoutViewModel extends AndroidViewModel {
    private Repository repo;
    private String siteCode = "";
    private String lastReference = "";

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        repo = Repository.getInstance();
    }

    public LiveData<List<MerchantItem>> getCheckoutItemsLiveData() {
        return repo.getCheckoutItemsLiveData();
    }

    public LiveData<OzowPaymentRequestResponse> requestOzowPayment(double totalPrice) {
        OzowPaymentRequestBody body = PaymentUtils.createOzowPaymentRequest(totalPrice);
        siteCode = body.getSiteCode();
        lastReference = body.getTransactionReference();
        return repo.requestOzowPayment(body);
    }

    public LiveData<Response<List<OzowTransaction>>> getLastOzowTransaction() {
        return repo.getLastOzowTransaction(siteCode, lastReference);
    }

    public User getUser() {
        return repo.getUser();
    }

    public void requestPayGatePayment(double totalPrice, User user) {
        repo.requestPayGatePayment(totalPrice, user);
    }

    public String getCurrentMerchantId() {
        return repo.getCurrentMerchantId();
    }

    public void sendSuccessfulTransaction(MerchantTransactionBody body) {
        repo.sendSuccessfulTransaction(body);
    }
}
