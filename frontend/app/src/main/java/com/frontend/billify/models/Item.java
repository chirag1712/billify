package com.frontend.billify.models;

import java.io.Serializable;

public class Item implements Serializable {
    private int item_id;
    private int tid;
    private String name;
    private float price;

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

}
