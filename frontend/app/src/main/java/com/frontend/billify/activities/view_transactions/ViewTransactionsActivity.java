package com.frontend.billify.activities.view_transactions;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.frontend.billify.R;
import com.frontend.billify.design_patterns.chart_render_strategies.ChartCurrencyRenderStrategy;
import com.frontend.billify.design_patterns.chart_render_strategies.ChartPercentRenderStrategy;
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
        transactionLabelMap.put(1, new Label(1, "Food", "Red", 1, "Pokebox", 10.5f));
        transactionLabelMap.put(2, new Label(2, "Shopping", "Blue", 2, "Walmart", 100.1f));
        transactionLabelMap.put(3, new Label(3, "Entertainment", "Darkgray", 3, "Netflix", 14.7f));
        transactionLabelMap.put(4, new Label(4, "Electronics", "Magenta", 4, "RAM card", 320.6f));
        transactionLabelMap.put(5, new Label(5, "Housing", "Lightgray", 5, "Rent", 872f));

        // TODO: Connect to API to fetch all labels and their total cost given user
        Map<Integer, Pair<Label, Float>> labelTotalMap = new HashMap<>();
        labelTotalMap.put(1, new Pair<>(new Label(1, "Food" ,"Red"), 10.5f));
        labelTotalMap.put(2, new Pair<>(new Label(2, "Shopping" ,"Blue"), 100.1f));
        labelTotalMap.put(3, new Pair<>(new Label(3, "Entertainment" ,"Darkgray"), 14.7f));
        labelTotalMap.put(4, new Pair<>(new Label(4, "Electronics" ,"Magenta"), 320.6f));
        labelTotalMap.put(5, new Pair<>(new Label(5, "Housing" ,"Lightgray"), 872f));

        // The observer. Gets notified when a transaction label is changed and update the chart
        chart = new TransactionChart(findViewById(R.id.transaction_chart), transactionLabelMap,
                labelTotalMap, new ChartPercentRenderStrategy());

        // The subject. Notifies the chart whenever a transaction label is changed
        transactionView = new TransactionView(findViewById(R.id.transaction_list), this,
                transactionLabelMap, labelTotalMap);
        transactionView.AttachObserver(chart);

        backButton.setOnClickListener(view -> finish());
    }

    public void onCurrencyFormatButton(View v) {
        chart.setRenderStrategy(new ChartCurrencyRenderStrategy());
    }

    public void onPercentFormatButton(View v) {
        chart.setRenderStrategy(new ChartPercentRenderStrategy());
    }
}