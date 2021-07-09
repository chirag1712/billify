package com.frontend.billify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.models.Item;
import com.frontend.billify.models.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemizedViewActivity extends Activity {
    private RecyclerView itemsRecView;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);

        itemsRecView = findViewById(R.id.recipeItems);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("B");
        Transaction currTransaction = (Transaction) b.getSerializable("S");

//        ArrayList<Item> items = new ArrayList<>();
//        items.add(new Item("Baked Apple Pie [270.0 Cals]",  1.29f));
//        items.add(new Item("McDouble [380.0 Cals]", 3.09f));
//        items.add(new Item("Junior Chicken [380.0 Cals]", 2.69f));
//        items.add(new Item("Spicier Szechuan McChicken [490.0 Cals", 6.09f));
//        items.add(new Item("World Famous Fries [240.0 Cals]", 3.29f));
//        items.add(new Item("Strawberry Banana Smoothie [130.0 Cals] Select Size Medium 250.0 Cals] CA$1.60", 4.09f));
//        items.add(new Item("Double Big Mac [730.0 Cals]", 8.29f));
//        items.add(new Item("Chocolate Triple Thick Milkshake [500.0 Cals]", 4.09f));
        ArrayList<Item> items = currTransaction.getItems();
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(this);
        adapter.setItems(items);
        itemsRecView.setAdapter(adapter);
        itemsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}
