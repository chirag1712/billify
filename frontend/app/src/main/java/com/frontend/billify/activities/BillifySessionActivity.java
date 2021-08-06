package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.frontend.billify.R;
import com.frontend.billify.models.StartSession;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class BillifySessionActivity extends Activity {
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://129.97.167.52:5000");
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

        // client joins session by emitting start session event
        int uid = Persistence.getUserId(this);
        String userName = Persistence.getUserName(this);
        StartSession request = new StartSession(new User(uid, userName), currTransaction.getTid());
        mSocket.emit("startSession", request.getJson());

        // itemized view for transaction items
        ItemizedView itemizedView = new ItemizedView(findViewById(R.id.recipeItems), this, mSocket, currTransaction);

        // price share view for users and their prices owed
        UserPriceShareView userPriceShareView = new UserPriceShareView(findViewById(R.id.user_shares), this, mSocket);

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
