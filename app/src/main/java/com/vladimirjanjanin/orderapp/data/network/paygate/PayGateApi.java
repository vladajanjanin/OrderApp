package com.vladimirjanjanin.orderapp.data.network.paygate;

import android.util.Xml;

import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PayGateApi {

    @Headers({"Content-Type: application/xml", "Cache-Control: no-cache"})
    @POST("process.trans")
    Call<ResponseBody> requestPayment(
            @Body RequestBody body
    );
}
