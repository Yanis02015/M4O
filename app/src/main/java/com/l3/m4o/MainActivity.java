package com.l3.m4o;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.activity_main_start_btn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnStream.class);
            if (permissionIsOK())
                startActivity(intent);
        });
    }

    public boolean permissionIsOK() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) !=
                        PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Les permissions de localisation sont requises", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSION);
            return false;
        } else {
            System.out.println("PERMISSION GRANTED");
        }
        return true;
    }
}