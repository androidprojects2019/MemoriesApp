package com.example.yourmemories;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yourmemories.DataBase.Model.Memory;
import com.example.yourmemories.DataBase.MyDataBase;
import com.example.yourmemories.base.BaseActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddMemoryActivity extends BaseActivity implements View.OnClickListener {
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 123;
    protected ImageView image;
    protected String imageString;
    protected EditText description;
    protected EditText title;
    protected Button addMemory;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_memory);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        initView();
        if (isCameraPermisionGranted()) {
            takePhoto();
        } else {
            askForCameraPermission();

        }
    }

    Uri imageUri;

    private void takePhoto() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                //use imageUri here to access the image

                // Bundle extras = data.getData();

                Log.e("URI", imageUri.toString());
                imageString = imageUri.toString();
                image.setImageURI(imageUri);
                //Bitmap bmp = (Bitmap) extras.get("data");

                // here you will get the image as bitmap


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
            }
        }


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_memory) {
            if (title.getText().toString().trim().isEmpty() || description.getText().toString().trim().isEmpty()) {
                showMessage("Please fill all fields", "ok");
            } else {
                showMessage("in else ", "ok");

                String titleText = title.getText().toString();
                String decriptionText = description.getText().toString();

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                Memory memory = new Memory(titleText, decriptionText, formattedDate, imageString, latitude, longitude);
                MyDataBase
                        .getInstance(activity)
                        .memoryDao()
                        .addMemory(memory);
                showMessage(R.string.memory_added_successfully, R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                },false);
            }
        }
    }

    private void initView() {
        image = (ImageView) findViewById(R.id.image);
        description = (EditText) findViewById(R.id.description);
        title = (EditText) findViewById(R.id.title);
        addMemory = (Button) findViewById(R.id.add_memory);
        addMemory.setOnClickListener(AddMemoryActivity.this);
    }

    public boolean isCameraPermisionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    public void askForCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showMessage(R.string.camera_explanasion, R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSIONS_REQUEST_CODE);

                }
            });
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSIONS_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(activity, "Sorry we can't access your location", Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
