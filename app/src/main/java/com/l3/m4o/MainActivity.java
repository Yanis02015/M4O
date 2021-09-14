package com.l3.m4o;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.activity_main_start_btn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OnStream.class);
            startActivity(intent);
        });
    }
}