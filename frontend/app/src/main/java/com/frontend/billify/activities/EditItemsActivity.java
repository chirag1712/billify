package com.frontend.billify.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.frontend.billify.R;
import com.frontend.billify.adapters.EditItemsRecViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class EditItemsActivity extends AppCompatActivity {

    ArrayList<String> itemNames;
    ArrayList<Float> itemPrices;

    RecyclerView recyclerView;

    FloatingActionButton addNewItemButton;
    ActivityResultLauncher<Intent> addItemActivityResultLauncher;
    ActivityResultLauncher<Intent> editItemActivityResultLauncher;

    EditItemsRecViewAdapter editItemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);


        itemNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.items)));
        itemPrices = new ArrayList<>();
        ArrayList<String> strPrices = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.prices)));
        for (String price: strPrices) {
            itemPrices.add(Float.valueOf(price));
        }

        addNewItemButton = findViewById(R.id.add_new_item_button);

        addItemActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String newItemName = data.getStringExtra(AddEditItemActivity.ADDED_ITEM_NAME);
                            Float newItemPrice = data.getFloatExtra(AddEditItemActivity.ADDED_ITEM_PRICE, -1);
                            itemNames.add(newItemName);
                            itemPrices.add(newItemPrice);
                            editItemsAdapter.notifyDataSetChanged();

                        }
                    }
                }
        );

        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditItemsActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.ADD_MODE, 1);
                addItemActivityResultLauncher.launch(intent);
            }
        });

        recyclerView = findViewById(R.id.edit_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        editItemsAdapter = new EditItemsRecViewAdapter(
                this,
                itemNames,
                itemPrices
        );
        recyclerView.setAdapter(editItemsAdapter);
        new ItemTouchHelper(swipeDeleteCallback).attachToRecyclerView(recyclerView);


        editItemActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String newItemName = data.getStringExtra(AddEditItemActivity.EDITED_ITEM_NAME);
                            Float newItemPrice = data.getFloatExtra(AddEditItemActivity.EDITED_ITEM_PRICE, -1);
                            int editedItemIndex = data.getIntExtra(AddEditItemActivity.EDITED_ITEM_INDEX, -1);
                            if (editedItemIndex != -1) {
                                itemNames.set(editedItemIndex, newItemName);
                                itemPrices.set(editedItemIndex, newItemPrice);
                            } else {
                                Toast.makeText(EditItemsActivity.this,
                                        "Something wrong went wrong with editing",
                                        Toast.LENGTH_SHORT).show();
                            }
                            editItemsAdapter.notifyDataSetChanged();

                        }
                    }
                }
        );

        editItemsAdapter.setOnItemClickListener(new EditItemsRecViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String itemName, float itemPrice, int itemIndex) {
                Intent intent = new Intent(EditItemsActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.OLD_ITEM_NAME, itemName);
                intent.putExtra(AddEditItemActivity.OLD_ITEM_PRICE, String.valueOf(itemPrice));
                intent.putExtra(AddEditItemActivity.EDITED_ITEM_INDEX, itemIndex);
                // The number 2 below doesn't matter. Adding extra attribute to intent just to check
                // if the extra attribute is EDIT_MODE or ADD_MODE
                intent.putExtra(AddEditItemActivity.EDIT_MODE, 2);
                editItemActivityResultLauncher.launch(intent);

            }
        });


    }

    ItemTouchHelper.SimpleCallback swipeDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
                              @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                              @NonNull @NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        // NOTE: Swipe triggers delete
        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
            // remove the item and prices that you swiped on from the items Array List
            itemNames.remove(viewHolder.getBindingAdapterPosition());
            itemPrices.remove(viewHolder.getBindingAdapterPosition());
            editItemsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView,
                                @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            // Code adapted from https://github.com/kitek/android-rv-swipe-delete/blob/master/app/src/main/java/pl/kitek/rvswipetodelete/SwipeToDeleteCallback.kt
            ColorDrawable background = new ColorDrawable();

            Drawable deleteIcon = ContextCompat.getDrawable(
                    EditItemsActivity.this,
                    R.drawable.ic_baseline_delete_24
            );
            int inHeight = deleteIcon.getIntrinsicHeight();
            int inWidth = deleteIcon.getIntrinsicWidth();

            View itemView = viewHolder.itemView;
            float itemHeight = itemView.getBottom() - itemView.getTop();
            int backgroundColor = Color.parseColor("#FF0000");
            background.setColor(backgroundColor);
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);


            // Calculate position of delete icon
            int iconTop = (int) (itemView.getTop() + (itemHeight - inHeight) / 2);
            int iconMargin = (int) ((itemHeight - inHeight) / 2);
            int iconLeft = itemView.getRight() - iconMargin - inWidth;
            int iconRight = itemView.getRight() - iconMargin;
            int iconBottom = iconTop + inHeight;

            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}