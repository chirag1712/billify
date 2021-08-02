package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.adapters.ReceiptsItemsRecViewAdapter;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.StartSession;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ItemizedView {
    private final RecyclerView view;
    private final Context context;
    private final Socket mSocket;
    private final Transaction transaction;

    public ItemizedView(RecyclerView view, Context context, Socket mSocket, Transaction transaction) {
        this.view = view;
        this.context = context;
        this.mSocket = mSocket;
        this.transaction = transaction;

        populateView();
    }

    private void populateView() {
        int uid = Persistence.getUserId(this.context);
        String userName = Persistence.getUserName(this.context);

        // recycler view setup for itemized view
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(
                this.context, mSocket, new User(uid, userName), this.transaction.getTid()
        );
        ArrayList<Item> items = this.transaction.getItems();
        // TODO: also look into UI things for implementing decorator pattern
        adapter.setItems(items);
        this.view.setAdapter(adapter);
        this.view.setLayoutManager(new LinearLayoutManager(this.context));
        Activity a = (Activity) this.context;

        // ==== SOCKET EVENTS =====
        // client joins session and server pushes the current state of the session
        StartSession request = new StartSession(new User(uid, userName), this.transaction.getTid());
        mSocket.emit("startSession", request.getJson()).on("currentState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                a.runOnUiThread(new Runnable() {
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
                a.runOnUiThread(new Runnable() {
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

}
