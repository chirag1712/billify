package com.frontend.billify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EditItemsRecViewAdapter extends RecyclerView.Adapter<EditItemsRecViewAdapter.EditItemsViewHolder> {

    ArrayList<String> itemNames;
    ArrayList<Float> itemPrices;
    Context context;
    private OnItemClickListener onItemClickListener;

    public EditItemsRecViewAdapter(Context context, ArrayList<String> itemNames, ArrayList<Float> itemPrices) {
        this.context = context;
        this.itemNames = itemNames;
        this.itemPrices = itemPrices;
    }

    @NonNull
    @NotNull
    @Override
    public EditItemsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.edit_item_row, parent, false);
        return new EditItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EditItemsViewHolder holder, int position) {
        holder.itemName.setText(itemNames.get(position));
        holder.price.setText(String.valueOf(itemPrices.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public class EditItemsViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView price;
        ConstraintLayout itemLayout;
        public EditItemsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.itemName = itemView.findViewById(R.id.edit_item_name);
            this.price = itemView.findViewById(R.id.edit_item_price);
            itemLayout = itemView.findViewById(R.id.item_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    // onItemClickListener can be null if not set with setOnClickListener
                    // position can be NO_POSITION if we click on an item when delete animation not finished
                    if ((onItemClickListener != null) && (position != RecyclerView.NO_POSITION)) {
                        onItemClickListener.onItemClick(
                                itemNames.get(position),
                                itemPrices.get(position),
                                position
                                );
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String itemName, float itemPrice, int itemIndex);
    }

    // list view provides these methods by default, recycler view doesn't
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
