package com.vladimirjanjanin.orderapp.data.network.backend;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientBackend {
    private static RetrofitClientBackend instance;
    private Retrofit retrofit;
    private static final String BASE_URL = "http://116.203.109.203/merchant/api/";

    private RetrofitClientBackend() {
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

    public static synchronized RetrofitClientBackend getInstance() {
        if (instance == null) {
            instance = new RetrofitClientBackend();
        }
        return instance;
    }

    public BackendApi getApi() {
        return retrofit.create(BackendApi.class);
    }
}
