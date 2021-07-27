package com.frontend.billify.models;

import java.util.ArrayList;

public class User {
    private String email;
    private String password;
    private String userName;
    private int id;
    private ArrayList<Group> groups;

    public User(String email, String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }

    //Second constructor to support list of groups
    public User(String email, String password, String userName, ArrayList<Group> groups) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.groups = groups;
    }

    // third constructor for use in itemized view : Acts as UserInfo like in the backend code
    public User(int uid, String userName) {
        this.id = uid;
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(int id) {
        this.id = id;
    }

}
