package com.frontend.billify.services;

import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiRoutes {

    // User routes
    @POST("api/users/login/")
    Call<User> loginUser(@Body User user);

    @POST("api/users/signup/")
    Call<User> signupUser(@Body User user);

    // NOTE: parse-receipt endpoint creates transaction
    @Multipart
    @POST("api/transactions/parse-receipt")
    Call<Transaction> createTransaction(
            @Part("gid") RequestBody gid,
            @Part MultipartBody.Part file);
}
