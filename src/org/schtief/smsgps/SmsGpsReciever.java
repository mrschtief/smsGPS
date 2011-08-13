package org.schtief.smsgps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsGpsReciever extends BroadcastReceiver {
	private static final String LOG_TAG = "SmsGpsReciever";

	static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Toast.makeText(context, "SMS Received ", Toast.LENGTH_LONG);
			// ---get the SMS message passed in---
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;
			String str = "";
			if (bundle != null) {
				// ---retrieve the SMS message received---
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					str += "SMS from " + msgs[i].getOriginatingAddress();
					str += " :";
					str += msgs[i].getMessageBody().toString();
					str += "\n";
					
					String content	=msgs[i].getMessageBody().toString();
					writeSD(content);
					
				}
				// ---display the new SMS message---
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void writeSD(String loc) {
		FileOutputStream f =null;
		try {
			// write to file
			f =  new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "gpsRecieve.csv"), true);
			if (null != f) {
				f.write((loc + "\n").getBytes());
				f.flush();
				Log.i(LOG_TAG, "wrote : "+loc);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "write location", e);
		}finally{
			if(null!=f){
				try {
					f.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "FileOutputStream close", e);
				}
			}
		}
	}
}
