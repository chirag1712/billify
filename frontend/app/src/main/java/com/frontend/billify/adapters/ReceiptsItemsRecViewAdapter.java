package com.frontend.billify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.ItemSelecter;
import com.frontend.billify.models.StartSession;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.socket.client.Socket;

public class ReceiptsItemsRecViewAdapter extends RecyclerView.Adapter<ReceiptsItemsRecViewAdapter.ViewHolder>{

    private String userName;
    private int tid;
    private int uid;
    private Socket mSocket;
    private ArrayList<Item> items = new ArrayList<>();
    private Context context;

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
        holder.price.setText(items.get(position).getStrPrice());
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fire socket event to select/deselect item based on whether
                // the uid was present in hashmap or not
                ItemSelecter request = new ItemSelecter(new User(uid, userName), tid, items.get(position).getItem_id());

                if (items.get(position).isSelectedBy(uid)) {
                    items.get(position).deselect(uid);
                    mSocket.emit("deselectItem", request.getJson());
                    Toast.makeText(context, items.get(position).getName() + " Deselected", Toast.LENGTH_SHORT).show();
                } else {
                    items.get(position).select(uid, userName);
                    mSocket.emit("selectItem", request.getJson());
                    // can remove this toast once sockets start working properly
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

    public class ViewHolder  extends  RecyclerView.ViewHolder{
        private TextView item_name, price;
        private CardView parent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.receiptItem);
            price = itemView.findViewById(R.id.itemPrice);
            parent = itemView.findViewById(R.id.receiptItemsParent);
        }
    }
}
