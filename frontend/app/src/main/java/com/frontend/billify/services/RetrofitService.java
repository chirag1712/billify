package com.frontend.billify.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    public Retrofit retrofit;

    public RetrofitService() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://10.7.2.118:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
