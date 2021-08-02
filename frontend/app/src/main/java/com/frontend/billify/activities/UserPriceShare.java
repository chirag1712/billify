package com.frontend.billify.activities;

import android.os.Build;
import android.widget.GridLayout;

import androidx.annotation.RequiresApi;

import com.frontend.billify.models.Item;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPriceShare {
    // TODO: should ideally be a recycler view with fixed number of elements
    private GridLayout userSharesGrid;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserPriceShare(GridLayout userSharesGrid, ArrayList<Item> items) {
        this.userSharesGrid = userSharesGrid;

        populateGrid(items);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void populateGrid(ArrayList<Item> items) {
        HashMap<String, Float> priceShares = new HashMap<>();
        items.forEach(item -> {
            HashMap<Integer, String> selectedUserData = item.getSelectedUserData();
            int n = selectedUserData.size();
            selectedUserData.forEach((uid, userName) -> {
                if (priceShares.containsKey(userName)) {
                    priceShares.put(userName, priceShares.get(userName) + (item.getPrice() / n));
                } else {
                    priceShares.put(userName, item.getPrice() / n);
                }
            });
        });

        // actually populate grid here
        // TODO: actually fetch all members of the group to show zero balance users as well maybe
        /*userSharesGrid.removeAllViews();
        int total = priceShares.size() * 2;
        int columns = 2; // one for name, one for priceShare
        int rows = priceShares.size();
        userSharesGrid.setColumnCount(columns);
        userSharesGrid.setRowCount(rows + 1);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; r < columns; c++) {

            }
        }
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
            userSharesGrid.addView(userText, gridParam);
        }*/
    }

}
