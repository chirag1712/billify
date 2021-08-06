package com.frontend.billify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.frontend.billify.R;
import com.frontend.billify.activities.view_transactions.ViewTransactionsActivity;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.UserTransaction;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button view_group_button = findViewById(R.id.view_all_group);
        Button add_receipt_button = findViewById(R.id.add_receipt_button);
        Button create_group_button = findViewById(R.id.create_group_button);
        Button view_transactions_button = findViewById(R.id.view_transactions_button);
        Button join_billify_session = findViewById(R.id.join_billify_session);
        Button logoutButton = findViewById(R.id.logout_button);
        RetrofitService retrofitService = new RetrofitService();
        TransactionController transactionController = new TransactionController(retrofitService);

        view_group_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomepageActivity.this, ViewGroupActivity.class));

            }
        });

        create_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, CreateGroupActivity.class));
            }
        });

        add_receipt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageActivity.this, UploadReceiptActivity.class);
                startActivity(intent);
            }
        });

        view_transactions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToViewTransactionsScreenIntent = new Intent(HomepageActivity.this, ViewTransactionsActivity.class);
                startActivity(moveToViewTransactionsScreenIntent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Persistence.clearLoginDetails(HomepageActivity.this);
                Intent loginActivityIntent = new Intent(HomepageActivity.this, AuthenticationActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }
}