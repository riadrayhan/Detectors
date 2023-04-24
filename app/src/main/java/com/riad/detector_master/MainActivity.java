package com.riad.detector_master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import soup.neumorphism.NeumorphButton;

public class MainActivity extends AppCompatActivity {

    NeumorphButton step,metal,speed,light,sound,temperature,pressure;

    private AdView adView;
    private static final String TAG = "Speed";
    private static final String AD_UNIT_ID = "ca-app-pub-7230640014460944/4861993561";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ads
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        //end ads

        step=(NeumorphButton) findViewById(R.id.id_step);
        metal=(NeumorphButton) findViewById(R.id.id_metal);
        speed=(NeumorphButton) findViewById(R.id.id_speed);
        light=(NeumorphButton) findViewById(R.id.id_light);
        sound=(NeumorphButton) findViewById(R.id.id_sound);
        temperature=(NeumorphButton) findViewById(R.id.id_temperature);
        pressure=(NeumorphButton) findViewById(R.id.id_pressure);

        getSupportActionBar().hide();


        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,StepDetector.class);
                startActivity(i);
            }
        });
        metal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Metal.class);
                startActivity(i);
            }
        });
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Speed.class);
                startActivity(i);
            }
        });
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Light.class);
                startActivity(i);
            }
        });
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Sound.class);
                startActivity(i);
            }
        });
        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Temperature.class);
                startActivity(i);
            }
        });
        pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Pressure.class);
                startActivity(i);
            }
        });
    }
}