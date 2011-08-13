package org.schtief.smsgps;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


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

        }

    }

}