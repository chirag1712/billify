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
        Bundle b = i.getBundleExtra("TransactionBundle");
        Transaction currTransaction = (Transaction) b.getSerializable("SerializedTransaction");

        ArrayList<Item> items = currTransaction.getItems();
        ReceiptsItemsRecViewAdapter adapter = new ReceiptsItemsRecViewAdapter(this);
        adapter.setItems(items);
        itemsRecView.setAdapter(adapter);
        itemsRecView.setLayoutManager(new LinearLayoutManager(this));
    }
}
