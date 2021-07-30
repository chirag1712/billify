package com.frontend.billify.activities.view_transactions;

import android.content.Context;
import android.util.Pair;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.adapters.TransactionLabelAdapter;
import com.frontend.billify.design_patterns.observer.Subject;
import com.frontend.billify.helpers.callback.ICallback;
import com.frontend.billify.models.Label;

import java.util.ArrayList;
import java.util.Map;

public class TransactionView extends Subject implements ICallback {

    private final RecyclerView view;
    private final Context context;

    public TransactionView(RecyclerView view, Context context,
                           Map<Integer, Label> transactionLabelMap,
                           Map<Integer, Pair<Label, Integer>> labelCountMap) {
        this.view = view;
        this.context = context;
        populateView(transactionLabelMap, labelCountMap);
    }

    private void populateView(Map<Integer, Label> transactionLabelMap,
                              Map<Integer, Pair<Label, Integer>> labelCountMap){

        // Look at TransactionLabelAdapter.java for description
        ArrayList<String> transactionNames = new ArrayList<>();
        ArrayList<Label> transactionLabels = new ArrayList<>(transactionLabelMap.values());
        ArrayList<Label> labelsToDisplay = new ArrayList<>();

        for (Label label : transactionLabelMap.values()) {
            transactionNames.add(label.getTransaction_name());
        }

        for (Pair<Label, Integer> entry : labelCountMap.values()) {
            labelsToDisplay.add(entry.first);
        }

        // Add this as callback to be notified when a label is changed
        TransactionLabelAdapter adapter = new TransactionLabelAdapter(transactionNames,
                transactionLabels, labelsToDisplay, this);
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
