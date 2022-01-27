package com.vladimirjanjanin.orderapp.data.network.backend;

import com.vladimirjanjanin.orderapp.data.dtos.LoginBody;
import com.vladimirjanjanin.orderapp.data.dtos.LoginResponse;
import com.vladimirjanjanin.orderapp.data.dtos.MerchantTransactionBody;
import com.vladimirjanjanin.orderapp.data.dtos.RegisterBody;
import com.vladimirjanjanin.orderapp.data.models.UpdateCustomItemPriceBody;
import com.vladimirjanjanin.orderapp.data.models.MerchantItem;
import com.vladimirjanjanin.orderapp.data.models.MerchantOrderBody;
import com.vladimirjanjanin.orderapp.data.models.UpdateFcmTokenBody;
import com.vladimirjanjanin.orderapp.data.models.User;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackendApi {

    @Headers({"Content-Type: application/json"})
    @POST("MerchantSecurity/Login")
    Call<LoginResponse> login(
            @Body LoginBody body
    );

    @GET("MerchantInventory/GetMerchantItems/{id}")
    Call<List<MerchantItem>> getMerchantItems(
            @Header("Authorization") String token,
            @Path("id") String id
    );

    @Headers({"Content-Type: application/json"})
    @POST("Users/AddNewUser")
    Call<User> register(
            @Body RegisterBody body
    );

    @Headers({"Content-Type: application/json"})
    @POST("MerchantTransactions/ExecuteMerchantTransaction")
    Call<ResponseBody> executeMerchantTransaction(
            @Header("Authorization") String token,
            @Body MerchantTransactionBody body
    );

    @Headers({"Content-Type: application/json"})
    @POST("MerchantOrders/ExecuteMerchantOrder")
    Call<ResponseBody> executeMerchantOrder(
            @Header("Authorization") String token,
            @Body MerchantOrderBody body
    );

    @Headers({"Content-Type: application/json"})
    @POST("FCM/UpdateFcmToken")
    Call<ResponseBody> updateFcmToken(
            @Header("Authorization") String token,
            @Body UpdateFcmTokenBody body
    );

    @Headers({"Content-Type: application/json"})
    @POST("Inventory/UpdateCustomItemPrice")
    Call<ResponseBody> setNewCustomItemPrice(
            @Header("Authorization") String token,
            @Body UpdateCustomItemPriceBody body
    );

}
