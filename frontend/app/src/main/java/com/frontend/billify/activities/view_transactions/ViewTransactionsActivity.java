package com.frontend.billify.activities.view_transactions;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.frontend.billify.R;
import com.frontend.billify.models.Label;
import com.frontend.billify.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class ViewTransactionsActivity extends AppCompatActivity {
    private TransactionChart chart;
    private TransactionView transactionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        Button backButton = findViewById(R.id.back_button);
        int userId = Persistence.getUserId(this);

        // TODO: Connect to API to fetch all transactions of a user
        Map<Integer, Label> transactionLabelMap = new HashMap<>();
        transactionLabelMap.put(1, new Label(1, "Food", "Red", 1, "Pokebox"));
        transactionLabelMap.put(2, new Label(2, "Shopping", "Blue", 2, "Walmart"));
        transactionLabelMap.put(3, new Label(3, "Entertainment", "Darkgray", 3, "Netflix"));
        transactionLabelMap.put(4, new Label(4, "Electronics", "Magenta", 4, "RAM card"));
        transactionLabelMap.put(5, new Label(5, "Housing", "Lightgray", 5, "Rent"));

        // TODO: Connect to API to fetch all labels and their count given user
        Map<Integer, Pair<Label, Integer>> labelCountMap = new HashMap<>();
        labelCountMap.put(1, new Pair<>(new Label(1, "Food" ,"Red"), 1));
        labelCountMap.put(2, new Pair<>(new Label(2, "Shopping" ,"Blue"), 1));
        labelCountMap.put(3, new Pair<>(new Label(3, "Entertainment" ,"Darkgray"), 1));
        labelCountMap.put(4, new Pair<>(new Label(4, "Electronics" ,"Magenta"), 1));
        labelCountMap.put(5, new Pair<>(new Label(5, "Housing" ,"Lightgray"), 1));

        // The observer. Gets notified when a transaction label is changed and update the chart
        chart = new TransactionChart(findViewById(R.id.transaction_chart), transactionLabelMap,
                labelCountMap);

        // The subject. Notifies the chart whenever a transaction label is changed
        transactionView = new TransactionView(findViewById(R.id.transaction_list), this,
                transactionLabelMap, labelCountMap);
        transactionView.AttachObserver(chart);

        backButton.setOnClickListener(view -> finish());
    }
}