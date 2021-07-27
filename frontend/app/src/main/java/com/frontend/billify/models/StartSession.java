package com.frontend.billify.models;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class StartSession {
    private int uid;
    private int tid;

    public StartSession(int uid, int tid) {
        this.uid = uid;
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
