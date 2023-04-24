package com.riad.detector_master;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


public class Pressure extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private boolean isPressureSensorAvailable;
    private Button startButton;
    private TextView pressureText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        isPressureSensorAvailable = (pressureSensor != null);

        startButton = (Button) findViewById(R.id.startButton);
        pressureText = (TextView) findViewById(R.id.pressureText);

        startButton.setOnClickListener(view -> {
            if (isPressureSensorAvailable) {
                sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
                startButton.setEnabled(false);
            } else {
                pressureText.setText("Pressure sensor not available in your device.");
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float pressure = event.values[0];
        pressureText.setText(String.format("%.1f hPa", pressure));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPressureSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}