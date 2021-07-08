package com.frontend.billify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.frontend.billify.controllers.GroupService;
import com.frontend.billify.models.Group;
import com.frontend.billify.models.GroupListAdapter;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class groupPop extends Activity {

    private final RetrofitService retrofitService = new RetrofitService();
    private final GroupService groupService = new GroupService(retrofitService);

    //create an array list of the groups a user belongs to
    ArrayList<Group> groups = new ArrayList<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_popup_window);

        //setting the size of the layout to be some percentage of the phones screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screen_width = dm.widthPixels;
        int screen_height = dm.heightPixels;
        getWindow().setLayout((int)(screen_width*0.8),(int) (screen_height*0.8));


        int uid = Persistence.getUserId(this);
        System.out.println("UID " + uid);

        groupService.getGroups(uid).enqueue(
                new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()) {
                            try {
                                JSONObject error = new JSONObject(response.errorBody().string());
                                 Toast.makeText(groupPop.this.getApplicationContext(),
                                        error.getString("error"),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(groupPop.this.getApplicationContext(),
                                        "Sorry :( Something went wrong.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        User user = response.body(); // only userId is returned
                        groups.addAll(user.getGroups());
                        PopulateGroups();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                        Toast.makeText(groupPop.this.getApplicationContext(),
                                "Cannot connect to login server", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void PopulateGroups() {
        System.out.println("Group name " + groups.get(0).getGroup_name());

        //Create an adapter that generates list views for the group list and adds it to group popup window
        GroupListAdapter grouplistadapter = new GroupListAdapter (this, groups);

        ListView listView = (ListView) findViewById(R.id.group_popup);
        listView.setAdapter(grouplistadapter);

        // make items clickable and transition to group transaction history pages
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(groupPop.this, GroupTransaction.class));
            }
        });
    }
}
