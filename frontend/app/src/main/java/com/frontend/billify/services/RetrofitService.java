package com.frontend.billify.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    public Retrofit retrofit;

    // change localhost api address here
    public RetrofitService() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.12:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
