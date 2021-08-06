package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.frontend.billify.R;

public class ViewReceiptImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt_image);

        //setting the size of the layout to be some percentage of the phones screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screen_width = dm.widthPixels;
        int screen_height = dm.heightPixels;
        getWindow().setLayout((int)(screen_width*0.8),(int) (screen_height*0.8));
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (getIntent().hasExtra("receipt_img")) {
            ImageView receiptImageView = findViewById(R.id.group_receipt_image_view);
            Glide.with(this).load(getIntent().getStringExtra("receipt_img")).into(receiptImageView);
        }
    }
}