package com.riad.detector_master;

import static java.lang.System.exit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import pl.droidsonroids.gif.GifImageView;

public class StepDetector extends AppCompatActivity implements SensorEventListener {

    int steps = 0;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    GifImageView gif;
    //start ads coding
    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-7230640014460944/8538580356";
    private static final String TAG = "StepDetector";

    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gameIsInProgress;
    private long timerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detector);

        gif=(GifImageView) findViewById(R.id.id_gif2);
        gif.setVisibility(View.INVISIBLE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            exit(-1);
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //start ads coding
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAd();
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // showInterstitial();
            }
        });

        startGame();

        //end ads coding
    }
    // start ads coding
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        StepDetector.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        showInterstitial();
                        Toast.makeText(StepDetector.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        StepDetector.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        StepDetector.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                        StepDetector.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void createTimer(final long milliseconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds, 50 ) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                showInterstitial();
            }
            @Override
            public void onFinish() {
                gameIsInProgress = false;
                retryButton.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        }
    }
    private void startGame() {
        if (interstitialAd == null) {
            loadAd();
        }
        retryButton.setVisibility(View.INVISIBLE);
        resumeGame(GAME_LENGTH_MILLISECONDS);
    }
    private void resumeGame(long milliseconds) {
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }

    //end ads coding
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Increment the step counter
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            TextView stepsTextView = findViewById(R.id.id_textview);
            stepsTextView.setText("Total Step : "+String.valueOf(steps));

            if (stepsTextView.getText().length()>0){
              gif.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
