package com.frontend.billify.controllers;

import com.frontend.billify.models.CreateGroupModel;
import com.frontend.billify.models.Group;
import com.frontend.billify.models.User;
import com.frontend.billify.services.ApiRoutes;
import com.frontend.billify.services.RetrofitService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;

public class GroupService {
    private final ApiRoutes apiRoutes;

    public GroupService(RetrofitService retrofitService) {
        this.apiRoutes = retrofitService.retrofit.create(ApiRoutes.class);
    }

    public Call<User> getGroups(int uid ) {
        if (uid == -1) {
            System.out.println("Error, No uid");
        }
        return this.apiRoutes.getGroups(uid);
    }

    public Call<Group> createGroup(int uid, String name, ArrayList<String> emails) {
        if (uid == -1) {
            System.out.println("Error, No uid");
        }
        return this.apiRoutes.createGroup(new CreateGroupModel(uid, name, emails));
    }
}
