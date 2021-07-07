package com.frontend.billify.models;

public class User {
    private String email;
    private String password;
    private String user_name;
    private int id;

    public User(String email, String password, String user_name) {
        this.email = email;
        this.password = password;
        this.user_name = user_name;
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
