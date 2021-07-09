package com.frontend.billify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.frontend.billify.persistence.Persistence;

public class HomepageActivity extends AppCompatActivity {
    Button view_group_button;
    Button view_receipt_items_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button view_group_button = (Button) findViewById(R.id.view_all_group);
        Button add_receipt_button = (Button) findViewById(R.id.add_receipt_button);
        Button create_group_button = (Button) findViewById(R.id.create_group_button);

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