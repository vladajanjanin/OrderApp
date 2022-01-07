package com.vladimirjanjanin.orderapp.data;

import android.provider.Telephony;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.vladimirjanjanin.orderapp.data.dtos.MerchantTransactionBody;
import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;
import com.vladimirjanjanin.orderapp.data.dtos.OzowTransactionResponse;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterBody;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterResponse;
import com.vladimirjanjanin.orderapp.data.models.Identity;
import com.vladimirjanjanin.orderapp.data.models.MerchantOrderBody;
import com.vladimirjanjanin.orderapp.data.models.Order;
import com.vladimirjanjanin.orderapp.data.models.OrderItem;
import com.vladimirjanjanin.orderapp.data.models.OzowPaymentRequestResponse;
import com.vladimirjanjanin.orderapp.data.models.OzowTransaction;
import com.vladimirjanjanin.orderapp.data.models.Sms;
import com.vladimirjanjanin.orderapp.data.models.SmsVerificationBody;
import com.vladimirjanjanin.orderapp.data.models.SmsVerificationRequestBody;
import com.vladimirjanjanin.orderapp.data.models.UpdateFcmTokenBody;
import com.vladimirjanjanin.orderapp.data.models.User;
import com.vladimirjanjanin.orderapp.data.network.ozow.OzowApi;
import com.vladimirjanjanin.orderapp.data.network.ozow.RetrofitClientOzow;
import com.vladimirjanjanin.orderapp.data.network.paygate.PayGateApi;
import com.vladimirjanjanin.orderapp.data.network.paygate.RetrofitClientPayGate;
import com.vladimirjanjanin.orderapp.data.network.sinch.RetrofitClientSinch;
import com.vladimirjanjanin.orderapp.data.network.sinch.SinchApi;
import com.vladimirjanjanin.orderapp.utils.PaymentUtils;
import com.vladimirjanjanin.orderapp.utils.Utils;
import com.vladimirjanjanin.orderapp.data.dtos.LoginBody;
import com.vladimirjanjanin.orderapp.data.dtos.LoginResponse;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.data.models.LoginInfo;
import com.vladimirjanjanin.orderapp.data.network.backend.BackendApi;
import com.vladimirjanjanin.orderapp.data.network.backend.RetrofitClientBackend;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static Repository                       instance;
    private BackendApi                              backendApi;
    private OzowApi                                 ozowApi;
    private PayGateApi                              payGateApi;
    private SinchApi                                sinchApi;

    private MutableLiveData<List<MerchantItem>>     inventoryLiveData;
    private MutableLiveData<List<MerchantItem>>     checkoutItemsLiveData;
    private LoginInfo                               loginInfo;
    private User                                    currentUser;
    private String                                  currentMerchantId = "";
    private String                                  fcmToken = "";

    private Repository() {
        backendApi = RetrofitClientBackend.getInstance().getApi();
        ozowApi = RetrofitClientOzow.getInstance().getApi();
        payGateApi = RetrofitClientPayGate.getInstance().getApi();
        sinchApi = RetrofitClientSinch.getInstance().getApi();

        loginInfo = new LoginInfo();
        inventoryLiveData = new MutableLiveData<>();
        checkoutItemsLiveData = new MutableLiveData<>();
    }

    public static Repository getInstance() {
        if (instance == null) instance = new Repository();
        return instance;
    }

    public LiveData<LoginInfo> login(String email, String password) {
        LoginBody body = new LoginBody(email, password);

        Call<LoginResponse> call = backendApi.login(body);
        MutableLiveData<LoginInfo> loginInfoLiveData = new MutableLiveData<>();
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    loginInfo.setCode(response.code());
                    if (response.body() != null) {
                        loginInfo.setUser(response.body().getFlattenUser());
                        Utils.log("ID:" + loginInfo.getUser().getId());
                        Utils.log("Logged in role: " + loginInfo.getUser().getRole());
                    }
                    loginInfo.setToken(response.body().getToken());

                    //hack until response codes are sorted
                    if (loginInfo.getToken().equals("")) {
                        //failed login (credentials probably)
                        loginInfo.setCode(-1);
                        loginInfo.setMessage("Login failed. Check your credentials.");
                    }
                    currentUser = loginInfo.getUser();
                    loginInfoLiveData.postValue(loginInfo);
                } else {
                    LoginInfo loginInfo = new LoginInfo();
                    loginInfo.setCode(response.code());
                    loginInfo.setMessage(response.message());
                    loginInfoLiveData.postValue(loginInfo);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setCode(-1);
                loginInfo.setMessage(t.getMessage());
                loginInfoLiveData.postValue(loginInfo);
            }
        });
        return loginInfoLiveData;
    }

    public LiveData<List<MerchantItem>> getInventoryLiveData() {
        return inventoryLiveData;
    }

    public LiveData<List<MerchantItem>> getCheckoutItemsLiveData() {
        return checkoutItemsLiveData;
    }

    public void setCheckoutItemsLiveData(List<MerchantItem> checkoutItems) {
        this.checkoutItemsLiveData.setValue(checkoutItems);
    }

    public void refreshInventory(String merchantId) {
        Call<List<MerchantItem>> call = backendApi.getMerchantItems(createAuthToken(), merchantId);
        call.enqueue(new Callback<List<MerchantItem>>() {
            @Override
            public void onResponse(Call<List<MerchantItem>> call, Response<List<MerchantItem>> response) {
                Utils.log("Fetching items: " + response.code());
                if (response.isSuccessful()) {
                    inventoryLiveData.postValue(response.body());
                } else {
                    Utils.log(response.code() + " " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<MerchantItem>> call, Throwable t) {
                Utils.log(t.getMessage());
            }
        });
    }

    private String createAuthToken() {
        Utils.log("Token: " + loginInfo.getToken());
        return "Bearer " + loginInfo.getToken();
    }

    public LiveData<OzowPaymentRequestResponse> requestOzowPayment(OzowPaymentRequestBody body) {

        MutableLiveData<OzowPaymentRequestResponse> responseLiveData = new MutableLiveData<>();

        Call<OzowPaymentRequestResponse> call = ozowApi.requestPayment(body);
        call.enqueue(new Callback<OzowPaymentRequestResponse>() {
            @Override
            public void onResponse(Call<OzowPaymentRequestResponse> call, Response<OzowPaymentRequestResponse> response) {
                response.body().setCode(response.code());
                responseLiveData.postValue(response.body());
            }
            @Override
            public void onFailure(Call<OzowPaymentRequestResponse> call, Throwable t) {
                Utils.log("Error: " + t.getMessage());
                OzowPaymentRequestResponse responseBody = new OzowPaymentRequestResponse();
                responseBody.setCode(-1);
                responseBody.setErrorMessage(t.getMessage());
                responseLiveData.postValue(responseBody);
            }
        });

        return responseLiveData;
    }

    public LiveData<RegisterResponse> register(RegisterBody body) {

        MutableLiveData<RegisterResponse> responseLiveData = new MutableLiveData<>();

        Call<User> call = backendApi.register(body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                RegisterResponse res = new RegisterResponse();
                if (response.isSuccessful()) {
                    res.setUser(response.body());
                    res.setCode(response.code());
                    responseLiveData.postValue(res);
                } else {
                    res.setCode(response.code());
                    responseLiveData.postValue(res);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                RegisterResponse res = new RegisterResponse();
                res.setCode(-1);
                responseLiveData.postValue(res);
            }
        });

        return responseLiveData;
    }

    public LiveData<Response<List<OzowTransaction>>> getLastOzowTransaction(String siteCode, String lastReference) {

        boolean isTest = true;

        MutableLiveData<Response<List<OzowTransaction>>> responseLiveData = new MutableLiveData<>();

        Call<List<OzowTransaction>> call = ozowApi.getTransaction(siteCode, lastReference, isTest);
        call.enqueue(new Callback<List<OzowTransaction>>() {
            @Override
            public void onResponse(Call<List<OzowTransaction>> call, Response<List<OzowTransaction>> response) {
                responseLiveData.postValue(response);
            }
            @Override
            public void onFailure(Call<List<OzowTransaction>> call, Throwable t) {
                Utils.log("Error: " + t.getMessage());
            }
        });

        return responseLiveData;
    }

    public User getUser() {
        return currentUser;
    }

    public void requestPayGatePayment(double totalPrice, User user) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/xml");

        String data = PaymentUtils.createPayGateData(totalPrice, user);

        RequestBody requestBody = RequestBody.create(mediaType, data);

        Call<ResponseBody> call = payGateApi.requestPayment(requestBody);

        call.enqueue(new Callback<ResponseBody> () {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.log("Code: " + response.code());
                Utils.log("RES:   " + response.body().toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.log(t.getMessage());
            }
        });

    }

    public boolean isMerchant() {
        return  (loginInfo.getUser().getRole().equals("Merchant"));
    }

    public void setCurrentMerchantId(String id) {
        this.currentMerchantId = id;
    }

    public String getCurrentMerchantId() {
        return currentMerchantId;
    }

    public LiveData<Integer> sendSuccessfulTransaction(MerchantTransactionBody body) {

        MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();
        Utils.log("Body: " + body.getMerchantId() + " " + body.getTotalPrice() + " " + body.getTransactionItems().get(0).getPrice());
        Call<ResponseBody> call = backendApi.executeMerchantTransaction(createAuthToken(), body);
        call.enqueue(new Callback<ResponseBody> () {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                responseLiveData.postValue(response.code());
                Utils.log("Code: " + response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.log(t.getMessage());
            }
        });
        return responseLiveData;
    }

    public void updateFcmToken() {

        UpdateFcmTokenBody body = new UpdateFcmTokenBody();
        body.setUserId(loginInfo.getUser().getId());
        body.setToken(fcmToken);

        Call<ResponseBody> call = backendApi.updateFcmToken(createAuthToken(), body);
        call.enqueue(new Callback<ResponseBody> () {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.log("Fcm response: " + response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.log(t.getMessage());
            }
        });

    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public LiveData<Integer> verifyPhoneNumber(String phoneNumber) {

        MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();

        SmsVerificationRequestBody body = new SmsVerificationRequestBody();
        Identity identity = new Identity();
        identity.setEndpoint(phoneNumber);
        body.setIdentity(identity);

        Call<ResponseBody> call = sinchApi.requestPhoneVerification(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.log("SMS Verif code: " + response.code());
                responseLiveData.postValue(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return responseLiveData;
    }

    public LiveData<Integer> verifyCode(String phoneNumber, String code) {

        MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();

        SmsVerificationBody body = new SmsVerificationBody();
        Sms sms = new Sms();
        sms.setCode(code);
        body.setSms(sms);

        Call<ResponseBody> call = sinchApi.verifyCode(phoneNumber, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.log("Final verification code: " + response.code());
                responseLiveData.postValue(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return responseLiveData;
    }

    public LiveData<Integer> updateMerchantInventory(String merchantId, List<MerchantItem> merchantItems) {
        MutableLiveData<Integer> responseLiveData = new MutableLiveData<>();

        MerchantOrderBody body = new MerchantOrderBody();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String created = dateF.format(date);

        body.setCreated(created);
        body.setMerchantId(merchantId);

        List<OrderItem> orderItems = new ArrayList<>();
        for (MerchantItem item : merchantItems) {
            if (item.getOrderQuantity() > 0) {
                OrderItem newItem = new OrderItem();
                newItem.setItemId(item.getInventoryItem().getId());
                newItem.setPrice(item.getInventoryItem().getPrice());
                newItem.setQuantity(item.getOrderQuantity());
                orderItems.add(newItem);
            }
        }
        body.setOrderItems(orderItems);
        
        Call<ResponseBody> call = backendApi.executeMerchantOrder(createAuthToken(), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                responseLiveData.postValue(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return responseLiveData;
    }

    public String getUserId() {
        return loginInfo.getUser().getId();
    }
}
