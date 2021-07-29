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

public class AddEditItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText itemPriceEditText;
    public static final String ADDED_ITEM_NAME = "ADDED_ITEM_NAME";
    public static final String ADDED_ITEM_PRICE = "ADDED_ITEM_PRICE";

    public static final String EDITED_ITEM_NAME = "EDITED_ITEM_NAME";
    public static final String EDITED_ITEM_PRICE = "EDITED_ITEM_PRICE";
    public static final String EDITED_ITEM_INDEX = "INDEX_OF_EDITED_ITEM";
    public static final String OLD_ITEM_NAME = "OLD_ITEM_NAME";
    public static final String OLD_ITEM_PRICE = "OLD_ITEM_PRICE";

    public static final String EDIT_MODE = "Edit Item";
    public static final String ADD_MODE = "Add Item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemNameEditText = findViewById(R.id.add_item_name);
        itemPriceEditText = findViewById(R.id.add_item_price);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        Intent intent = getIntent();
        if (intent.hasExtra(EDIT_MODE)) {
            setTitle(EDIT_MODE);
            itemNameEditText.setText(intent.getStringExtra(OLD_ITEM_NAME));
            itemPriceEditText.setText(intent.getStringExtra(OLD_ITEM_PRICE));
        } else {
            setTitle(ADD_MODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_item_menu, menu);
        return true; // True to display add_item_menu XML
    }
    
    private void saveNewItem() {
        String itemName = itemNameEditText.getText().toString();
        String strItemPrice = itemPriceEditText.getText().toString();
        if (itemName.trim().isEmpty() || (strItemPrice.trim().isEmpty())) {
            Toast.makeText(this, "You need to insert an item name and price",
                    Toast.LENGTH_SHORT).show();

            return;
        }
        Float itemPrice = Float.valueOf(strItemPrice);

        Intent oldIntent = getIntent();
        Intent data = new Intent();

        String itemNamePropertyKey = "", itemPricePropertyKey = "";

        if (oldIntent.hasExtra(EDIT_MODE)) {
            itemNamePropertyKey = EDITED_ITEM_NAME;
            itemPricePropertyKey = EDITED_ITEM_PRICE;
            int editedItemIndex = oldIntent.getIntExtra(EDITED_ITEM_INDEX, -1);
            data.putExtra(EDITED_ITEM_INDEX, editedItemIndex);
        } else {
            itemNamePropertyKey = ADDED_ITEM_NAME;
            itemPricePropertyKey = ADDED_ITEM_PRICE;
        }

        data.putExtra(itemNamePropertyKey, itemName);
        data.putExtra(itemPricePropertyKey, itemPrice);
        setResult(RESULT_OK, data);

        finish();
    }

    private void goBackToPrevActivity() {
        Intent oldIntent = getIntent();
        if (oldIntent.hasExtra(EDIT_MODE)) {
            Toast.makeText(this, "Didn't edit the item",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Didn't add a new item",
                    Toast.LENGTH_SHORT).show();
        }
        Intent newIntent = new Intent();

        setResult(RESULT_CANCELED, newIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_new_item:
                saveNewItem();
                return true;
            default:
                goBackToPrevActivity();
                return true;
//                return super.onOptionsItemSelected(item);
        }
    }
}