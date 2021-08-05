package com.frontend.billify.models;

import java.util.ArrayList;

public class CreateGroupModel {
    private int user_id;
    private String name;
    private ArrayList<String> emails;

    public CreateGroupModel(int user_id, String name, ArrayList<String> emails){
        this.user_id = user_id;
        this.name = name;
        this.emails = emails;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void setEmails(ArrayList<String> emails) {
        this.emails = emails;
    }
}
