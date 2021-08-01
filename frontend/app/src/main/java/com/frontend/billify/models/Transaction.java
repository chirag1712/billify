package com.frontend.billify.models;

import android.telephony.gsm.GsmCellLocation;

import com.google.gson.Gson;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Transaction implements Serializable {
    private int tid;
    private int gid;
    private String t_date;
    private String t_state;
    private String receipt_img; // NOTE: receipt_img is a URL to the image
    private String transaction_name;
    private ArrayList<Item> items;
    private int label_id;

    /* currPhotoFile is used to store the chosen picture and send it to backend for parsing receipt
    or creating a new transaction
    */

    private static final HashMap<String, Integer> labelNameToLabelId = new HashMap<>();

    static {
        labelNameToLabelId.put("Unlabelled", 1);
        labelNameToLabelId.put("Food", 2);
        labelNameToLabelId.put("Entertainment", 3);
        labelNameToLabelId.put("Groceries", 4);
        labelNameToLabelId.put("Shopping", 5);
        labelNameToLabelId.put("Electronics", 6);
        labelNameToLabelId.put("Housing", 7);
    }

    private File currPhotoFile;
    public Transaction(int tid, int gid, String t_date, String t_state,
                       String transaction_name, String receipt_img) {
        this.tid = tid;
        this.gid = gid;
        this.t_date = t_date;
        this.t_state = t_state;
        this.items = new ArrayList<Item>();
        this.transaction_name = transaction_name;
        this.receipt_img = receipt_img;
    }

    public Transaction(Transaction t) {
        this.tid = t.tid;
        this.gid = t.gid;
        this.t_date = t.t_date;
        this.t_state = t.t_state;
        this.transaction_name = t.transaction_name;
        this.receipt_img = t.receipt_img;

        // deep copy
        this.items = new ArrayList<Item>();
        for (Item item: t.items) {
            this.items.add(new Item(item.getItem_id(), item.getTid(), item.getName(), item.getPrice()));
        }
    }
    
    public void addItem(int position, Item item) {
        this.items.add(position, item);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public int getTid() {
        return this.tid;
    }

    public int getGid() {
        return this.gid;
    }

    public String getName() { return this.transaction_name; }

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public void printItems() {
        for (Item item: items) {
            System.out.println(item.getName() + ": " + item.getPrice());
        }
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public int getNumItems() {
        return items.size();
    }

    public String getTransactionJSONString() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public File getCurrPhotoFile() {
        return currPhotoFile;
    }

    public void setCurrPhotoFile(File currPhotoFile) {
        this.currPhotoFile = currPhotoFile;
    }

    public void setTransaction_name(String transactionName) {
        this.transaction_name = transactionName;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getLabel_id() {
        return label_id;
    }

    public void setLabel_id(int label_id) {
        this.label_id = label_id;
    }

    public void setLabel_id(String labelName) {
        this.label_id = labelNameToLabelId.get(labelName);
        System.out.println("Label ID is: " + String.valueOf(this.label_id));
    }
}
