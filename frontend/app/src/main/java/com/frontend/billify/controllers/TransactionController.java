package com.frontend.billify.controllers;

import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.services.ApiRoutes;
import com.frontend.billify.services.RetrofitService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionController {
    private final ApiRoutes apiRoutes;

    public TransactionController(RetrofitService retrofitService) {
        this.apiRoutes = retrofitService.retrofit.create(ApiRoutes.class);
    }

    public Call<Transaction> createTransaction(int gid, File img) {
        RequestBody requestGid = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                Integer.toString(gid)
        );
        RequestBody requestImg = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                img
        );
        MultipartBody.Part body =  MultipartBody.Part.createFormData(
                "file",
                img.getName(),
                requestImg
        );
        Call<Transaction> call = this.apiRoutes.createTransaction(requestGid, body);
        return call;

    }

}
