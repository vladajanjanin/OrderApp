package com.vladimirjanjanin.orderapp.data.network.ozow;

import com.google.gson.JsonObject;
import com.vladimirjanjanin.orderapp.data.dtos.LoginBody;
import com.vladimirjanjanin.orderapp.data.dtos.LoginResponse;
import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;
import com.vladimirjanjanin.orderapp.data.dtos.OzowTransactionResponse;
import com.vladimirjanjanin.orderapp.data.models.OzowPaymentRequestResponse;
import com.vladimirjanjanin.orderapp.data.models.OzowTransaction;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OzowApi {
    String apiKey = "d254ffc559cd416b9905166070c4862d";

    @Headers({"Content-Type: application/json", "ApiKey: d254ffc559cd416b9905166070c4862d"})
    @POST("PostPaymentRequest")
    Call<OzowPaymentRequestResponse> requestPayment(
            @Body OzowPaymentRequestBody body
    );

    @Headers({"Content-Type: application/json", "ApiKey: d254ffc559cd416b9905166070c4862d"})
    @GET("GetTransactionByReference")
    Call<List<OzowTransaction>> getTransaction(
            @Query("SiteCode") String siteCode,
            @Query("TransactionReference") String transactionReference,
            @Query("IsTest") boolean isTest
    );
}
