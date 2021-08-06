package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frontend.billify.R;

import com.frontend.billify.adapters.PastTransactionCardAdapter;
import com.frontend.billify.controllers.GroupService;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.services.RetrofitService;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupTransactionActivity extends AppCompatActivity {
    private RecyclerView pastTransactionsRecView;
    private final RetrofitService retrofitService = new RetrofitService();
    private final TransactionController transactionController = new TransactionController(retrofitService);
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction);

        int gid = Integer.parseInt(getIntent().getStringExtra("gid"));
        System.out.println("gid: " + gid);
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

        //function makes API call for transactions
        // getTransactions(gid);
        getTransactions(gid);


        final Button add_group_receipt = findViewById(R.id.add_group_receipt);
        add_group_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupTransaction.this, UploadReceiptActivity.class);
                intent.putExtra("gid", getIntent().getStringExtra("gid"));
                startActivity(intent);
            }
        });
    }

    public void getTransactions(int gid){

        transactionController.getGroupTransactions(gid).enqueue(new Callback<ArrayList<Transaction>>() {
            @Override
            public void onResponse(Call<ArrayList<Transaction>> call, Response<ArrayList<Transaction>> response) {
                if (!response.isSuccessful()) {
                    try {
                        JSONObject error = new JSONObject(response.errorBody().string());
                        Toast.makeText(GroupTransactionActivity.this.getApplicationContext(),
                                error.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(GroupTransactionActivity.this.getApplicationContext(),
                                "Sorry :( Something went wrong.",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                transactions = response.body();
                populateTransactions(transactions);
            }

            @Override
            public void onFailure(Call<ArrayList<Transaction>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Cannot connect to login server", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void populateTransactions(ArrayList<Transaction> transactions) {
        PastTransactionCardAdapter adapter = new PastTransactionCardAdapter (this);
        adapter.setItems(transactions);
        pastTransactionsRecView.setAdapter(adapter);
        pastTransactionsRecView.setLayoutManager(new LinearLayoutManager(this));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://billify.s3.amazonaws.com/android%20group%202021-07-27%2020%3A10-1627416608911.jpg"));
        startActivity(browserIntent);
    }
}

