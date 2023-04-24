package com.riad.detector_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Metal extends AppCompatActivity implements SensorEventListener {

    private TextView value;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    LinearLayout ll;
    MediaPlayer mp;
    //start ads coding
    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-7230640014460944/8538580356";
    private static final String TAG = "Metal";

    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gameIsInProgress;
    private long timerMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metal);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
        }

        value = (TextView) findViewById(R.id.value);
        ll=(LinearLayout)findViewById(R.id.id_ll);
        // define decimal formatter
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.000", symbols);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

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
                    Metal.this.interstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                    showInterstitial();
                    Toast.makeText(Metal.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                    interstitialAd.setFullScreenContentCallback(
                            new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    Metal.this.interstitialAd = null;
                                    Log.d("TAG", "The ad was dismissed.");
                                }
                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    Metal.this.interstitialAd = null;
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
                                    Metal.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
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
/*    @Override
    public void onResume() {
        super.onResume();
        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
    }*/
/*    @Override
    public void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }*/
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
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        countDownTimer.cancel();
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // get values for each axes X,Y,Z
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];

            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));

            // set value on the screen
            value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
            if (magnitude > 50){

                // Toast.makeText(getApplicationContext(),"metal detect...",Toast.LENGTH_SHORT).show();
                ll.setBackgroundColor(Color.RED);
                mp= MediaPlayer.create(this,R.raw.b);
                mp.start();
            }
            if (magnitude<49){

                //  mp.stop();
                ll.setBackgroundColor(Color.rgb(13, 36, 66));
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}


