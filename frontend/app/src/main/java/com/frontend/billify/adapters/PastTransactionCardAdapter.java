package com.frontend.billify.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.frontend.billify.R;
import com.frontend.billify.activities.BillifySessionActivity;
import com.frontend.billify.activities.ViewGroupActivity;
import com.frontend.billify.activities.ViewReceiptImageActivity;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Group;
import com.frontend.billify.models.SettleResponse;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.TransactionSummary;
import com.frontend.billify.models.UserTransactionShare;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastTransactionCardAdapter extends RecyclerView.Adapter<PastTransactionCardAdapter.ViewHolder>{
    private final RetrofitService retrofitService = new RetrofitService();
    private final TransactionController transactionController = new TransactionController(retrofitService);
    private ArrayList<UserTransactionShare> user_shares = new ArrayList<UserTransactionShare>();

    private ArrayList<Transaction> transactions = new ArrayList<Transaction>();

    private Context context;

    public PastTransactionCardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_transaction, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Transaction curTransaction = transactions.get(position);

        holder.transaction_label.setText(curTransaction.getName());

        holder.date.setText(curTransaction.getFormattedT_date());

        holder.joinBillifySession.setOnClickListener(view -> {
            holder.hiddenView.setVisibility(View.GONE);

            transactionController.getTransaction(curTransaction.getTid()).enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    Transaction fullTransaction = new Transaction(response.body());
                    fullTransaction.printItems();
                    Intent moveToItemizedScreenIntent = new Intent(
                            context,
                            BillifySessionActivity.class
                    );
                    Bundle transactionBundle = new Bundle();
                    transactionBundle.putSerializable("SerializedTransaction", fullTransaction);
                    moveToItemizedScreenIntent.putExtra(
                            "TransactionBundle",
                            transactionBundle
                    );
                    context.startActivity(moveToItemizedScreenIntent);
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    Toast.makeText(context,
                            "Failed joining the billify session",
                            Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });

        holder.viewReceiptBtn.setOnClickListener(view -> {
            Intent viewReceiptImgIntent = new Intent(context,
                    ViewReceiptImageActivity.class);
            viewReceiptImgIntent.putExtra("receipt_img", curTransaction.getReceipt_img());
            context.startActivity(viewReceiptImgIntent);
        });

        holder.settleBtn.setOnClickListener(view -> {
            int uid = Persistence.getUserId(context);
            transactionController.settleTransaction(uid, curTransaction.getTid()).enqueue(new Callback<SettleResponse>() {

                @Override
                public void onResponse(Call<SettleResponse> call, Response<SettleResponse> response) {
                    Toast.makeText(context,
                            "Transaction settled successfully!", Toast.LENGTH_LONG).show();
                    holder.hiddenView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<SettleResponse> call, Throwable t) {
                    Toast.makeText(context,
                            "Settle transaction failed", Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });

        holder.parent.setOnClickListener(view -> {
            if (holder.hiddenView.getVisibility() == View.GONE) {
                getUserShares(curTransaction.getTid(),holder.hiddenView);
            } else {
                holder.hiddenView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setItems(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        System.out.println(transactions.toString());
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView transaction_label, date;
        private CardView parent;
        private RelativeLayout hiddenView;
        private Button joinBillifySession;
        private Button viewReceiptBtn;
        private Button settleBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_label = itemView.findViewById(R.id.transaction_label);
            date = itemView.findViewById(R.id.transaction_date);
            hiddenView = itemView.findViewById(R.id.hidden_shares_and_buttons);
            parent = itemView.findViewById(R.id.past_transaction);
            joinBillifySession = itemView.findViewById(R.id.join_billify_session);
            viewReceiptBtn = itemView.findViewById(R.id.view_receipt);
            settleBtn = itemView.findViewById(R.id.settle_share);
        }
    }

    public void getUserShares(int tid,RelativeLayout hiddenView){
        System.out.println("tid is "+tid);
        transactionController.getUserTransactionShare(tid).enqueue(new Callback<TransactionSummary>() {
            @Override
            public void onResponse(Call<TransactionSummary> call, Response<TransactionSummary> response) {
                if (!response.isSuccessful()) {
                    try {
                        JSONObject error = new JSONObject(response.errorBody().string());
                        Toast.makeText((Activity) context,
                                error.getString("error"),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText((Activity) context,
                                "Sorry :( Something went wrong.",
                                Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                TransactionSummary transactionSummary = response.body();
                setUserShares(transactionSummary,hiddenView);
            }

            @Override
            public void onFailure(Call<TransactionSummary> call, Throwable t) {
                Toast.makeText(context,
                        "Cannot connect to backend server", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });

    }
    public void setUserShares(TransactionSummary transactionSummary,RelativeLayout hiddenView){
        user_shares = transactionSummary.getUserPriceShares();

        ListView price_share_view = (ListView) hiddenView.getChildAt(0);

        UserShareListAdapter usersharelistadapter = new UserShareListAdapter ((Activity) context, user_shares);
        price_share_view.setAdapter(usersharelistadapter);
        price_share_view.setEnabled(false);
        ViewGroup vg = price_share_view;
        View listItem = usersharelistadapter.getView(0,null,vg);
        listItem.measure(0,0);
        int listItemHeight = listItem.getMeasuredHeight();
        ViewGroup.LayoutParams param = price_share_view.getLayoutParams();
        param.height = listItemHeight*(usersharelistadapter.getCount()) + (price_share_view.getDividerHeight() * (usersharelistadapter.getCount()-1));
        price_share_view.setLayoutParams(param);

        //Set the total for the transaction
        TextView transaction_total = (TextView) hiddenView.getChildAt(2);
        transaction_total.setText("$" + transactionSummary.getTotalPrice());
        if (hiddenView.getVisibility() == View.VISIBLE) {
            // Collapse the card if expanded
            hiddenView.setVisibility(View.GONE);
        } else {
            //expand the card if collapsed
            hiddenView.setVisibility(View.VISIBLE);
        }
    }
}