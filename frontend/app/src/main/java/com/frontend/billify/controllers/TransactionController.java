package com.frontend.billify.controllers;

import com.frontend.billify.models.SettleResponse;
import com.frontend.billify.models.SettleTransactionModel;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.TransactionSummary;
import com.frontend.billify.models.User;
import com.frontend.billify.models.UserTransaction;
import com.frontend.billify.models.UserTransactionShare;
import com.frontend.billify.services.ApiRoutes;
import com.frontend.billify.services.RetrofitService;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class TransactionController {
    private final ApiRoutes apiRoutes;

    public TransactionController(RetrofitService retrofitService) {
        this.apiRoutes = retrofitService.retrofit.create(ApiRoutes.class);
    }

    // All Transactions in a group
    public Call<ArrayList<Transaction>> getGroupTransactions(int gid ) {
        return this.apiRoutes.getGroupTransactions(gid);
    }

    //All user shares for a transaction
    public Call<TransactionSummary> getUserTransactionShare(int tid ) {
        return this.apiRoutes.getUserTransactionShares(tid);
    }

    public Call<Transaction> parseReceipt(File img) {

        RequestBody requestImg = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                img
        );
        MultipartBody.Part file =  MultipartBody.Part.createFormData(
                "file",
                img.getName(),
                requestImg
        );

        Call<Transaction> call = this.apiRoutes.parseReceipt(
                file
        );
        return call;

    }

    public Call<Transaction> getTransaction(int tid) {
        Call<Transaction> call = this.apiRoutes.getTransaction(tid);
        return call;
    }

    public Call<ArrayList<UserTransaction>> getUserTransactionDetails(int uid) {
        Call<ArrayList<UserTransaction>> call = this.apiRoutes.getUserTransactionDetails(uid);
        return call;
    }

    public Call<Object> updateUserTransactionLabels(ArrayList<UserTransaction> labelUpdates) {
        Call<Object> call = this.apiRoutes.updateUserTransactionLabels(labelUpdates);
        return call;
    }

    public Call<SettleResponse> settleTransaction(int uid, int tid) {
        Call<SettleResponse> call = this.apiRoutes.settleTransaction(new SettleTransactionModel(uid, tid));
        return call;
    }
    

    public Call<Transaction> createTransaction(String transactionDetails, File img) {
        RequestBody transactionDetailsBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                transactionDetails
        );

        RequestBody requestImg = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                img
        );

        MultipartBody.Part file =  MultipartBody.Part.createFormData(
                "file",
                img.getName(),
                requestImg
        );

        Call<Transaction> call = this.apiRoutes.createTransaction(transactionDetailsBody, file);
        return call;
    }


}
