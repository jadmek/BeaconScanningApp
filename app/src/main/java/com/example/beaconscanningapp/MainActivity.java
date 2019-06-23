package com.example.beaconscanningapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements BeaconConsumer, RangeNotifier {
//    // log
//    protected static final String TAG = MainActivity.class.getSimpleName();
    // permissions
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 111;
    // beacon
    private BeaconManager beaconManager;
    // TODO fix issue in this filter
    private static final String BEACON_FILTER = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";
    private Region beaconRegion = new Region("ray_region", null, null, null);
    int counter = 0;
    // views
    private TextView tvInfo, tvBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestPermissionsIfNeeded();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
    }

    private void initViews() {
        tvInfo = findViewById(R.id.tvInfo);
        tvBeacons = findViewById(R.id.tvBeacons);
    }

    // BEACON //

    private void initBeaconDetection() {
        displayInfo("Setting Up Beacon Detection...");
        // init
        beaconManager = BeaconManager.getInstanceForApplication(this);
//        // give the beacon manager our beacon layout, to filter our scans for these types of beacons only
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        if (beaconManager != null) {
            // display info
            displayInfo("Beacon Service Connected");
            // setup monitoring
//            beaconManager.removeAllRangeNotifiers();
            beaconManager.addRangeNotifier(this);
            try {
                beaconManager.startRangingBeaconsInRegion(beaconRegion);
            } catch (RemoteException e) {
                // do nothing
            }
        } else {
            displayInfo("Unknown Error");
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        if (collection != null) {
            // display size
            displayInfo("Beacons In Region (" + collection.size() + ") [beat #" + counter++ +"]");
            // generate beacons
            List<Beacon> listBeacons = new ArrayList<>(collection.size());
            listBeacons.addAll(collection);
            // display info
            displayBeacons(listBeacons);
        } else {
            displayInfo("Beacons In Region: Null");
        }
    }

    // DISPLAY //

    private void displayInfo(String strInfo) {
        tvInfo.setText(strInfo);
    }

    private void displayBeacons(@NonNull List<Beacon> listBeacons) {
        // generate beacons info
        StringBuilder stringBuilder = new StringBuilder();
        for (Beacon beacon: listBeacons) {
            // check type
            int serviceUid = beacon.getServiceUuid();
            if (serviceUid != 0xFEAA) { // this is an iBeacon or ALTBeacon
                // get beacon info
//                String bluetoothAddress = beacon.getBluetoothAddress();
//                String bluetoothName = beacon.getBluetoothName();
                String distance = String.format(Locale.getDefault(), "%.2f", beacon.getDistance());
                String uuid = beacon.getId1().toString();
                String major = beacon.getId2().toString();
                String minor = beacon.getId3().toString();
                // generate info string
                stringBuilder.append("UUID: ").append(uuid).append("\n");
                stringBuilder.append("Distance: ").append(distance).append("\n");
                stringBuilder.append("Major: ").append(major).append("\n");
                stringBuilder.append("Minor: ").append(minor).append("\n");
                // ad break
                stringBuilder.append("\n\n");
            }
        }
        // display
        tvBeacons.setText(stringBuilder.toString());
    }

    // PERMISSIONS //

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                displayInfo("Requesting Permissions...");
                // if not granted ask for it
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                handlePermissionsState(true);
            }
        } else {
            handlePermissionsState(true);
        }
    }

    private void handlePermissionsState(boolean granted) {
        if (granted) {
            // remove warning
            displayInfo("Permissions Granted");
            // setup beacon
            initBeaconDetection();
        } else {
            // display warning
            displayInfo("Permissions Not Granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                // if granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handlePermissionsState(true);
                } else {
                    handlePermissionsState(false);
                }
            }
        }
    }
}
