package com.frontend.billify.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PriceShareRecViewAdapter extends RecyclerView.Adapter<PriceShareRecViewAdapter.ViewHolder>{
    @NonNull
    @Override
    public PriceShareRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PriceShareRecViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView user_name, price_share;
        private final CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
