package com.frontend.billify.activities;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class UserPriceShare {
    // TODO: should ideally be a recycler view with fixed number of elements
    private RecyclerView userPriceShares;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserPriceShare(RecyclerView userPriceShares, Context context) {
        this.userPriceShares = userPriceShares;

        populateView();
    }

    private void populateView() {
//        int uid = Persistence.getUserId(this.context);
//        String userName = Persistence.getUserName(this.context);
    }
}
