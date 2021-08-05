package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.adapters.ReceiptsItemsRecViewAdapter;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.StartSession;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ItemizedViewActivity extends Activity {
    private RecyclerView itemsRecView;
    // TODO: can add models for the socket responses later
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
        String userName = Persistence.getUserName(this);
        StartSession request = new StartSession(new User(uid, userName), currTransaction.getTid());

        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(
                this, mSocket, new User(uid, userName), currTransaction.getTid()
        );
        ArrayList<Item> items = currTransaction.getItems();
        // TODO: also look into UI things for implementing decorator pattern
        adapter.setItems(items);
        itemsRecView.setAdapter(adapter);
        itemsRecView.setLayoutManager(new LinearLayoutManager(this));

        mSocket.emit("startSession", request.getJson()).on("currentState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            JSONArray data = (JSONArray) ((JSONObject) args[0]).get("items");
                            for (int k = 0; k < data.length(); k++) {
                                JSONObject j = (JSONObject) data.get(k);
                                int item_id = Integer.parseInt((String) j.get("item_id"));
                                Item i = items.stream().filter(item -> item.getItem_id() == item_id).findFirst().get();
                                i.updateSelectedBy((JSONArray) j.get("userInfos"));
                            }
                            System.out.println(args[0]);
                            System.out.println(items.get(0).getSelectedUsers());
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        // listener for items being modified
        mSocket.on("itemUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            int item_id = (int) data.get("item_id");
                            for (int i = 0; i < items.size(); i++) {
                                Item item = items.get(i);
                                if (item.getItem_id() == item_id) {
                                    item.updateSelectedBy((JSONArray) data.get("userInfos"));
                                    items.set(i, item);
                                    adapter.setItems(items);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
