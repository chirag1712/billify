package com.frontend.billify.models;

public class SettleTransactionModel {
    private int uid;
    private int tid;

    public SettleTransactionModel(int uid, int tid){
        this.tid = tid;
        this.uid = uid;
    }
}
