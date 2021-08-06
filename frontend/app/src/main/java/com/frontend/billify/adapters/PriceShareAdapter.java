package com.frontend.billify.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.frontend.billify.R;
import com.frontend.billify.models.UserTransactionShare;

import java.util.ArrayList;

public class PriceShareAdapter extends ArrayAdapter<UserTransactionShare> {
    private final Context context;
    private final ArrayList<UserTransactionShare> userPriceInfos;
    private final int layoutResourceId;

    public PriceShareAdapter(Context context, int layoutResourceId, ArrayList<UserTransactionShare> userPriceInfos) {
        super(context, layoutResourceId, userPriceInfos);
        this.context = context;
        this.userPriceInfos = userPriceInfos;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.price_share_username);
            holder.priceShare = (TextView) convertView.findViewById(R.id.price_share);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set values for the text views for price shares
        UserTransactionShare userPriceInfo = this.userPriceInfos.get(position);
        holder.userName.setText(userPriceInfo.getUserName() + ":");
        holder.priceShare.setText("$" + userPriceInfo.getPriceShare());

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class ViewHolder {
        TextView userName;
        TextView priceShare;
    }
}
