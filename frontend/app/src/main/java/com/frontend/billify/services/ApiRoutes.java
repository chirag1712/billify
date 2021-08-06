package com.frontend.billify.services;

import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.CreateGroupModel;
import com.frontend.billify.models.Group;
import com.frontend.billify.models.User;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Part;


public interface ApiRoutes {

    // User routes
    @POST("api/users/login/")
    Call<User> loginUser(@Body User user);

    @POST("api/users/signup/")
    Call<User> signupUser(@Body User user);

    // Group routes
    @GET("api/groups/user/{uid}")
    Call<User> getGroups(@Path("uid") int uid);

    @POST("api/groups/create")
    Call<Group> createGroup(@Body CreateGroupModel createGroupModel);

    // Transaction routes
    // NOTE: parse-receipt endpoint creates transaction
    @Multipart
    @POST("api/transactions/parse-receipt")
    Call<Transaction> parseReceipt(@Part MultipartBody.Part file);

    @GET("api/transactions/transaction/{tid}")
    Call<Transaction> getTransaction(@Path("tid") int tid);

    @GET("api/transactions/get-group-transactions/{gid}}")
    Call<ArrayList<Transaction>> getGroupTransactions(@Path("gid") int gid);

    @Multipart
    @POST("api/transactions/create-transaction")
    Call<Transaction> createTransaction(
      @Part("transaction_details") RequestBody transactionDetails,
      @Part MultipartBody.Part file
    );
}
