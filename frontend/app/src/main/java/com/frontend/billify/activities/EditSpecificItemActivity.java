package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.frontend.billify.R;

public class EditSpecificItemActivity extends AppCompatActivity {

    TextView itemNameView, itemPriceView;
    String itemName, itemPrice;
    int itemIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specific_item);
        itemNameView = findViewById(R.id.edit_specific_item_name);
        itemPriceView = findViewById(R.id.edit_specific_item_price);

        getData();
        setData();

        Button saveEditButton = findViewById(R.id.save_edit_specific_item_button);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(EditSpecificItemActivity.this, EditItemsActivity.class);
//                intent.
//                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_item_menu, menu);
        return true; // True to display add_item_menu XML
    }


    private void getData() {
        Intent intent = getIntent();
        if (intent.hasExtra("item_name") && intent.hasExtra("item_price")) {
            itemName = intent.getStringExtra("item_name");
            itemPrice = intent.getStringExtra("item_price");
            itemIndex = intent.getIntExtra("item_index", -1);

        } else {
            Toast.makeText(this, "Items not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        itemNameView.setText(itemName);
        itemPriceView.setText(itemPrice);
    }
}