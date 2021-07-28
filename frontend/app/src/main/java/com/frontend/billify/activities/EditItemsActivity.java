package com.frontend.billify.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

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

        // NOTE: Swipe triggers delete
        @Override
        public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
            items.remove(viewHolder.getBindingAdapterPosition());
            editItemsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

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