package com.frontend.billify.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Item implements Serializable {
    private int item_id;
    private int tid;
    private String name;
    private float price;

    // maps uid to user for all users who have selected this item
    private HashMap<Integer, String> selectedBy;

    public Item(int item_id, int tid, String name, float price) {
        this.item_id = item_id;
        this.tid = tid;
        this.name = name;
        this.price = price;
    }

    public Item(String name, float price) {
        this.name = name;
        this.price = price;
    }


    public int getItem_id() {
        return this.item_id;
    }

    public String getName() {
        return this.name;
    }

    public float getPrice() {
        return this.price;
    }
    public String getStrPrice() {
        return Float.toString(this.price);
    }

    public Boolean isSelectedBy(int uid) {
        return this.selectedBy.containsKey(uid);
    }
    public void select(int uid, String username) {
        this.selectedBy.put(uid, username);
    }
    public void deselect(int uid) {
        this.selectedBy.remove(uid);
    }

    // helper for returning all user names who have selected the item
}
