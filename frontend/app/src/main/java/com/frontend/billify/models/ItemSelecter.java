package com.frontend.billify.models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemSelecter {
    private String userName;
    // Can be used to fire events for selecting and deselecting items
    private int uid;
    private int tid;
    private int item_id;

    public ItemSelecter(User u, int tid, int item_id) {
        this.uid = u.getId();
        this.userName = u.getUserName();
        this.tid = tid;
        this.item_id = item_id;
    }

    public JSONObject getJson() {
        Gson gson = new Gson();
        try {
            JSONObject obj = new JSONObject(gson.toJson(this));
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
