package com.frontend.billify.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.frontend.billify.R;
import java.io.File;

import static com.frontend.billify.helpers.photo.PhotoDetails.getCameraPhotoOrientation;

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

        } else if (getIntent().hasExtra("receipt_img_file")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            File receiptImageFile = (File) getIntent().getExtras().get("receipt_img_file");
            ImageView receiptImageView = findViewById(R.id.group_receipt_image_view);
            Bitmap myBitmap = BitmapFactory.decodeFile(receiptImageFile.getAbsolutePath(), options);

            myBitmap = BitmapFactory.decodeFile(receiptImageFile.getAbsolutePath());
            receiptImageView.setImageBitmap(myBitmap);
            receiptImageView.setRotation(getCameraPhotoOrientation(receiptImageFile));
        }
    }

    private int[] getNewImageWidthHeight(int imageWidth, int imageHeight, int screen_width, int screen_height) {
        float i=1, j=1;

        float[] arr = {1f, 1.5f, 1.75f, 2f, 2.5f, 2.75f, 3, 3.5f};
        for (int k = 0; k < arr.length; ++k) {
            float elem = arr[k];
            i = elem;
            if ((imageWidth * elem) > (0.95 * screen_width)) {
                i = arr[k-1];
                break;
            }
        }

        for (int k = 0; k < arr.length; ++k) {
            float elem = arr[k];
            j = elem;
            if ((imageHeight * elem) > (0.95 * screen_height)) {
                j = arr[k-1];
                break;
            }
        }

        imageHeight = (int) Math.min(i, j) * imageHeight;
        imageWidth = (int) Math.min(i, j) * imageWidth;

        return new int[]{imageWidth, imageHeight};

    }

}