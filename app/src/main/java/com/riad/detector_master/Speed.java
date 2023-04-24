package com.riad.detector_master;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.util.Locale;

import soup.neumorphism.NeumorphButton;

public  class Speed extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private NeumorphButton speedTextView,stepTextView;
    private int stepCount = 0;
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorAvailable;
    private float stepLength = 0.7f; // in meters

    private AdView adView;
    private static final String TAG = "Speed";
    private static final String AD_UNIT_ID = "ca-app-pub-7230640014460944/4861993561";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);

        speedTextView = findViewById(R.id.id_speedtext);
        stepTextView = findViewById(R.id.id_textview);

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            isSensorAvailable = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        if (isSensorAvailable) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                        stepCount = (int) event.values[0];
                        //stepTextView.setText(String.format(Locale.getDefault(), "Steps: %d", stepCount));
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {


        super.onPause();
        locationManager.removeUpdates(this);
        if (isSensorAvailable) {
            sensorManager.unregisterListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            });
        }
        locationManager.removeUpdates(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            float speed = location.getSpeed();
            speedTextView.setText(String.format(Locale.getDefault(), "Speed: %.1f km/h", speed * 3.6));
            float distance = stepCount * stepLength;
            float pace = distance / ((float) location.getTime() / 1000 / 60);
            String paceString = String.format(Locale.getDefault(), "Pace: %.1f min/km", pace);
            speedTextView.append("\n" + paceString);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
