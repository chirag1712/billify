package com.frontend.billify.activities.view_transactions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.frontend.billify.R;
import com.frontend.billify.activities.HomepageActivity;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.design_patterns.chart_render_strategies.ChartCurrencyRenderStrategy;
import com.frontend.billify.design_patterns.chart_render_strategies.ChartPercentRenderStrategy;
import com.frontend.billify.models.Label;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.UserTransaction;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewTransactionsActivity extends AppCompatActivity {
    private TransactionChart chart;
    private TransactionView transactionView;
    private ArrayList<UserTransaction> userTransactions = new ArrayList<>();

    private final RetrofitService retrofitService = new RetrofitService();
    private final TransactionController transactionController = new TransactionController(retrofitService);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        Button backButton = findViewById(R.id.back_button);
        int userId = Persistence.getUserId(this);

        ArrayList<UserTransaction> userTransactions = new ArrayList<>();

        transactionController.getUserTransactionDetails(userId).enqueue(new Callback<ArrayList<UserTransaction>>() {
            @Override
            public void onResponse(Call<ArrayList<UserTransaction>> call, Response<ArrayList<UserTransaction>> response) {

                for (UserTransaction resUserTransaction: response.body()) {
                    UserTransaction userTransaction = new UserTransaction(resUserTransaction);
                    userTransactions.add(userTransaction);
                }
                System.out.println(
                        "Successful homepage view transaction request with return value: "
                                + userTransactions.size()
                );
            }

            @Override
            public void onFailure(Call<ArrayList<UserTransaction>> call, Throwable t) {
                System.out.println("checkpoint 1 ---- ");
            }
        });

        // TODO: Connect to API to fetch all transactions of a user

        System.out.println("checkpoint 1 ---- ");
        // Map from tid to UserTransaction
        Map<Integer, UserTransaction> tidToUserTransactionMap = new HashMap<>();
        System.out.println("checkpoint 2 ---- " +  userTransactions.size());

//        for (UserTransaction userTransaction: userTransactions) {
//            System.out.println("checkpoint 3 ---- ");
//            tidToUserTransactionMap.put(userTransaction.getTid(), userTransaction);
//            userTransaction.printUserTransaction();
//            count--;
//        }

        tidToUserTransactionMap.put(1, new UserTransaction(349, 7, "Pokebox",
                new Label(2, "Food" ,"#FF4848"), 10.5f)
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactionController.updateUserTransactionLabels(new ArrayList<>(tidToUserTransactionMap.values())).enqueue(new Callback<ArrayList<UserTransaction>>() {
                    @Override
                    public void onResponse(Call<ArrayList<UserTransaction>> call, Response<ArrayList<UserTransaction>> response) {

//                        for (UserTransaction resUserTransaction: response.body()) {
//                            UserTransaction userTransaction = new UserTransaction(resUserTransaction);
//                            userTransactions.add(userTransaction);
//                        }
                        System.out.println(
                                "resssssss--------: "
                                        + response.body().toString()
                        );
                    }

                    @Override
                    public void onFailure(Call<ArrayList<UserTransaction>> call, Throwable t) {
                        System.out.println("checkpoint 1 ---- ");
                    }
                });
            }
        });
    }

    public void onCurrencyFormatButton(View v) {
        chart.setRenderStrategy(new ChartCurrencyRenderStrategy());
    }

    public void onPercentFormatButton(View v) {
        chart.setRenderStrategy(new ChartPercentRenderStrategy());
    }
}