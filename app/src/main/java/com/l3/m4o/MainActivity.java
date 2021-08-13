package com.l3.m4o;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSION = 1;
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "admin";
    private final static String IP_PORT = "192.168.1.4:8080";
    private final static String ARG = "h264_pcm.sdp";
    private final static String RTSP_URL = "rtsp://" + USERNAME + ":" + PASSWORD + "@" + IP_PORT + "/" + ARG;
    private final static String RTSP_URL1 = "rtsp://" + IP_PORT + "/" + ARG;


    private final Context context = this;
    int i = 0;
    public static MutableLiveData<String> listen = new MutableLiveData<>();
    WifiController wifiController;
    Button btnGo;
    VideoView videoView;
    VLCVideoLayout vlcVideoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureToolbar();
        listen.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                i++;
                System.out.println("Value is changed");
                Toast.makeText(context, "i = " + i, Toast.LENGTH_SHORT).show();
                startStream();
            }
        });
        vlcVideoLayout = findViewById(R.id.activity_main_video_view);
        wifiController = new WifiController(context);
        btnGo = findViewById(R.id.activity_main_go_btn);
        btnGo.setOnClickListener(v -> {
            if (permissionIsOK())
                triggerMechanism();
            else
                System.out.println("permission denied");
        });
        //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(myIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void triggerMechanism() {
        wifiController.startWifiResult();
    }

    public void startStream() {
        /*videoView.setVideoURI(Uri.parse(RTSP_URL1));
        videoView.requestFocus();
        videoView.start();*/
        LibVLC libVLC = new LibVLC(this);
        MediaPlayer mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        Media media = new Media(libVLC, Uri.parse(RTSP_URL1));
        media.setHWDecoderEnabled(true, false);
        media.addOption("network-caching=500");
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
    }

    public boolean permissionIsOK() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            System.out.println("Demande de permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSION);
            return false;
        } else {
            System.out.println(
                    Manifest.permission.ACCESS_FINE_LOCATION +
                            " / " +
                            Manifest.permission.ACCESS_COARSE_LOCATION +
                            " / " +
                            Manifest.permission.CHANGE_WIFI_STATE +
                            " / " +
                            PackageManager.PERMISSION_GRANTED +
                            " / " +
                            ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
            );
            System.out.println("PERMISSION GRANTED");
        }

        return true;
    }
}