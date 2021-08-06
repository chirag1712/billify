package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.adapters.ReceiptsItemsRecViewAdapter;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserPriceShareView {
    private final RecyclerView view;
    private final Context context;
    private final Socket mSocket;

    public UserPriceShareView(RecyclerView view, Context context, Socket mSocket) {
        this.view = view;
        this.context = context;
        this.mSocket = mSocket;
        populateView();
    }

    private void populateView() {
        // recycler view setup for itemized view for transaction items
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(
                this.context, mSocket, new User(uid, userName), this.transaction.getTid()
        );
        ArrayList<Item> items = this.transaction.getItems();
        adapter.setItems(items);
        this.view.setAdapter(adapter);
        this.view.setLayoutManager(new LinearLayoutManager(this.context));

        Activity activity = (Activity) this.context;
        mSocket.on("currentState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) ((JSONObject) args[0]).get("price_shares");
                            System.out.println(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSocket.on("itemUpdated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            JSONObject itemUpdated = (JSONObject) args[0];
                            int item_id = (int) itemUpdated.get("item_id");
                            JSONArray userInfos = (JSONArray) itemUpdated.get("userInfos");
                            System.out.println(item_id);
                            System.out.println(userInfos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
