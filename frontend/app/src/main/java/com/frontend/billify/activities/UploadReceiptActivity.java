package com.frontend.billify.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.frontend.billify.R;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.services.RetrofitService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private ProgressBar uploadProgress;

    ActivityResultLauncher<Intent> cameraResultLauncher;
    ActivityResultLauncher<Intent> galleryResultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt);

        final Button takePhotoButton = findViewById(R.id.take_photo);
        final Button showGalleryButton = findViewById(R.id.show_gallery);
        final ProgressBar uploadProgress = findViewById(R.id.uploadProgressBar);
        final Button editItemsButton = findViewById(R.id.edit_items_button);

        this.uploadProgress = uploadProgress;

        editItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadReceiptActivity.this, EditItemsActivity.class);
                startActivity(intent);
            }
        });


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
                                cameraResultLauncher.launch(takePictureIntent);
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
                        galleryResultLauncher.launch(imagePickerIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                uploadProgress.setVisibility(View.VISIBLE);
                                createTransaction(Integer.parseInt(getIntent().getStringExtra("gid")), transactionController);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                        }
                    }
                }
        );

        galleryResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                // Creating file
                                currPhotoFile = null;
                                try {
                                    currPhotoFile = createImageFile();
                                } catch (IOException ex) {
                                    Log.d(TAG, "Error occurred while creating the file");
                                }
                                Intent data = result.getData();
                                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                                FileOutputStream outputStream = new FileOutputStream(currPhotoFile);
                                // Copying
                                copyStream(inputStream, outputStream);
                                outputStream.close();
                                inputStream.close();
                                uploadProgress.setVisibility(View.VISIBLE);
                                System.out.println("In upload image ");
                                createTransaction(Integer.parseInt(getIntent().getStringExtra("gid")), transactionController);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                        }
                    }
                }
        );

    }

    private Uri getPhotoURI(File photoFile) {
        /*
        Gets PhotoURI for a given File
         */
        return FileProvider.getUriForFile(this,
                "com.example.android.fileprovider",
                photoFile);
    }

    private boolean CameraHasPermission() {
        /*
        Returns True if Camera permission is granted and false otherwise
         */
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
        /*
        Creates a new Group Transaction by making a call to the API, can specify a callback.
         */
        transactionController.createTransaction(
                gid,
                this.currPhotoFile
        ).enqueue(new Callback<Transaction>() {

            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                uploadProgress.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    try {
                        Toast parseReceiptErrorToast = Toast.makeText(
                                UploadReceiptActivity.this,
                                "Couldn't Parse Receipt",
                                Toast.LENGTH_SHORT
                        );
                        parseReceiptErrorToast.show();
                        System.out.println("Error code onResponse "
                                + response.code()
                                + " "
                                + response.errorBody().string());
                    } catch (Exception e) {
                        System.out.println(
                                "Exception occurred during response callback from receipt parser API: "
                                        + e);
                    }
                    return;
                }
                Transaction currTransaction = new Transaction(response.body());
                currTransaction.setCurrPhotoFile(UploadReceiptActivity.this.currPhotoFile);
                System.out.println("Successful upload request with return value: "
                        + currTransaction.getName()
                );
                Intent moveToEditAndConfirmItemsActivityIntent = new Intent(
                        UploadReceiptActivity.this,
                        EditItemsActivity.class
                );
                Bundle transactionBundle = new Bundle();
                transactionBundle.putSerializable("SerializedTransaction", currTransaction);
                moveToEditAndConfirmItemsActivityIntent.putExtra(
                        "TransactionBundle",
                        transactionBundle
                        );
                startActivity(moveToEditAndConfirmItemsActivityIntent);

            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void requestCameraPermission() {
        /*
        Request camera for permission
         */
        System.out.println("Ask permission!");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PIC_REQUEST);
    }

    private File createImageFile() throws IOException {
        /*
        Create a Temporary file to store an image in
         */
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




    private void copyStream(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        /*
        A method to copy data from an input stream to a file output stream
         */

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

}