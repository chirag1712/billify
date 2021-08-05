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
import com.frontend.billify.models.UserTransaction;
import com.frontend.billify.persistence.Persistence;

import java.util.ArrayList;
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

        // Map from tid to UserTransaction
        Map<Integer, UserTransaction> tidToUserTransactionMap = new HashMap<Integer, UserTransaction>();
        tidToUserTransactionMap.put(1, new UserTransaction(1, "Pokebox",
                new Label(1, "Unlabelled" ,"#F0A500"), 10.5f)
        );
        tidToUserTransactionMap.put(2, new UserTransaction(2, "Walmart",
                new Label(4, "Groceries", "#1EAE98"), 100.1f)
        );
        tidToUserTransactionMap.put(3, new UserTransaction(3, "Netflix",
                new Label(3, "Entertainment" ,"#035397"), 14.7f)
        );

        tidToUserTransactionMap.put(4, new UserTransaction(4, "RAM card",
                new Label(6, "Electronics" ,"#D62AD0"), 320.6f)
        );

        tidToUserTransactionMap.put(5, new UserTransaction(5, "Rent",
                new Label(7, "Housing" ,"#6930C3"), 872f)
        );

        // TODO: Connect to API to fetch all labels and their total cost given user
        // Maps from label id to Pair of Label and corresponding total transaction amount
        Map<Integer, Pair<Label, Float>> labelTotalMap = new HashMap<>();

        ArrayList<Label> uniqueLabels = Label.getUniqueLabels();

        for (Label uniqueLabel: uniqueLabels) {
            int lid = uniqueLabel.getLId();
            labelTotalMap.put(lid, new Pair<>(uniqueLabel, 0f));
        }

        for (UserTransaction transaction: tidToUserTransactionMap.values()) {
            int transactionLabelId = transaction.getLabel().getLId();
            float currVal = labelTotalMap.get(transactionLabelId).second;
            Label currLabel = labelTotalMap.get(transactionLabelId).first;
            labelTotalMap.put(transaction.getLabel().getLId(),
                    new Pair(currLabel, currVal + transaction.getPrice_share()));
        }

        ArrayList<Label> labels = new ArrayList<>();
        for (Pair<Label, Float> labelTotalPair: labelTotalMap.values()) {
            labels.add(labelTotalPair.first);
        }

        // The observer. Gets notified when a transaction label is changed and update the chart
        chart = new TransactionChart(findViewById(R.id.transaction_chart), tidToUserTransactionMap,
                labelTotalMap,
                new ChartPercentRenderStrategy());

        // The subject. Notifies the chart whenever a transaction label is changed
        transactionView = new TransactionView(
                findViewById(R.id.transaction_list),
                this,
                new ArrayList<>(tidToUserTransactionMap.values()),
                labels);
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