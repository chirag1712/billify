package com.frontend.billify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.frontend.billify.R;
import com.frontend.billify.activities.view_transactions.ViewTransactionsActivity;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

                startActivity(new Intent(HomepageActivity.this, groupPop.class));

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
                intent.putExtra("gid", "4");
                startActivity(intent);
            }
        });

        view_transactions_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageActivity.this, ViewTransactionsActivity.class);
                startActivity(intent);
            }
        });
            
        join_billify_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionController.getTransaction(280).enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        Transaction currTransaction = new Transaction(response.body());
                        System.out.println(
                                "Successful homepage join billify session request with return value: "
                                + currTransaction.getName()
                        );
                        currTransaction.printItems();
                        System.out.println("Curr Transaction TID: " +
                                String.valueOf(currTransaction.getTid()));
                        Intent moveToItemizedScreenIntent = new Intent(
                                HomepageActivity.this,
                                ItemizedViewActivity.class
                        );
                        Bundle transactionBundle = new Bundle();
                        transactionBundle.putSerializable("SerializedTransaction", currTransaction);
                        moveToItemizedScreenIntent.putExtra(
                                "TransactionBundle",
                                transactionBundle
                        );
                        startActivity(moveToItemizedScreenIntent);

                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        Toast.makeText(HomepageActivity.this,
                                "Failed joining the billify session",
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
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