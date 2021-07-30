package com.frontend.billify.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        this.selectedBy = new HashMap<Integer,String>();
    }

    public Item(String name, float price) {
        this.name = name;
        this.price = price;
        this.selectedBy = new HashMap<Integer,String>();
    }

    public int getTid() {
        return tid;
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
        return String.valueOf(this.price);
    }

    public Boolean isSelectedBy(int uid) {
        return this.selectedBy.containsKey(uid);
    }

    public void select(int uid, String username) {
        // add uid to hashmap
        this.selectedBy.put(uid, username);
    }

    public void deselect(int uid) {
        // remove uid from hashmap
        this.selectedBy.remove(uid);
    }

    // helper for returning all user names who have selected the item
    public ArrayList<String> getSelectedUsers() {
        return new ArrayList<>(this.selectedBy.values());
    }

    public void updateSelectedBy(JSONArray userInfos) {
        this.selectedBy.clear();
        for(int i = 0; i < userInfos.length(); i++) {
            try {
                JSONObject userInfo = (JSONObject) userInfos.get(i);
                int uid = (int) userInfo.get("uid");
                String username = (String) userInfo.get("username");
                this.selectedBy.put(uid, username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setPrice(String price) {
        this.price = Float.valueOf(price);
    }


}
