package com.frontend.billify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Item;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadReceiptActivity extends AppCompatActivity {
    String currentPhotoPath;
    private File currPhotoFile;
    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int IMAGE_PICKER_CODE = 2;
    private static final String TAG = UploadReceiptActivity.class.getName();
    private final RetrofitService retrofitService = new RetrofitService();
    private final TransactionController transactionController = new TransactionController(retrofitService);
    private final Transaction transaction = new Transaction(
            0,
            0,
            null,
            null,
            null,
            null
    );

    private ProgressBar uploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt);

        final Button takePhotoButton = (Button) findViewById(R.id.take_photo);
        final Button showGalleryButton = (Button) findViewById(R.id.show_gallery);
        final ProgressBar uploadProgress = findViewById(R.id.uploadProgressBar);
        this.uploadProgress = uploadProgress;

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (CameraHasPermission()) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                ex.printStackTrace();
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = UploadReceiptActivity.this.getPhotoURI(photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                UploadReceiptActivity.this.startActivityForResult(
                                        takePictureIntent,
                                        CAMERA_PIC_REQUEST
                                );
                                UploadReceiptActivity.this.currPhotoFile = photoFile;
                            }
                        }

                    } else {
                        requestCameraPermission();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        showGalleryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent imagePickerIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    if (imagePickerIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(imagePickerIntent, IMAGE_PICKER_CODE);
                    }
//                    Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    imagePickerIntent.setType("image/*");
//
//                    startActivityForResult(Intent.createChooser(imagePickerIntent, "Select Picture"),
//                            IMAGE_PICKER_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Uri getPhotoURI(File photoFile) {
        return FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                photoFile);
    }

    private boolean CameraHasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void createTransaction(
            int gid,
            TransactionController transactionController
    ) {
        transactionController.createTransaction(
                gid,
                this.currPhotoFile
        ).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Error code onResponse " + response.code() + " " + response.errorBody().toString());
                    return;
                }
                Transaction currTransaction = response.body();
                uploadProgress.setVisibility(View.GONE);
                System.out.println("Successful request with return value: "
                        + currTransaction.getName()
                );
                currTransaction.printItems();
                System.out.println(currTransaction.getTid());
                Intent moveToItemizedScreenIntent = new Intent(
                        UploadReceiptActivity.this,
                        ItemizedViewActivity.class
                );
                Bundle transactionBundle = new Bundle();
                transactionBundle.putSerializable("SerializedTransaction", currTransaction);
                moveToItemizedScreenIntent.putExtra(
                        "TransactionBundle",
                        transactionBundle
                        );
                startActivity(moveToItemizedScreenIntent);

            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }
    private void requestCameraPermission() {
        System.out.println("Ask permission!");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PIC_REQUEST);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == CAMERA_PIC_REQUEST) && (resultCode == RESULT_OK)) {
                this.uploadProgress.setVisibility(View.VISIBLE);
                System.out.println("In upload image " + getIntent().getStringExtra("gid"));
                this.createTransaction(Integer.parseInt(getIntent().getStringExtra("gid")), transactionController);
            } else if ((requestCode == IMAGE_PICKER_CODE) && (resultCode == RESULT_OK)) {
                try {
                    // Creating file
                    this.currPhotoFile = null;
                    try {
                        this.currPhotoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.d(TAG, "Error occurred while creating the file");
                    }

                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream outputStream = new FileOutputStream(this.currPhotoFile);
                    // Copying
                    copyStream(inputStream, outputStream);
                    outputStream.close();
                    inputStream.close();
                    this.uploadProgress.setVisibility(View.VISIBLE);
                    System.out.println("In upload image " + getIntent().getStringExtra("gid"));
                    this.createTransaction(Integer.parseInt(getIntent().getStringExtra("gid")), transactionController);
                } catch (Exception e) {
                    Log.d(TAG, "onActivityResult: " + e.toString());
                }

            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void copyStream(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

}