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
			Log.i(LOG_TAG, "SMS Received");
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
					String content = msgs[i].getMessageBody().toString();
					Log.i(LOG_TAG, content);
					writeSD(content);
					startMap(context, content);
				}
				// ---display the new SMS message---
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void startMap(Context context, String content) {
		try {
			if (!content.contains(","))
				return;
			String token[] = content.split(",");
			if (token == null)
				return;
			if (token.length < 2)
				return;

			String uri = "geo:" + token[0] + "," + token[1]
					+ "?q=my+street+address";
			
			Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
			mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mapIntent);
		} catch (Exception e) {
			Log.e(LOG_TAG, "startMap failed", e);
		}
	}

	private void writeSD(String loc) {
		FileOutputStream f = null;
		try {
			// write to file
			f = new FileOutputStream(new File(Environment
					.getExternalStorageDirectory().getAbsolutePath(),
					"gpsRecieve.csv"), true);
			if (null != f) {
				f.write((loc + "\n").getBytes());
				f.flush();
				Log.i(LOG_TAG, "wrote : " + loc);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "write failed", e);
		} finally {
			if (null != f) {
				try {
					f.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "FileOutputStream close", e);
				}
			}
		}
	}
}
