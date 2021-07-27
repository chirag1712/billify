package com.frontend.billify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.frontend.billify.R;
import com.frontend.billify.adapters.EditItemsRecViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class EditItemsActivity extends AppCompatActivity {

    ArrayList<String> items;
    ArrayList<String> prices;

    RecyclerView recyclerView;

    EditItemsRecViewAdapter editItemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);
        items = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.items)));
        prices = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.prices)));
        recyclerView = findViewById(R.id.edit_items_recycler_view);
        editItemsAdapter = new EditItemsRecViewAdapter(this, items, prices);
        recyclerView.setAdapter(editItemsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(swipeDeleteCallback).attachToRecyclerView(recyclerView);
        System.out.println("Back here again!");
    }

    ItemTouchHelper.SimpleCallback swipeDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
                              @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                              @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
            items.remove(viewHolder.getBindingAdapterPosition());
            editItemsAdapter.notifyDataSetChanged();
        }
    };
}