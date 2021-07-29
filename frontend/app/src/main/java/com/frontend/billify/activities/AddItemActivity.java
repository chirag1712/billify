package com.frontend.billify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.frontend.billify.R;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText itemPriceEditText;

    public static final String ADDED_ITEM_NAME = "ADDED_ITEM_NAME";
    public static final String ADDED_ITEM_PRICE = "ADDED_ITEM_PRICE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemNameEditText = findViewById(R.id.add_item_name);
        itemPriceEditText = findViewById(R.id.add_item_price);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        setTitle("Add Item");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_item_menu, menu);
        return true; // True to display add_item_menu XML
    }
    
    private void saveNewItem() {
        String itemName = itemNameEditText.getText().toString();
        String itemPrice = itemPriceEditText.getText().toString();

        if (itemName.trim().isEmpty() || itemPrice.trim().isEmpty()) {
            Toast.makeText(this, "You need to insert an item name and price",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Intent data = new Intent();
        data.putExtra(ADDED_ITEM_NAME, itemName);
        data.putExtra(ADDED_ITEM_PRICE, itemPrice);
        setResult(RESULT_OK, data);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_new_item:
                saveNewItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}