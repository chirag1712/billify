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
import com.frontend.billify.models.Item;
import com.frontend.billify.models.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EditItemsRecViewAdapter extends RecyclerView.Adapter<EditItemsRecViewAdapter.EditItemsViewHolder> {

//    ArrayList<String> itemNames;
//    ArrayList<Float> itemPrices;
    Transaction transaction;
    Context context;
    private OnItemClickListener onItemClickListener;

    public EditItemsRecViewAdapter(Context context, Transaction transaction) {
        this.context = context;
        this.transaction = transaction;
//        this.itemNames = itemNames;
//        this.itemPrices = itemPrices;
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
        holder.itemName.setText(transaction.getItems().get(position).getName());
        holder.price.setText((transaction.getItems().get(position).getStrPrice()));
    }

    @Override
    public int getItemCount() {
        return transaction.getItems().size();
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
                                transaction.getItems().get(position),
                                position
                                );
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Item item, int itemIndex);
    }

    // list view provides these methods by default, recycler view doesn't
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
