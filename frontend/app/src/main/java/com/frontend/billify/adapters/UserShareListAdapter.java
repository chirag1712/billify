package com.frontend.billify.adapters;

import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.frontend.billify.R;

import java.util.ArrayList;

public class UserShareListAdapter extends ArrayAdapter<Pair<String, Integer>> {
    private static final String LOG_TAG = UserShareListAdapter.class.getSimpleName();

    public UserShareListAdapter(Activity context, ArrayList<Pair<String, Integer>> user_shares) {
        super(context,0,user_shares);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_transaction_share, parent, false);
        }
        Pair<String, Integer> current_user_share = getItem(position);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.username);
        nameTextView.setText(current_user_share.first);

        TextView priceTextView = (TextView) listItemView.findViewById(R.id.user_transaction_share);
        priceTextView.setText(current_user_share.second.toString());

        return listItemView;
    }
}
