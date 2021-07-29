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
import com.frontend.billify.models.Item;

public class AddEditItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText itemPriceEditText;
    public static final String ADDED_ITEM = "ADDED_ITEM";
    public static final String EDITED_ITEM_INDEX = "INDEX_OF_EDITED_ITEM";
    public static final String EDITED_ITEM = "EDITED_ITEM";
    public static final String OLD_ITEM = "OLD_ITEM";
    public static final String EDIT_MODE = "Edit Item";
    public static final String ADD_MODE = "Add Item";
    Item itemToBeEdited;
    Item itemToBeAdded;

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
            itemToBeEdited = (Item) intent.getSerializableExtra(OLD_ITEM);

            itemNameEditText.setText(itemToBeEdited.getName());
            itemPriceEditText.setText(itemToBeEdited.getStrPrice());
        } else {
            setTitle(ADD_MODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit_item_menu, menu);
        return true; // True to display add_edit_item_menu XML
    }
    
    private void saveItem() {
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

        if (oldIntent.hasExtra(EDIT_MODE)) {
            int editedItemIndex = oldIntent.getIntExtra(EDITED_ITEM_INDEX, -1);

            itemToBeEdited.setName(itemName);
            itemToBeEdited.setPrice(itemPrice);
            data.putExtra(EDITED_ITEM, itemToBeEdited);
            data.putExtra(EDITED_ITEM_INDEX, editedItemIndex);

        } else {
            itemToBeAdded = new Item(itemName, Float.valueOf(strItemPrice));
            data.putExtra(ADDED_ITEM, itemToBeAdded);
        }

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
            case R.id.save_item:
                saveItem();
                return true;
            default:
                goBackToPrevActivity();
                return true;
        }
    }
}