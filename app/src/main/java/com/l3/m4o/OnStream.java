package com.l3.m4o;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.util.VLCVideoLayout;

public class OnStream extends AppCompatActivity implements IVLCVout.Callback{
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String IP_PORT = "192.168.1.10:8080";
    private final static String ARG = "h264_pcm.sdp";
    private final static String RTSP_URL = "rtsp://" + USERNAME + ":" + PASSWORD + "@" + IP_PORT + "/" + ARG;


    private final Context context = this;
    public static MutableLiveData<String> listenForPlayStream = new MutableLiveData<>();
    private WifiController wifiController;
    private Button btnStartStream;
    private Button btnStopStream;
    private VLCVideoLayout vlcVideoLayout;
    private MediaPlayer mediaPlayer;
    private LibVLC libVLC;
    private boolean connexionSearch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_stream);

        this.configureToolbar();

        listenForPlayStream.observe(this, s -> {
            System.out.println("Value listenForPlayStream is changed");
            startStream();
        });
        vlcVideoLayout = findViewById(R.id.activity_main_video_view);
        wifiController = new WifiController(context);
        btnStartStream = findViewById(R.id.activity_on_stream_start_btn);
        btnStartStream.setOnClickListener(v -> {
                if(connexionSearch){
                    triggerMechanism();
                    connexionSearch = false;
                }
                else
                    calcelPause();
        });
        btnStopStream = findViewById(R.id.activity_on_stream_stop_btn);
        btnStopStream.setOnClickListener(v -> {
            stopStram();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_main_about) {
            Intent intent = new Intent(OnStream.this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_activity_main_contact) {
            Intent intent = new Intent(OnStream.this, ContactActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
            Toolbar toolbar = (Toolbar) findViewById(R.id.activity_on_stream_toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void triggerMechanism() {
        wifiController.startWifiEnable();
    }

    public void startStream() {
        btnStartStream.setEnabled(false);
        btnStartStream.setTextColor(Color.GRAY);
        libVLC = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        Media media = new Media(libVLC, Uri.parse(RTSP_URL));
        media.setHWDecoderEnabled(true, false);
        media.addOption("network-caching=500");
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
        btnStopStream.setEnabled(true);
        btnStopStream.setTextColor(ContextCompat.getColor(context, R.color.teal_200));
    }

    private void stopStram() {
        btnStopStream.setEnabled(false);
        btnStopStream.setTextColor(Color.GRAY);
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        btnStartStream.setEnabled(true);
        btnStartStream.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
    }

    private void calcelPause() {
        mediaPlayer.play();
        btnStopStream.setEnabled(true);
        btnStopStream.setTextColor(ContextCompat.getColor(context, R.color.teal_200));
        btnStartStream.setEnabled(false);
        btnStartStream.setTextColor(Color.GRAY);
    }

    @Override
    protected void onDestroy() {
        if(!connexionSearch) {
            mediaPlayer.pause();
            btnStopStream.setEnabled(true);
            btnStopStream.setTextColor(ContextCompat.getColor(context, R.color.purple_500));
            btnStartStream.setEnabled(false);
            btnStartStream.setTextColor(Color.GRAY);
        }
        super.onDestroy();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }


}