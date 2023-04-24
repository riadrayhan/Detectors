package com.riad.detector_master;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Light extends AppCompatActivity implements SensorEventListener {


    TextView textViewOutput;
    SensorManager sensorManager;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        textViewOutput = (TextView) findViewById(R.id.textViewSensorOutput);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        Log.v(TAG, "Lux = " + lux);

        try {
            textViewOutput = (TextView) findViewById(R.id.textViewSensorOutput);
            textViewOutput.setText("Light  : " + String.valueOf(lux) + " [lux]");
            if ( textViewOutput.getText().length()>2){
                Toast.makeText(getApplicationContext(),"Light Detected !",Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.v(TAG, "Exception = " + e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}