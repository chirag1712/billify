package com.frontend.billify.models;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.models.Item;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import android.transition.TransitionManager;

public class PastTransactionCardAdapter extends RecyclerView.Adapter<PastTransactionCardAdapter.ViewHolder>{

    private ArrayList<Pair<String,Integer>> transactions = new ArrayList<Pair<String,Integer>>();
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

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.transaction_label.setText(transactions.get(position).first);
        holder.total.setText(transactions.get(position).second);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setItems(ArrayList<Item> items) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView transaction_label, total;
        private CardView parent;
        private View hiddenView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_label = itemView.findViewById(R.id.transaction_label);
            total = itemView.findViewById(R.id.total_transaction_price);
            hiddenView = itemView.findViewById(R.id.user_price_shares);
            parent = itemView.findViewById(R.id.past_transaction);
        }
    }
}