package com.frontend.billify.models;

import java.util.ArrayList;

public class Transaction {
    private int tid;
    private int gid;
    private String t_date;
    private String t_state;
    private ArrayList<Item> items;

    public Transaction(int tid, int gid, String t_date, String t_state) {
        this.tid = tid;
        this.gid = gid;
        this.t_date = t_date;
        this.t_state = t_state;
        this.items = new ArrayList<Item>();
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

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public void printItems() {
        for (Item item: items) {
            System.out.println(item.getName() + ": " + item.getPrice());
        }
    }

}
