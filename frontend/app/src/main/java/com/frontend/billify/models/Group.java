package com.frontend.billify.models;

public class Group {
    private int gid;
    private String group_name;

    public Group(int gid, String group_name){
        this.gid = gid;
        this.group_name = group_name;

    }

    public int getGid(){return gid;}

    public String getGroup_name(){return group_name;}
}
