package com.frontend.billify.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Transaction implements Serializable {
    private int tid;
    private int gid;
    private String t_date;
    private String t_state;
    // TODO: Let DB Schema be fixed and cross-reference names with DB
    private String receipt_img; // NOTE: This is a URL
    private String transaction_name;
    private ArrayList<Item> items;

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
            System.out.println(item.getItem_id());
            this.items.add(new Item(item.getItem_id(), item.getTid(), item.getName(), item.getPrice()));
        }
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

}
