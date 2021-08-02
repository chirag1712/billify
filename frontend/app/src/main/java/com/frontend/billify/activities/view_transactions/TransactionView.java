package com.frontend.billify.activities.view_transactions;

import android.content.Context;
import android.util.Pair;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.adapters.TransactionLabelAdapter;
import com.frontend.billify.design_patterns.observer.Subject;
import com.frontend.billify.helpers.callback.ICallback;
import com.frontend.billify.models.Label;
import com.frontend.billify.models.UserTransaction;

import java.util.ArrayList;
import java.util.Map;

public class TransactionView extends Subject implements ICallback {

    private final RecyclerView view;
    private final Context context;
    private TransactionLabelAdapter adapter;

    public TransactionView(RecyclerView view, Context context,
                           ArrayList<UserTransaction> transactionLabelMap,
                           ArrayList<Label> labelsToDisplay) {
        this.view = view;
        this.context = context;
        populateView(transactionLabelMap, labelsToDisplay);
    }

    private void populateView(ArrayList<UserTransaction> transactionLabelMap,
                              ArrayList<Label> labelsToDisplay){


        // Add this as callback to be notified when a label is changed
        adapter = new TransactionLabelAdapter(
                transactionLabelMap,
                labelsToDisplay,
                this);
        view.setAdapter(adapter);

        // LinearLayoutManager renders linearly (duh)
        view.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void callback(Object o) {
        // Notify observer that a label is changed and pass that label
        super.Notify(o);

        //TODO: Call API for label change
    }
}
