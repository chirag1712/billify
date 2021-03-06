package com.frontend.billify.controllers;

import com.frontend.billify.models.User;
import com.frontend.billify.services.ApiRoutes;
import com.frontend.billify.services.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

    private final ApiRoutes apiRoutes;

    public UserService(RetrofitService retrofitService) {
        this.apiRoutes = retrofitService.retrofit.create(ApiRoutes.class);
    }

    public Call<User> loginUser(User user) {
        return this.apiRoutes.loginUser(user);
    }

    public Call<User> signupUser(User user) {
        return this.apiRoutes.signupUser(user);
    }
}
