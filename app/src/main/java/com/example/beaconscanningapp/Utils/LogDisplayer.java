package com.example.beaconscanningapp.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Class display system logs only in debug mode.
 */
public class LogDisplayer {

    public static void displayLogV(String tag, String message) {
        if(message != null && tag != null) {
            Log.v(tag, message);
            saveLogs(getDate() + " || " + tag + ": " + message + "\n");
        }
    }

    private static String getDate() {
        // Create a DateFormatter object for displaying date information.
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss.SSS", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    /**
     * gets the logs
     */
    public static void getLogs() {
        Process logcat;
        final StringBuilder log = new StringBuilder();
        try {
            // gets the logcat
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;
            String separator = System.getProperty("line.separator");
            // loop through logcat, and append it with line seperation between each log message
            while ((line = br.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
            saveLogs(log.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * saves the given log in file
     */
    private static void saveLogs(String log) {
        try {
            // gets the DCIM directory
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // creates file called log.txt
            File fileLog = new File(dir, "BeaconAppJadLog.txt");
            if (!fileLog.exists()) {
                fileLog.createNewFile();
            }
            // appends new data to file
            try {
                FileOutputStream out = new FileOutputStream(fileLog, true);
                out.write(log.getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // clear logs when done
//            clearLogs();
        } catch (Exception e) {

        }
    }

    /**
     * clears logs such that when we click save logs again, we dont repeat the same logs.
     */
    private static void clearLogs() {
        try {
            Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
