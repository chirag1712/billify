package com.frontend.billify.models;

public class UserTransactionShare {
    private int uid;
    private String user_name;
    private float price_share;
    private boolean settled;

    public UserTransactionShare(int uid, String user_name, float price_share){
        this.uid = uid;
        this.user_name = user_name;
        this.price_share = price_share;
    }

    public UserTransactionShare(int uid, String user_name, float price_share, boolean settled){
        this.uid = uid;
        this.user_name = user_name;
        this.price_share = price_share;
        this.settled = settled;
    }

    public String getUserName(){
        return user_name;
    }

    public int getUid(){
        return uid;
    }

    public float getPriceShare(){
        return price_share;
    }
    public boolean isSettled(){
        return settled;
    }

    public void setUserName( String userName){
        this.user_name = user_name;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public void setPriceShare(float price_share){
        this.price_share = price_share;
    }
    public void setSettled(boolean isSettled){
        this.settled = isSettled;
    }

}
