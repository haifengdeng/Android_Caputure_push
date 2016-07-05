package net.ossrs.pushdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import net.ossrs.sea.PushStream;

import net.ossrs.pushdemo.R;
import net.ossrs.sea.PushStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private String flv_url= "rtmp://10.128.164.55:1990/live/stream_test5";
    private int vbitrate_kbps = 1000;
    private static final String TAG = "SrsPublisher";

    private PushStream pushStreamer;
    // settings storage
    private SharedPreferences sp;

    private boolean isCameraBack =true;

    public MainActivity() {
        pushStreamer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("SrsPublisher", MODE_PRIVATE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // restore data.
        flv_url = sp.getString("FLV_URL", flv_url);
        vbitrate_kbps = sp.getInt("VBITRATE", vbitrate_kbps);
        isCameraBack = sp.getBoolean("BACK_FRONT",isCameraBack);
        Log.i(TAG, String.format("initialize flv url to %s, vbitrate=%dkbps", flv_url, vbitrate_kbps));

        // initialize url.
        final EditText efu = (EditText) findViewById(R.id.flv_url);
        efu.setText(flv_url);
        efu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String fu = efu.getText().toString();
                if (fu == flv_url || fu.isEmpty()) {
                    return;
                }

                flv_url = fu;
                Log.i(TAG, String.format("flv url changed to %s", flv_url));

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("FLV_URL", flv_url);
                editor.commit();
            }
        });

        final EditText evb = (EditText) findViewById(R.id.vbitrate);
        evb.setText(String.format("%dkbps", vbitrate_kbps));
        evb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int vb = Integer.parseInt(evb.getText().toString().replaceAll("kbps", ""));
                if (vb == vbitrate_kbps) {
                    return;
                }

                vbitrate_kbps = vb;
                Log.i(TAG, String.format("video bitrate changed to %d", vbitrate_kbps));

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("VBITRATE", vbitrate_kbps);
                editor.commit();

                if(null!=pushStreamer)
                {
                    pushStreamer.setVideoBitrate(vbitrate_kbps);
                }
            }
        });

        // for camera, @see https://developer.android.com/reference/android/hardware/Camera.html
        final Button btnPublish = (Button) findViewById(R.id.capture);
        final Button btnStop = (Button) findViewById(R.id.stop);
        final Button btnBackPreview = (Button) findViewById(R.id.back_front);
        final SurfaceView preview = (SurfaceView) findViewById(R.id.camera_preview);
        btnPublish.setEnabled(true);
        btnStop.setEnabled(false);
        btnBackPreview.setEnabled(true);
        if(!isCameraBack)
            btnBackPreview.setText("FronC");
        else
            btnBackPreview.setText("BackC");
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null !=pushStreamer)
                   pushStreamer.dispose();

                btnPublish.setEnabled(true);
                btnStop.setEnabled(false);
                btnBackPreview.setEnabled(true);
            }
        });

        btnBackPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCameraBack)
                    isCameraBack =false;
                else
                    isCameraBack =true;

                if(null !=pushStreamer)
                    pushStreamer.setCameraBack(isCameraBack);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("BACK_FRONT", isCameraBack);
                editor.commit();

                if(!isCameraBack)
                   btnBackPreview.setText("FronC");
                else
                    btnBackPreview.setText("BackC");
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null !=pushStreamer)
                    pushStreamer.dispose();
                if(null == pushStreamer)
                    pushStreamer=new PushStream();

                pushStreamer.setCameraBack(isCameraBack);
                pushStreamer.setVideoBitrate(vbitrate_kbps);
                pushStreamer.setWidthHeight(1280,720);
                pushStreamer.start( flv_url,preview.getHolder());
                btnPublish.setEnabled(false);
                btnStop.setEnabled(true);
                btnBackPreview.setEnabled(false);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        final Button btn = (Button) findViewById(R.id.capture);
        btn.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != pushStreamer)
            pushStreamer.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
