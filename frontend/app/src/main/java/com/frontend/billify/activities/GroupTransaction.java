package com.frontend.billify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.frontend.billify.R;

public class GroupTransaction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_transaction);

        final Button add_group_receipt = findViewById(R.id.add_group_receipt);
        add_group_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupTransaction.this, UploadReceiptActivity.class);
                intent.putExtra("gid", getIntent().getStringExtra("gid"));
                startActivity(intent);
            }
        });
    }
}