package com.frontend.billify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.widget.ListView;

import com.frontend.billify.models.Group;
import com.frontend.billify.models.GroupListAdapter;
import com.frontend.billify.models.UserShareListAdapter;

import java.util.ArrayList;

public class GroupTransaction extends AppCompatActivity {
    ArrayList<Pair<String,Integer>> user_shares = new ArrayList<Pair<String,Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_shares.add(new Pair<String,Integer>("Alric",20));
        user_shares.add(new Pair<String,Integer>("Huy",40));
        user_shares.add(new Pair<String,Integer>("Pranav",12));
        user_shares.add(new Pair<String,Integer>("Denis",45));
        user_shares.add(new Pair<String,Integer>("Chirag",32));
        user_shares.add(new Pair<String,Integer>("Mayank",25));


        setContentView(R.layout.activity_group_transaction);
        //Create an adapter that generates list views for the group list and adds it to group popup window
        UserShareListAdapter usersharelistadapter = new UserShareListAdapter (this, user_shares);

        ListView listView = (ListView) findViewById(R.id.user_price_shares);
        listView.setAdapter(usersharelistadapter);




    }
}