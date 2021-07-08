package com.frontend.billify.services;

import com.frontend.billify.models.User;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRoutes {

    @POST("api/users/login/")
    Call<User> loginUser(@Body User user);

    @POST("api/users/signup/")
    Call<User> signupUser(@Body User user);

    @GET("api/groups/user/{uid}")
    Call<User> getGroups(@Path("uid") int uid);
}
