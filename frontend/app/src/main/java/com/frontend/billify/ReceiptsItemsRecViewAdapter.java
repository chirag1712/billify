package com.frontend.billify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReceiptsItemsRecViewAdapter extends RecyclerView.Adapter<ReceiptsItemsRecViewAdapter.ViewHolder>{

    private ArrayList<ReceiptItem> items = new ArrayList<>();

    public ReceiptsItemsRecViewAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.txt.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<ReceiptItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView txt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.receiptItem);
        }
    }
}
