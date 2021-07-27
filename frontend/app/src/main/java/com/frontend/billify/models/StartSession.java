package com.frontend.billify.models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class StartSession {
    private String username;
    private int uid;
    private int tid;

    public StartSession(User u, int tid) {
        this.uid = u.getId();
        this.username = u.getUser_name();
        this.tid = tid;
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
