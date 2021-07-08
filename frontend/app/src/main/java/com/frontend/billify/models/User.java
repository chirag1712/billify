package com.frontend.billify.models;

import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String user_name;
    private int id;
    private ArrayList<Group> groups;

    public User(String email, String password, String user_name) {
        this.email = email;
        this.password = password;
        this.user_name = user_name;
    }

    //Second constructor to support list of groups
    public User(String email, String password, String user_name, ArrayList<Group> groups) {
        this.email = email;
        this.password = password;
        this.user_name = user_name;
        this.groups = groups;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Group> getGroups(){return groups;}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setId(int id) {
        this.id = id;
    }

}
