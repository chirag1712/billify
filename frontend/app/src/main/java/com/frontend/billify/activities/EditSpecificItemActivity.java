package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.frontend.billify.R;

public class EditSpecificItemActivity extends AppCompatActivity {

    TextView itemNameView, itemPriceView;
    String itemName, itemPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specific_item);
        itemNameView = findViewById(R.id.edit_specific_item_name);
        itemPriceView = findViewById(R.id.edit_specific_item_price);

        getData();
        setData();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("item_name") && intent.hasExtra("item_price")) {
            itemName = intent.getStringExtra("item_name");
            itemPrice = intent.getStringExtra("item_price");
        } else {
            Toast.makeText(this, "Items not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        itemNameView.setText(itemName);
        itemPriceView.setText(itemPrice);
    }
}