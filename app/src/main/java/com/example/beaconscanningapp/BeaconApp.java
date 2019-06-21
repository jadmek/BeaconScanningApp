package com.example.beaconscanningapp;

import android.app.Application;

import com.example.beaconscanningapp.Utils.SendNotificationUtility;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class BeaconApp extends Application implements BootstrapNotifier {

    private BackgroundPowerSaver backgroundPowerSaver;
    private static final String TAG = BeaconApp.class.getSimpleName();
    private RegionBootstrap regionBootstrap;
    private static final String BEACON_FILTER = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    @Override
    public void onCreate() {
        super.onCreate();
        //set up beacon manager
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        // give the beacon manager our beacon layout, to filter our scans for these types of beacons only
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BEACON_FILTER));
        // for now this region indicates that we want all beacons with the above layout.
        // the null values indiciates that the only filter we are aplying on our scans, if for the beacons to be of the layout
        // specified above.
        Region region = new Region("exampleRegion", null, null, null);
        // later on we should get a list of beacons and insert their major and minor values in place of the null.
        // search for those specifically:
        regionBootstrap = new RegionBootstrap(this, region);
        // calling this saves backround power by 80%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    /**
     * Called when a beacon is sensed for the first time
     * @param region a specfic beacon
     */
    @Override
    public void didEnterRegion(Region region) {
        // Sending notification when we enter the range of a beacon
        SendNotificationUtility.sendNotification(getBaseContext(), "We entered beacon with major and minor: " + region.getId2() + " " + region.getId3());
    }

    /**
     * Called when a previously sensed beacon is no longer sensed
     * @param region a specific beacon
     */
    @Override
    public void didExitRegion(Region region) {
        // sending a notification when we exit the range of a beacon
        SendNotificationUtility.sendNotification(getBaseContext(), "We exited beacon with major and minor: " + region.getId2() + " " + region.getId3());
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
