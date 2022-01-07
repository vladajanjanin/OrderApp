package com.vladimirjanjanin.orderapp.data.network.ozow;

import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientOzow {

    private static RetrofitClientOzow instance;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://api.ozow.com/";

    private RetrofitClientOzow() {
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

    public static synchronized RetrofitClientOzow getInstance() {
        if (instance == null) {
            instance = new RetrofitClientOzow();
        }
        return instance;
    }

    public OzowApi getApi() {
        return retrofit.create(OzowApi.class);
    }
}
