package com.frontend.billify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.ItemSelecter;
import com.frontend.billify.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.socket.client.Socket;

public class ReceiptsItemsRecViewAdapter extends RecyclerView.Adapter<ReceiptsItemsRecViewAdapter.ViewHolder>{

    private final String userName;
    private final int tid;
    private final int uid;
    private final Socket mSocket;
    private ArrayList<Item> items = new ArrayList<>();
    private final Context context;

    public ReceiptsItemsRecViewAdapter(Context context, Socket mSocket, User u, int tid) {
        this.context = context;
        this.mSocket = mSocket;
        this.uid = u.getId();
        this.userName = u.getUserName();
        this.tid = tid;
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
        holder.item_name.setText(items.get(position).getName());
        holder.price.setText("$" + items.get(position).getStrPrice());

        // setting the grid layout with items
        holder.grid.removeAllViews();
        int total = items.get(position).getSelectedUsers().size();
        int column = 3;
        int row = total / column;
        holder.grid.setColumnCount(column);
        holder.grid.setRowCount(row + 1); // +1 to avoid an edge case iirc
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            TextView userText = new TextView(this.context);
            userText.setText(items.get(position).getSelectedUsers().get(i));
            userText.setPadding(10, 10, 10, 10);
            userText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            GridLayout.Spec rowSpan = GridLayout.spec(r, 1);
            GridLayout.Spec colSpan = GridLayout.spec(c, 1);

            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colSpan);
            holder.grid.addView(userText, gridParam);
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fire socket event to select/deselect item based on whether
                // the uid was present in hashmap or not
                ItemSelecter request = new ItemSelecter(new User(uid, userName), tid, items.get(position).getItem_id());

                // updates for sender separately to avoid any latency
                if (items.get(position).isSelectedBy(uid)) {
                    items.get(position).deselect(uid);
                    mSocket.emit("deselectItem", request.getJson());
                    Toast.makeText(context, items.get(position).getName() + " Deselected", Toast.LENGTH_SHORT).show();
                } else {
                    items.get(position).select(uid, userName);
                    mSocket.emit("selectItem", request.getJson());
                    Toast.makeText(context, items.get(position).getName() + " Selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView item_name, price;
        private final CardView parent;
        private final GridLayout grid;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.receiptItem);
            price = itemView.findViewById(R.id.itemPrice);
            parent = itemView.findViewById(R.id.receiptItemsParent);
            grid = itemView.findViewById(R.id.selected_users);
        }
    }
}
