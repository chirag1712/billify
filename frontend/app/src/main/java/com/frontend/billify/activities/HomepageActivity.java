package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.frontend.billify.R;

public class HomepageActivity extends AppCompatActivity {
    Button view_group_button;
    Button view_receipt_items_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button view_group_button = findViewById(R.id.view_all_group);
        Button add_receipt_button = findViewById(R.id.add_receipt_button);
        Button create_group_button = findViewById(R.id.create_group_button);

        view_group_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomepageActivity.this, groupPop.class));

            }
        });

        create_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, CreateGroupActivity.class));
            }
        });

        add_receipt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageActivity.this, UploadReceiptActivity.class);
                intent.putExtra("gid", "4");
                startActivity(intent);
            }
        });
    }
}