package com.frontend.billify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.activities.EditSpecificItemActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EditItemsRecViewAdapter extends RecyclerView.Adapter<EditItemsRecViewAdapter.EditItemsViewHolder> {

    ArrayList<String> items;
    ArrayList<String> prices;
    Context context;
    public EditItemsRecViewAdapter(Context context, ArrayList<String> items, ArrayList<String> prices) {
        this.context = context;
        this.items = items;
        this.prices = prices;
    }

    @NonNull
    @NotNull
    @Override
    public EditItemsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_item_row, parent, false);
        return new EditItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EditItemsViewHolder holder, int position) {
        holder.item.setText(items.get(position));
        holder.price.setText(prices.get(position));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSpecificItemActivity.class);
                intent.putExtra("item_name", items.get(position));
                intent.putExtra("item_price", prices.get(position));
                intent.putExtra("item_index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class EditItemsViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        TextView price;
        ConstraintLayout itemLayout;
        public EditItemsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.item = itemView.findViewById(R.id.edit_item_name);
            this.price = itemView.findViewById(R.id.edit_item_price);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }
    }
}
