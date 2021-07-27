package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.adapters.ReceiptsItemsRecViewAdapter;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.StartSession;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ItemizedViewActivity extends Activity {
    private RecyclerView itemsRecView;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:5000");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

        itemsRecView = findViewById(R.id.recipeItems);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("TransactionBundle");
        Transaction currTransaction = (Transaction) b.getSerializable("SerializedTransaction");

        // fire socket event to join billify session with uid and tid
        mSocket.connect();
        int uid = Persistence.getUserId(this);
        String username = Persistence.getUserName(this);
        StartSession request = new StartSession(new User(uid, username), currTransaction.getTid());
        mSocket.emit("startSession", request.getJson());

        // set listener for "currentItems" (emitted by server when you join)
        // here or item level: set listeners for "itemSelected" and "itemDeselected" (emitted by server when anyone updates the item)

        ArrayList<Item> items = currTransaction.getItems();
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(this, mSocket, uid, currTransaction.getTid());
        adapter.setItems(items);
        itemsRecView.setAdapter(adapter);
        itemsRecView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
