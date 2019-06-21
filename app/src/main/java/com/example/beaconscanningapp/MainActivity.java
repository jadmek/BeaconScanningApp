package com.example.beaconscanningapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.beaconscanningapp.Utils.LogDisplayer;

public class MainActivity extends Activity {

    protected static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //if not granted ask for it.
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                // if granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogDisplayer.displayLogV(TAG, "location is granted");
                } else {
                    LogDisplayer.displayLogV(TAG, "location is NOT granted");
                }
                // do nothing since we already told it to perform in launch of app.
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
