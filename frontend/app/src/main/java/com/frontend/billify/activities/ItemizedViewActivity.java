package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.models.Transaction;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ItemizedViewActivity extends Activity {
    private ItemizedView itemizedView;
    private UserPriceShare userPriceShare;

    private RecyclerView itemsRecView;
    // TODO: can add models for the socket responses later
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billify_session);

        Button saveButton = findViewById(R.id.save_button);
        mSocket.connect();

        Intent i = getIntent();
        Bundle b = i.getBundleExtra("TransactionBundle");
        Transaction currTransaction = (Transaction) b.getSerializable("SerializedTransaction");

        // price share view
        userPriceShare = new UserPriceShare(findViewById(R.id.user_shares), this);

        // itemized view
        itemizedView = new ItemizedView(findViewById(R.id.recipeItems), this, mSocket, currTransaction);

        // move socket listeners here in parent class how? -> dispatch to class specific event handlers somehow

        saveButton.setOnClickListener(view -> {
            mSocket.disconnect();
            finish();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
