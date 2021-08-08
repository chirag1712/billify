package com.frontend.billify.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Pair;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.frontend.billify.R;
import com.frontend.billify.models.UserTransactionShare;

import java.util.ArrayList;

public class UserShareListAdapter extends ArrayAdapter<UserTransactionShare> {
    private static final String LOG_TAG = UserShareListAdapter.class.getSimpleName();

    public UserShareListAdapter(Activity context, ArrayList<UserTransactionShare> user_shares) {
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
        if (!isEmpty()) {
            UserTransactionShare current_user_share = getItem(position);
            TextView nameTextView = (TextView) listItemView.findViewById(R.id.username);
            nameTextView.setText(current_user_share.getUserName());

            TextView priceTextView = (TextView) listItemView.findViewById(R.id.user_transaction_share);
            priceTextView.setText("$" + String.valueOf(current_user_share.getPriceShare()));

            if (current_user_share.isSettled()) {
                setTextViewtoSettleMode(nameTextView);
                setTextViewtoSettleMode(priceTextView);
//                priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }
        }

        return listItemView;
    }

    private void setTextViewtoSettleMode(TextView textView) {
        textView.setTextColor(Color.parseColor("#3DDC84"));
    }
}
