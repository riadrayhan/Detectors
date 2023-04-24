package com.riad.detector_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Sound extends AppCompatActivity {

    private AudioRecord audioRecord;
    private int bufferSize;
    private boolean isRecording;
    private ClapDetectionTask clapDetectionTask;
    ImageView image;
    TextView sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        image=(ImageView)findViewById(R.id.id_image);
        sound=(TextView)findViewById(R.id.id_sound);
        image.setVisibility(View.INVISIBLE);

        bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRecording = true;
        clapDetectionTask = new ClapDetectionTask();
        clapDetectionTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRecording = false;
        clapDetectionTask.cancel(true);
    }

    private class ClapDetectionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            audioRecord.startRecording();
            short[] buffer = new short[bufferSize];
            int bufferReadResult;
            int clapCount = 0;
            boolean lastPeak = false;
            while (isRecording) {
                bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    if (buffer[i] > 900 && !lastPeak) {
                        clapCount++;
                        lastPeak = true;
                    } else if (buffer[i] < 900) {
                        lastPeak = false;
                    }
                }
                if (clapCount >= 2) {
                    clapCount = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sound.setText("Sound Detected...");
                            image.setVisibility(View.VISIBLE);
                            Toast.makeText(Sound.this, "Sound detected!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            audioRecord.stop();
            return null;
        }
    }
}
