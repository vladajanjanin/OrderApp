package com.vladimirjanjanin.orderapp.data.network.paygate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vladimirjanjanin.orderapp.data.network.ozow.OzowApi;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClientPayGate {

    private static RetrofitClientPayGate instance;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://secure.paygate.co.za/PayHost/";

    private RetrofitClientPayGate() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    public static synchronized RetrofitClientPayGate getInstance() {
        if (instance == null) {
            instance = new RetrofitClientPayGate();
        }
        return instance;
    }

    public PayGateApi getApi() {
        return retrofit.create(PayGateApi.class);
    }
}
