package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.frontend.billify.R;

import com.frontend.billify.adapters.PastTransactionCardAdapter;

import java.util.ArrayList;

public class GroupTransactionActivity extends AppCompatActivity {
    private RecyclerView pastTransactionsRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction);
        ArrayList<Pair<Pair<String,Integer>,ArrayList<Pair<String,Integer>>>> transaction = new ArrayList<Pair<Pair<String,Integer>,ArrayList<Pair<String,Integer>>>>();
        ArrayList<Pair<String,Integer>> user_shares = new ArrayList<Pair<String,Integer>>();
        user_shares.add(new Pair<String,Integer>("Alric",20));
        user_shares.add(new Pair<String,Integer>("Huy",40));
        user_shares.add(new Pair<String,Integer>("Pranav",12));
        user_shares.add(new Pair<String,Integer>("Denis",45));
        user_shares.add(new Pair<String,Integer>("Chirag",32));
        user_shares.add(new Pair<String,Integer>("Mayank",25));

        Pair<String,Integer> transaction_details = new Pair<String,Integer>("WALMART",300);
        Pair<Pair<String,Integer>,ArrayList<Pair<String,Integer>>> single_transaction= new Pair<Pair<String,Integer>,ArrayList<Pair<String,Integer>>>(transaction_details,user_shares);
        transaction.add(single_transaction);
        transaction.add(single_transaction);




        pastTransactionsRecView= findViewById(R.id.recipeTransactions);


//        //Create an adapter that generates list views for the group list and adds it to group popup window
//        UserShareListAdapter usersharelistadapter = new UserShareListAdapter (this, user_shares);
//
//        ListView listView = (ListView) findViewById(R.id.user_price_shares);
//        listView.setAdapter(usersharelistadapter);
        PastTransactionCardAdapter adapter = new PastTransactionCardAdapter (this);
        adapter.setItems(transaction);
        pastTransactionsRecView.setAdapter(adapter);
        pastTransactionsRecView.setLayoutManager(new LinearLayoutManager(this));
        final Button add_group_receipt = findViewById(R.id.add_group_receipt);
        add_group_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupTransactionActivity.this, UploadReceiptActivity.class);
                System.out.println("In group transaction " + getIntent().getStringExtra("gid"));
                intent.putExtra("gid", getIntent().getStringExtra("gid"));
                startActivity(intent);
            }
        });
    }
}

