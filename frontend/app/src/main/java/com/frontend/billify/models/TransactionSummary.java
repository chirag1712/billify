package com.frontend.billify.models;

import java.util.ArrayList;

public class TransactionSummary {
    private float total_price;
    private ArrayList<UserTransactionShare> user_price_shares;

    public TransactionSummary(float total_price, ArrayList<UserTransactionShare> user_price_shares){
        this.total_price = total_price;
        this.user_price_shares = user_price_shares;
    }

    public float getTotalPrice(){
        return total_price;
    }

    public ArrayList<UserTransactionShare> getUserPriceShares(){
        return user_price_shares;
    }
}
