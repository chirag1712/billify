package com.frontend.billify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.frontend.billify.models.Group;
import com.frontend.billify.models.GroupListAdapter;

import java.util.ArrayList;

public class groupPop extends Activity {
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

        //create an array list of the groups a user belongs to
        ArrayList<Group> groups = new ArrayList<Group>();
        groups.add(new Group(1,"cs446 group"));
        groups.add(new Group(2,"apartment group"));
        groups.add(new Group(3,"work group"));
        groups.add(new Group(4,"gym group"));

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
