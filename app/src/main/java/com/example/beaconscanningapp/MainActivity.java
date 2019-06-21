package com.example.beaconscanningapp;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    protected static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
