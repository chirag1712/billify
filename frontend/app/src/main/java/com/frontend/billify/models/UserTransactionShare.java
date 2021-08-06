package com.frontend.billify.models;

public class UserTransactionShare {
    private int uid;
    private String userName;
    private float priceShare;
    private boolean isSettled;

    public UserTransactionShare(int uid, String userName, float priceShare, boolean isSettled){
        this.uid = uid;
        this.userName = userName;
        this.priceShare = priceShare;
        this.isSettled= isSettled;

    }
    public String getUserName(){
        return userName;
    }

    public int getUid(){
        return uid;
    }

    public float getPriceShare(){
        return priceShare;
    }
    public boolean getIsSettled(){
        return isSettled;
    }

    public void setUserName( String userName){
        this.userName = userName;
    }

    public void setUid(int uid){
        this.uid = uid;
    }

    public void setPriceShare(float priceShare){
        this.priceShare = priceShare;
    }
    public void setIsSettled(boolean isSettled){
        this.isSettled = isSettled;
    }

}
