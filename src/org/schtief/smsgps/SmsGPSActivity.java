package org.schtief.smsgps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;

public class SmsGPSActivity extends Activity implements OnClickListener {
    private static final String TAG = "ServicesDemo";
    Button buttonStart, buttonStop, buttonVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonVideo = (Button) findViewById(R.id.buttonVideo);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonVideo.setOnClickListener(this);
    }

    public void onClick(View src) {
        switch (src.getId()) {
            case R.id.buttonStart:
                Log.d(TAG, "onClick: starting srvice");
                startService(new Intent(this, SmsGpsService.class));
                break;
            case R.id.buttonStop:
                Log.d(TAG, "onClick: stopping srvice");
                stopService(new Intent(this, SmsGpsService.class));
                break;
            case R.id.buttonVideo:
                Log.d(TAG, "onClick: Video Recording");
                Intent i = new Intent("android.media.action.VIDEO_CAPTURE");
                i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/recording" + System.currentTimeMillis() + ".mp4")));
                i.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 0);
                i.putExtra("android.intent.extra.durationLimit", 10 * 60 * 60);
                startActivityForResult(i, 2);

                break;
        }
    }
}