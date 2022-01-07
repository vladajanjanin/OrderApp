package com.vladimirjanjanin.orderapp.data.network.sinch;

import com.vladimirjanjanin.orderapp.data.dtos.OzowPaymentRequestBody;
import com.vladimirjanjanin.orderapp.data.models.OzowPaymentRequestResponse;
import com.vladimirjanjanin.orderapp.data.models.OzowTransaction;
import com.vladimirjanjanin.orderapp.data.models.SmsVerificationBody;
import com.vladimirjanjanin.orderapp.data.models.SmsVerificationRequestBody;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SinchApi {

    @Headers({"Content-Type: application/json", "Authorization: Basic YTUxZmZiYTUtMTY2NC00NGQ4LTlhMzYtMjA2MmY3ODkyODZkOkpYY3luVG1GUFVxalI1U3NSblFPRFE9PQ=="})
    @POST("verifications/")
    Call<ResponseBody> requestPhoneVerification(
            @Body SmsVerificationRequestBody body
    );

    @Headers({"Content-Type: application/json", "Authorization: Basic YTUxZmZiYTUtMTY2NC00NGQ4LTlhMzYtMjA2MmY3ODkyODZkOkpYY3luVG1GUFVxalI1U3NSblFPRFE9PQ=="})
    @PUT("verifications/number/{phoneNumber}")
    Call<ResponseBody> verifyCode(
            @Path("phoneNumber") String phoneNumber,
            @Body SmsVerificationBody body
            );
}
