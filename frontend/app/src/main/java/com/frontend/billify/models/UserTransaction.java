package com.frontend.billify.models;

public class UserTransaction {
    private int tid;
    private int uid;
    private String transaction_name;
    private Label label;
    private float price_share;

    public UserTransaction(int tid, int uid, String transaction_name, Label label) {
        this.tid = tid;
        this.uid = uid;
        this.transaction_name = transaction_name;
        this.label = label;
    }

    public UserTransaction(int tid, String transaction_name, Label label) {
        this.tid = tid;
        this.transaction_name = transaction_name;
        this.label = label;
    }

    public UserTransaction(int tid, String transaction_name, Label label, float price_share) {
        this.tid = tid;
        this.transaction_name = transaction_name;
        this.label = label;
        this.price_share = price_share;
    }

    public UserTransaction(UserTransaction userTransaction) {
        this.tid = userTransaction.tid;
        this.transaction_name = userTransaction.transaction_name;
        this.label = new Label(userTransaction.label);
        this.price_share = userTransaction.price_share;
    }


    public float getPrice_share() {
        return price_share;
    }

    public int getTid() {
        return tid;
    }

    public int getUid() {
        return uid;
    }

    public Label getLabel() {
        return label;
    }

    public String getTransaction_name() {
        return transaction_name;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public String getLabel_name() {
        return label.getLabel_name();
    }

    public void setPrice_share(float price_share) {
        this.price_share = price_share;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }
}