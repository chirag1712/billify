package com.frontend.billify;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemizedViewActivity extends Activity {
    private RecyclerView itemsRecView;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

        itemsRecView = findViewById(R.id.recipeItems);

        ArrayList<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem("Baked Apple Pie [270.0 Cals]", 1.29));
        items.add(new ReceiptItem("McDouble [380.0 Cals]", 3.09));
        items.add(new ReceiptItem("Junior Chicken [380.0 Cals]", 2.69));
        items.add(new ReceiptItem("Spicier Szechuan McChicken [490.0 Cals", 6.09));
        items.add(new ReceiptItem("World Famous Fries [240.0 Cals]", 3.29));
        items.add(new ReceiptItem("Strawberry Banana Smoothie [130.0 Cals] Select Size Medium 250.0 Cals] CA$1.60", 4.09));
        items.add(new ReceiptItem("Double Big Mac [730.0 Cals]", 8.29));
        items.add(new ReceiptItem("Chocolate Triple Thick Milkshake [500.0 Cals]", 4.09));
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter();
        adapter.setItems(items);
        itemsRecView.setAdapter(adapter);
        itemsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}
