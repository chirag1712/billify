package com.frontend.billify.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    public Retrofit retrofit;

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    // change localhost api address here
    public RetrofitService() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://129.97.167.52:5000")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
