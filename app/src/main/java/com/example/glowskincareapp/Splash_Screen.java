package com.example.glowskincareapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();

        handler.postDelayed(() -> {
            Intent intent = new Intent(Splash_Screen.this, MainActivity.class);
            intent.setType(Settings.ACTION_SYNC_SETTINGS);
            Splash_Screen.this.startActivity(intent);
            finish();
        },  1000);


    }

}