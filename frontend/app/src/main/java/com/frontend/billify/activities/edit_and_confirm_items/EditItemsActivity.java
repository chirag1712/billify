package com.frontend.billify.activities.edit_and_confirm_items;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.frontend.billify.R;
import com.frontend.billify.activities.HomepageActivity;
import com.frontend.billify.adapters.EditItemsRecViewAdapter;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.services.RetrofitService;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditItemsActivity extends AppCompatActivity {

    Transaction currTransaction;
    RecyclerView recyclerView;

    FloatingActionButton addNewItemButton;
    ActivityResultLauncher<Intent> addItemActivityResultLauncher;
    ActivityResultLauncher<Intent> editItemActivityResultLauncher;

    EditItemsRecViewAdapter editItemsAdapter;
    Button confirmButton;
    ProgressBar createTransactionRequestProgressBar;
    Dialog successfulCreationTransactionPopup;
    MaterialTextView transactionNameTextView;
    MaterialCardView transactionNameCardView;

    private final RetrofitService retrofitService = new RetrofitService();
    private final TransactionController transactionController = new TransactionController(retrofitService);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_items);

        createTransactionRequestProgressBar = findViewById(R.id.create_transaction_request_progress_bar);
        confirmButton = findViewById(R.id.confirm_items_button);
        addNewItemButton = findViewById(R.id.add_new_item_button);
        transactionNameCardView = findViewById(R.id.transaction_name_edit_screen_cardview);
        successfulCreationTransactionPopup = new Dialog(this);

        // adding OnClick event listeners to buttons on this activity
        addConfirmButtonClickListener();
        addAddNewItemButtonClickListener();

        Intent i = getIntent();
        if (i.hasExtra("TransactionBundle")) {
            Bundle b = i.getBundleExtra("TransactionBundle");
            currTransaction = (Transaction) b.getSerializable("SerializedTransaction");
        }

        if (currTransaction.getNumItems() == 0) {
            Toast.makeText(
                    this,
                    "Couldn't parse any items from receipt. You can add items manually.",
                    Toast.LENGTH_SHORT).show();
        }

        transactionNameTextView = findViewById(R.id.transaction_name_edit_screen_textview);
        transactionNameTextView.setText(currTransaction.getName());
        addNewItemButton = findViewById(R.id.add_new_item_button);

        setUpEditItemsRecyclerView();

    }

    private void setUpEditItemsRecyclerView() {
        // Setting up recycler view and the EditItemsRecViewAdapter for items
        recyclerView = findViewById(R.id.edit_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        editItemsAdapter = new EditItemsRecViewAdapter(
                this,
                currTransaction
        );
        recyclerView.setAdapter(editItemsAdapter);
        // Adding Swipe left to Delete Item functionality
        new ItemTouchHelper(swipeDeleteCallback).attachToRecyclerView(recyclerView);

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
                                Toast.makeText(
                                        EditItemsActivity.this,
                                        "Something wrong went wrong with editing",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                }
        );

    }

    private void addAddNewItemButtonClickListener() {
        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditItemsActivity.this, AddEditItemActivity.class);
                intent.putExtra(AddEditItemActivity.ADD_MODE, 1);
                addItemActivityResultLauncher.launch(intent);
            }
        });

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

    }

    private void addConfirmButtonClickListener() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        (currTransaction.getCurrPhotoFile() != null) &&
                                (currTransaction.getNumItems() > 0)
                ) {
                    currTransaction.printItems();
                    createTransactionRequestProgressBar.setVisibility(View.VISIBLE);
                    confirmButton.setVisibility(View.INVISIBLE);
                    createTransaction();

                } else if (currTransaction.getCurrPhotoFile() == null) {
                    Toast.makeText(
                            EditItemsActivity.this,
                            "Can't Confirm since there's no Receipt selected to upload",
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (currTransaction.getNumItems() == 0) {
                    Toast.makeText(
                            EditItemsActivity.this,
                            "Can't Confirm since there are no items in this Transaction",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            EditItemsActivity.this,
                            "Was not able to create Transaction since number of items are either" +
                                    "0 or there is no receipt image",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    public void createTransaction() {
        // Method that makes POST request to create a Transaction
        transactionController.createTransaction(
                currTransaction.getTransactionJSONString(),
                currTransaction.getCurrPhotoFile()).enqueue(
                new Callback<Transaction>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        createTransactionRequestProgressBar.setVisibility(View.GONE);
                        confirmButton.setVisibility(View.VISIBLE);
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    EditItemsActivity.this,
                                    "Couldn't Parse Receipt",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                        Transaction currTransaction = response.body();
                        showSuccessAnimation();
                        moveBackToHomepageActivity(1000);
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        createTransactionRequestProgressBar.setVisibility(View.GONE);
                        confirmButton.setVisibility(View.VISIBLE);
                        Toast.makeText(
                                EditItemsActivity.this,
                                "Failed creating transaction since API request failed",
                                Toast.LENGTH_SHORT
                        ).show();
                        t.printStackTrace();
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSuccessAnimation() {
        /*
        This method sets all elements on EditItemsActivity to GONE visibility
        and animates a Success Tick animation. This method is intended to be called
        when the API response for creating a transaction is successful
         */
        ImageView SuccessImageView = findViewById(R.id.create_transaction_success_animation);
        ImageView greenBackground = findViewById(R.id.green_background);
        recyclerView.setVisibility(View.GONE);
        transactionNameTextView.setVisibility(View.GONE);
        transactionNameTextView.setVisibility(View.GONE);
        transactionNameCardView.setVisibility(View.GONE);
        greenBackground.setVisibility(View.VISIBLE);
        SuccessImageView.setVisibility(View.VISIBLE);
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;

        Drawable drawable = SuccessImageView.getDrawable();
        if (drawable instanceof AnimatedVectorDrawableCompat) {
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        confirmButton.setVisibility(View.GONE);
        addNewItemButton.setVisibility(View.GONE);
    }

    private void moveBackToHomepageActivity(int delay) {
        /*
        This method is called to move back to the Homepage Activity after
        delay ms of delay.
         */
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent moveBackToHomepageIntent = new Intent(
                        EditItemsActivity.this,
                        HomepageActivity.class
                );
                startActivity(moveBackToHomepageIntent);
            }
        }, delay);
    }


    // Adding swipe left to delete functionality
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