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
import android.widget.Button;
import android.widget.Toast;

import com.frontend.billify.R;
import com.frontend.billify.adapters.EditItemsRecViewAdapter;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.max;

public class EditItemsActivity extends AppCompatActivity {

//    ArrayList<String> itemNames;
//    ArrayList<Float> itemPrices;

    Transaction currTransaction;
    RecyclerView recyclerView;

    FloatingActionButton addNewItemButton;
    ActivityResultLauncher<Intent> addItemActivityResultLauncher;
    ActivityResultLauncher<Intent> editItemActivityResultLauncher;

    EditItemsRecViewAdapter editItemsAdapter;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);

        confirmButton = findViewById(R.id.confirm_items_button);

//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        ArrayList<String> itemNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.items)));
        ArrayList<String> strPrices = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.prices)));
        currTransaction = new Transaction(1, 4, "2004-01-01", "NOT_STARTED",
                "None", "empty_url");

        for (int i = 0; i < itemNames.size(); ++i) {
            currTransaction.addItem(new Item(itemNames.get(i), Float.valueOf(strPrices.get(i))));
        }

        Intent i = getIntent();
        if (i.hasExtra("TransactionBundle")) {
            Bundle b = i.getBundleExtra("TransactionBundle");
            currTransaction = (Transaction) b.getSerializable("SerializedTransaction");
        }



        addNewItemButton = findViewById(R.id.add_new_item_button);

        addItemActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Item newItem = (Item) data.getSerializableExtra(AddEditItemActivity.ADDED_ITEM);
                            int insertIndex = 0;
                            currTransaction.addItem(insertIndex, newItem);
                            editItemsAdapter.notifyItemInserted(insertIndex);
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
//        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        editItemsAdapter = new EditItemsRecViewAdapter(
                this,
                currTransaction
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
                            Item editedItem = (Item) data.getSerializableExtra(AddEditItemActivity.EDITED_ITEM);
                            int editedItemIndex = data.getIntExtra(AddEditItemActivity.EDITED_ITEM_INDEX, -1);
                            if (editedItemIndex != -1) {
                                currTransaction.getItems().set(editedItemIndex, editedItem);
                                editItemsAdapter.notifyItemChanged(editedItemIndex);
                            } else {
                                Toast.makeText(EditItemsActivity.this,
                                        "Something wrong went wrong with editing",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        editItemsAdapter.setOnItemClickListener(new EditItemsRecViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item, int itemIndex) {
                Intent intent = new Intent(EditItemsActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.OLD_ITEM, item);
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
            int position = viewHolder.getBindingAdapterPosition();
            currTransaction.getItems().remove(position);
            editItemsAdapter.notifyItemRemoved(position);
            editItemsAdapter.notifyItemRangeChanged(position, currTransaction.getNumItems());

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