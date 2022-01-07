package com.vladimirjanjanin.orderapp.data.network.sinch;

import com.vladimirjanjanin.orderapp.data.network.ozow.OzowApi;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientSinch {

    private static RetrofitClientSinch instance;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://verificationapi-v1.sinch.com/verification/v1/";

    private RetrofitClientSinch() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static synchronized RetrofitClientSinch getInstance() {
        if (instance == null) {
            instance = new RetrofitClientSinch();
        }
        return instance;
    }

    public SinchApi getApi() {
        return retrofit.create(SinchApi.class);
    }
}
