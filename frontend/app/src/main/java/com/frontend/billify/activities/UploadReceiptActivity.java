package com.frontend.billify.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.frontend.billify.R;
import com.frontend.billify.activities.edit_and_confirm_items.EditItemsActivity;
import com.frontend.billify.adapters.LabelDropdownAdapter;
import com.frontend.billify.controllers.GroupService;
import com.frontend.billify.controllers.TransactionController;
import com.frontend.billify.models.Label;
import com.frontend.billify.models.Transaction;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private final GroupService groupService = new GroupService(retrofitService);

    private ProgressBar uploadProgress;
    private EditText transactionNameEditText;
    private TextView selectPhotoTextView;
    private AutoCompleteTextView labelTextView;
    private ArrayAdapter<String> labelArrayAdapter;

    private AutoCompleteTextView groupTextView;
    private ArrayAdapter<String> groupArrayAdapter;


    private Button uploadReceiptButton;
    private Button takePhotoButton;
    private Button showGalleryButton;

    ActivityResultLauncher<Intent> cameraResultLauncher;
    ActivityResultLauncher<Intent> galleryResultLauncher;

    private ImageView receiptImageView;

    private User currUser;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt);


        int uid = Persistence.getUserId(this);
        getUserGroups(uid);

        takePhotoButton = findViewById(R.id.take_photo);
        showGalleryButton = findViewById(R.id.show_gallery);
        uploadProgress = findViewById(R.id.uploadProgressBar);
        uploadReceiptButton = findViewById(R.id.upload_receipt_button);
        transactionNameEditText = findViewById(R.id.transaction_name_edit_text);
        labelTextView = findViewById(R.id.auto_complete_label_text_view);
        receiptImageView = findViewById(R.id.receipt_image_view);
        selectPhotoTextView = findViewById(R.id.select_photo_textview);

        // Add Label dropdown
        LabelDropdownAdapter labelDropdownAdapter = new LabelDropdownAdapter(
                this,
                Label.getUniqueLabels()
        );

        labelTextView.setAdapter(labelDropdownAdapter);
        // Set default Label in dropdown
        labelTextView.setText(labelDropdownAdapter.getItem(0).toString(), false);

        receiptImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadReceiptActivity.this, ViewReceiptImageActivity.class);
                intent.putExtra("receipt_img_file", currPhotoFile);
                startActivity(intent);
            }
        });
        // Set default Transaction Name
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm");
        Date date = new Date(System.currentTimeMillis());
        String defaultTransactionName = "Transaction " + formatter.format(date);
        transactionNameEditText.setText(defaultTransactionName);

        // Add on click listeners for buttons on this activity
        addCameraButtonClickListener();
        addGalleryButtonClickListener();
        addUploadButtonClickListener();

    }

    private void addCameraButtonClickListener() {
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

        // Add StartActivityForResult callbacks for Camera launcher intent
        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            try {
                                Toast.makeText(
                                        UploadReceiptActivity.this,
                                        "Chose a picture from the camera",
                                        Toast.LENGTH_SHORT).show();
                                selectPhotoTextView.setVisibility(View.GONE);
                                receiptImageView.setVisibility(View.VISIBLE);
                                uploadReceiptButton.setVisibility(View.VISIBLE);
                                Bitmap myBitmap = BitmapFactory.decodeFile(currPhotoFile.getAbsolutePath());
                                receiptImageView.setImageBitmap(myBitmap);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                        }
                    }
                }
        );

    }

    private void addGalleryButtonClickListener() {
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

        // Add StartActivityForResult callbacks for Gallery launcher intent
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
                                System.out.println("In upload image ");
                                Toast.makeText(
                                        UploadReceiptActivity.this,
                                        "Chose a picture from the gallery",
                                        Toast.LENGTH_SHORT).show();
                                selectPhotoTextView.setVisibility(View.GONE);
                                receiptImageView.setVisibility(View.VISIBLE);
                                uploadReceiptButton.setVisibility(View.VISIBLE);
                                Bitmap myBitmap = BitmapFactory.decodeFile(currPhotoFile.getAbsolutePath());
                                receiptImageView.setImageBitmap(myBitmap);
                            } catch (Exception e) {
                                Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                        }
                    }
                }
        );
    }

    private void addUploadButtonClickListener() {

        uploadReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transactionName = transactionNameEditText.getText().toString().trim();
                if (transactionName.equals("")) {
                    Toast.makeText(
                            UploadReceiptActivity.this,
                            "Add a Transaction Name",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (UploadReceiptActivity.this.currPhotoFile == null) {
                    Toast.makeText(
                            UploadReceiptActivity.this,
                            "Pick a photo first",
                            Toast.LENGTH_SHORT).show();
                    return;

                }

                uploadAndParseReceipt();
            }
        });
    }


    // Below are camera and gallery related helper methods
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


    private void getUserGroups(int uid) {
        // A method that makes a GET request to get user's groups
        groupService.getGroups(uid).enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                        Toast.makeText(
                                UploadReceiptActivity.this,
                                "Couldn't get user Groups successful response from API",
                                Toast.LENGTH_SHORT
                        ).show();
                    return;
                }
                currUser = response.body(); // only userId is returned
                updateGroupDropdownOfCurrUser(); // updates UI Group dropdown of current User
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Toast.makeText(
                        UploadReceiptActivity.this,
                        "Failed to make API request for getting Groups",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateGroupDropdownOfCurrUser() {
        /*
        This method is called when the Activity receives the current user's groups from the backend
        API request.
         */
        ArrayList<String> groupNames = currUser.getGroupNames();
        currUser.initGroupNameToGidMap();
        currUser.initGidToGroupPositionMap();
        groupTextView = findViewById(R.id.auto_complete_groups_text_view);
        groupArrayAdapter = new ArrayAdapter<>(
                UploadReceiptActivity.this,
                R.layout.list_group,
                groupNames
        );
        groupTextView.setAdapter(groupArrayAdapter);
        String defaultGroupName = "";
        if (getIntent().hasExtra("gid")) {
            int gid = getIntent().getIntExtra("gid", -1);
            defaultGroupName = groupArrayAdapter.getItem(currUser.getGroupPositionFromGid(gid)).toString();
        } else {
            defaultGroupName = groupArrayAdapter.getItem(0).toString();
        }
        groupTextView.setText(defaultGroupName, false);
    }

    public void uploadAndParseReceipt() {
        /*
        Creates a new Group Transaction by making a call to the API, can specify a callback.
         */
        uploadProgress.setVisibility(View.VISIBLE);
        uploadReceiptButton.setVisibility(View.GONE);
        transactionController.parseReceipt(this.currPhotoFile).enqueue(
                new Callback<Transaction>() {
                    @Override
                    public void onResponse(@NotNull Call<Transaction> call, @NotNull Response<Transaction> response) {
                        Transaction parsedItemsTransaction = new Transaction();
                        uploadProgress.setVisibility(View.GONE);
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    UploadReceiptActivity.this,
                                    "Couldn't get parsed receipt successfully from API",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            if (response.body() == null) {
                                Toast.makeText(
                                        UploadReceiptActivity.this,
                                        "Parse Receipt Response body is null",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            parsedItemsTransaction = response.body();
                        }
                        /* NOTE: createNewTransactionFromParsedItems creates a new Transaction
                        that fills parsedItemsTransaction with gid, label_id and other information.
                         */
                        parsedItemsTransaction = createNewTransactionFromParsedItems(
                                parsedItemsTransaction
                        );
                        moveToEditItemsActivity(parsedItemsTransaction);
                    }

                    @Override
                    public void onFailure(@NotNull Call<Transaction> call, @NotNull Throwable t) {
                        uploadProgress.setVisibility(View.GONE);
                        Toast.makeText(
                                UploadReceiptActivity.this,
                                "Failed Parsing Receipt since API request failed",
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
    }

    // capture image orientation

    private int getCameraPhotoOrientation(Context context, Uri imageUri,
                                         String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    private Transaction createNewTransactionFromParsedItems(Transaction parsedItemsTransaction) {
        /*
        parsedItemsTransaction is the response from the API that only contains parsed items.
        We create a new transaction from it and populate it with extra fields such as
        gid, transaction name and label id.
         */

        /* We call copy constructor since parsedItemsTransaction argument is constructed
         using gson library when it is assigned to response.body(). The GSON library
         skips the developer-defined constructors and builds the parsedItemsTransaction object
         via reflection:
         https://stackoverflow.com/questions/40441460/does-googles-gson-use-constructors.
         */
        Transaction newTransaction = new Transaction(parsedItemsTransaction);
        newTransaction.setGid(currUser.getGidFromGroupName(groupTextView.getText().toString()));
        String transactionName = transactionNameEditText.getText().toString().trim();
        newTransaction.setTransaction_name(transactionName);
        newTransaction.setCurrPhotoFile(UploadReceiptActivity.this.currPhotoFile);
        newTransaction.setLabel_id(labelTextView.getText().toString());
        return newTransaction;
    }

    private void moveToEditItemsActivity(Transaction newTransactionWithParsedItems) {
        /*
        This method is called after getting parsed receipt items and it moves the newly created
        transaction from the parsed items onto the EditItemsActivity where the user can edit
        the items.
         */
        Intent moveToEditAndConfirmItemsActivityIntent = new Intent(
                UploadReceiptActivity.this,
                EditItemsActivity.class
        );
        Bundle transactionBundle = new Bundle();
        transactionBundle.putSerializable("SerializedTransaction", newTransactionWithParsedItems);
        moveToEditAndConfirmItemsActivityIntent.putExtra(
                "TransactionBundle",
                transactionBundle
        );
        uploadReceiptButton.setVisibility(View.VISIBLE);
        startActivity(moveToEditAndConfirmItemsActivityIntent);
    }
}