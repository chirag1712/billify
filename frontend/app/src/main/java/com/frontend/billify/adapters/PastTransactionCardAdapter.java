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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.models.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PastTransactionCardAdapter extends RecyclerView.Adapter<PastTransactionCardAdapter.ViewHolder>{

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

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.transaction_label.setText(transactions.get(position).getName());
        holder.total.setText(transactions.get(position).getTid());


        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Pair<String,Integer>> user_shares = new ArrayList<Pair<String,Integer>>();
                user_shares.add(new Pair<String,Integer>("Alric",20));
                user_shares.add(new Pair<String,Integer>("Huy",40));
                user_shares.add(new Pair<String,Integer>("Pranav",12));
                user_shares.add(new Pair<String,Integer>("Denis",45));
                user_shares.add(new Pair<String,Integer>("Chirag",32));
                user_shares.add(new Pair<String,Integer>("Mayank",25));
                ListView price_share_view = (ListView) holder.hiddenView.getChildAt(0);
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

    public void setItems(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        System.out.println(transactions.toString());
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView transaction_label, total;
        private CardView parent;
        private RelativeLayout hiddenView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transaction_label = itemView.findViewById(R.id.transaction_label);
            total = itemView.findViewById(R.id.total_transaction_price);
            hiddenView = itemView.findViewById(R.id.hidden_shares_and_buttons);
            parent = itemView.findViewById(R.id.past_transaction);
        }
    }
}