package com.example.yourmemories;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yourmemories.DataBase.DAOs.MemoryDao;
import com.example.yourmemories.DataBase.Model.Memory;
import com.example.yourmemories.DataBase.MyDataBase;
import com.example.yourmemories.Location.MyLocationProvider;
import com.example.yourmemories.base.BaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final int GPS_PERMISSIONS_REQUEST_CODE = 123;
    protected Button newMemory;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (isGPSPermissionGranted()) {
            getUserLocation();
        } else {
            askForGPSPermission();
        }
        initView();
    }

    private void initView() {
        newMemory = (Button) findViewById(R.id.new_memory);
        newMemory.setOnClickListener(MainActivity.this);
    }


    public boolean isGPSPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    public void askForGPSPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            showMessage(R.string.gps_explaination, R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            GPS_PERMISSIONS_REQUEST_CODE);

                }
            });
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_PERMISSIONS_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSIONS_REQUEST_CODE: {
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

    Location myLocation;
    MyLocationProvider locationProvider;

    public void getUserLocation() {
        if (locationProvider == null) {
            locationProvider = new MyLocationProvider(activity);
        }
        locationProvider.getUserLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                Log.e("location", myLocation + " ");
                drawUserMarkerOnMap();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        drawUserMarkerOnMap();
    }

    Marker userMarker = null;

    public void drawUserMarkerOnMap() {
        Log.e("mmap", mMap + " ");
        Log.e("usermarker", userMarker + " ");
        Log.e("mylocation", myLocation + " ");
        if (myLocation == null) {
            return;
        }
        if (mMap == null) return;

//        if (userMarker == null) {
            memories = getMemories();
            Log.e("memories", memories + "");
            for (Memory memory : memories) {
                Log.e("memory",memory +"" );
                userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(memory.getLat(), memory.getLong()))
                        .title(memory.getTitle())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.memory)));

            }
//        }
//        else {
//            userMarker.setPosition(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
//        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12f));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.new_memory) {
            Intent intent = new Intent(MainActivity.this, AddMemoryActivity.class);
            intent.putExtra("latitude ", myLocation.getLatitude());
            intent.putExtra("longitude", myLocation.getLongitude());
            startActivity(intent);
        }
    }

    List<Memory> memories;

    public List<Memory> getMemories() {
        memories = MyDataBase.getInstance(this).memoryDao().getMmories();
        return memories;

    }

    public Bitmap loadBitmap(String url) {
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("image",bm+"" );
        return bm;
    }
}
