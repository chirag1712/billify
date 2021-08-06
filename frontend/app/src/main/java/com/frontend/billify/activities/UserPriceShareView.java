package com.frontend.billify.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.frontend.billify.R;
import com.frontend.billify.adapters.PriceShareAdapter;
import com.frontend.billify.models.UserTransactionShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class UserPriceShareView {
    private final ListView view;
    private final Context context;
    private final Socket mSocket;

    public UserPriceShareView(ListView view, Context context, Socket mSocket) {
        this.view = view;
        this.context = context;
        this.mSocket = mSocket;
        populateView();
    }

    private void populateView() {
        ArrayList<UserTransactionShare> userPriceShares = new ArrayList<>();
        PriceShareAdapter adapter = new PriceShareAdapter(this.context, R.layout.user_price_share, userPriceShares);
        this.view.setAdapter(adapter);
        Activity activity = (Activity) this.context;

        // ==== SOCKET EVENT LISTENERS =====
        mSocket.on("currentState", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) ((JSONObject) args[0]).get("price_shares");
                            populatePriceShares(adapter, userPriceShares, data);
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
                            JSONObject data = (JSONObject) ((JSONObject) args[0]).get("price_shares");
                            populatePriceShares(adapter, userPriceShares, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void populatePriceShares(PriceShareAdapter adapter, ArrayList<UserTransactionShare> userPriceShares, JSONObject data) throws JSONException {
        userPriceShares.clear();
        Iterator<String> keys = data.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if (data.get(key) instanceof JSONObject) {
                JSONObject userPriceShare = (JSONObject) data.get(key);
                userPriceShares.add(new UserTransactionShare(Integer.parseInt(key),
                        (String) userPriceShare.get("userName"),
                        Float.parseFloat(String.valueOf(userPriceShare.get("price_share")))
                ));
            }
        }
        adapter.notifyDataSetChanged();
    }
}
