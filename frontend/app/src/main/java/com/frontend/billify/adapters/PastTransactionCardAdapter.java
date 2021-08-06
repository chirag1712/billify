package com.frontend.billify.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.activities.GroupTransactionActivity;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.TransactionSummary;
import com.frontend.billify.models.UserTransactionShare;
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

    private int first_click_pos = -1;

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

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.transaction_label.setText(transactions.get(position).getName());
        holder.date.setText(transactions.get(position).getName());


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(first_click_pos != position){
                    getUserShares(transactions.get(position).getTid(),holder.hiddenView);
                    first_click_pos = position;
                }
                else{
                    System.out.println(user_shares.toString());
                    if (holder.hiddenView.getVisibility() == View.VISIBLE) {

                        // The transition of the hiddenView is carried out
                        //  by the TransitionManager class.
                        // Here we use an object of the AutoTransition
                        // Class to create a default transition.
                        holder.hiddenView.setVisibility(View.GONE);

                    }

                    // If the CardView is not expanded, set its visibility
                    // to visible and change the expand more icon to expand less.
                    else {
                        holder.hiddenView.setVisibility(View.VISIBLE);
                    }
                }


//                ArrayList<Pair<String,Integer>> user_shares = new ArrayList<Pair<String,Integer>>();
//                user_shares.add(new Pair<String,Integer>("Alric",20));
//                user_shares.add(new Pair<String,Integer>("Huy",40));
//                user_shares.add(new Pair<String,Integer>("Pranav",12));
//                user_shares.add(new Pair<String,Integer>("Denis",45));
//                user_shares.add(new Pair<String,Integer>("Chirag",32));
//                user_shares.add(new Pair<String,Integer>("Mayank",25));
//                ListView price_share_view = (ListView) holder.hiddenView.getChildAt(0);
//                UserShareListAdapter usersharelistadapter = new UserShareListAdapter ((Activity) context, user_shares);
//                price_share_view.setAdapter(usersharelistadapter);
//                price_share_view.setEnabled(false);
//                ViewGroup vg = price_share_view;
//                View listItem = usersharelistadapter.getView(0,null,vg);
//                listItem.measure(0,0);
//                int listItemHeight = listItem.getMeasuredHeight();
//                ViewGroup.LayoutParams param = price_share_view.getLayoutParams();
//                param.height = listItemHeight*(usersharelistadapter.getCount()) + (price_share_view.getDividerHeight() * (usersharelistadapter.getCount()-1));
//                price_share_view.setLayoutParams(param);

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_label = itemView.findViewById(R.id.transaction_label);
            date = itemView.findViewById(R.id.transaction_date);
            hiddenView = itemView.findViewById(R.id.hidden_shares_and_buttons);
            parent = itemView.findViewById(R.id.past_transaction);
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
                        "Cannot connect to login server", Toast.LENGTH_LONG).show();
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
        transaction_total.setText(Float.toString(transactionSummary.getTotalPrice()));
        if (hiddenView.getVisibility() == View.VISIBLE) {

            // Collapse the card if expanded
            hiddenView.setVisibility(View.GONE);

        }


        else {

            //expand the card if collapsed
            hiddenView.setVisibility(View.VISIBLE);
        }
    }
}