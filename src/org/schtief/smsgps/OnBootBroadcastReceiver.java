package org.schtief.smsgps;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;


public class OnBootBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "SMSGPS";


    @Override
    public void onReceive(Context c, Intent intent) {

        Log.i(TAG, "        Context:" + c);
        Log.i(TAG, "received intent: " + intent);
        Log.i(TAG, "received intent.getType(): " + intent.getType());
        Log.i(TAG, "received intent.describeContents(): " + intent.describeContents());
        Log.i(TAG, "received Intent.getDataString(): " + intent.getDataString());

        if (intent.getExtras() == null) {
            Log.i(TAG, "Boot complete");

            c.startService(new Intent(c, SmsGpsService.class));

        } else {

            Log.i(TAG, "extra: " + intent.getExtras().keySet());

            Bundle extras = intent.getExtras();
            Log.i(TAG, "extra.describeContents(): " + extras.describeContents());

            for (String key : extras.keySet()) {
                Log.i(TAG, "EXTRAS: " + key + ": " + extras.get(key));
            }

            Integer temperature = extras.getInt("temperature");
            Integer level = extras.getInt("level");
            Log.i(TAG, "      Level: " + level);
            Log.i(TAG, "Temperature: " + temperature);
            if (temperature != null) {
                SmsGpsService.SetTemparature(temperature);
            }

            if (level != null) {
                SmsGpsService.SetLevel(level);

            }
        }

    }

}