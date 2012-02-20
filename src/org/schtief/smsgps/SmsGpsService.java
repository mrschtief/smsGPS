package org.schtief.smsgps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsGpsService extends Service {

    private static final long INTERVAL = 120000;
    private static final String TAG = "SMSGPS";
    private static Integer level = -1;
    private static Integer temperature = -1;

    Location location = null;
    LocationListener locationListener = null;

    static DecimalFormat DF4 = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.US));
    static DecimalFormat DF2 = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private Timer timer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

        IntentFilter intentFilter = createBatteryIntentFilter();
        OnBootBroadcastReceiver br = new OnBootBroadcastReceiver();
        // register battery handler here
        this.registerReceiver(br, intentFilter);


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                SmsGpsService.this.location = location;
                Log.d(TAG, "onLocationChanged " + System.currentTimeMillis());
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.d(TAG, "onProviderEnabled " + System.currentTimeMillis());
            }

            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled " + System.currentTimeMillis());
            }

            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled " + System.currentTimeMillis());
            }
        };


        // Register the listener with the Location Manager to receive location
        // updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                INTERVAL, 0, locationListener);

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (null != SmsGpsService.this.location) {
                    String loc = "" + DF4.format(SmsGpsService.this.location.getLatitude())//8
                            + "," + DF4.format(SmsGpsService.this.location.getLongitude())//8
                            + "," + DF4.format(SmsGpsService.this.location.getAltitude())//6
                            + "," + DF4.format(SmsGpsService.this.location.getSpeed())//6
                            + "," + DF4.format(SmsGpsService.this.location.getAccuracy())//6
                            + "," + DF4.format(SmsGpsService.this.location.getBearing())//6
                            + "," + System.currentTimeMillis() / 1000
                            + "," + DF2.format(temperature / 10.0)
                            + "," + level;//10					Log.i(TAG, loc);
                    //write to file
                    writeSD(loc);
                    sendSMS(loc);
                } else
                    Log.d(TAG, "hello no location" + System.currentTimeMillis());
            }

            private void writeSD(String loc) {
                FileOutputStream f = null;
                try {
                    // write to file
                    f = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "gps.csv"), true);
                    if (null != f) {
                        f.write((loc + "\n").getBytes());
                        f.flush();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "write location", e);
                } finally {
                    if (null != f) {
                        try {
                            f.close();
                        } catch (IOException e) {
                            Log.e(TAG, "FileOutputStream close", e);
                        }
                    }
                }
            }

            private void sendSMS(String loc) {
                try {
                    SmsManager sms = SmsManager.getDefault();
		            sms.sendTextMessage("" /*insert phone number here/*, null, loc,  null, null);
                    sms.sendTextMessage(""/*insert phone number here/*, null, loc, null, null);
                } catch (Exception e) {
                    Log.e(TAG, "sendSMS", e);
                }
            }
        }, 0, INTERVAL);
    }


    private IntentFilter createBatteryIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        return intentFilter;
    }

    public static void SetLevel(Integer l) {
        level = l.intValue();
    }

    public static void SetTemparature(Integer t) {
        temperature = t.intValue();
    }
}
