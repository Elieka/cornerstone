package com.example.maple;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity started");

        // Check for audio recording permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startSoundDetectionService();
        }

        Button stopVibrationButton = findViewById(R.id.stopVibrationButton);
        stopVibrationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SoundDetectionService.class);
            intent.setAction("STOP_VIBRATION");
            startService(intent);
            Log.d(TAG, "Stop Vibration button clicked");
        });
    }

    private void startSoundDetectionService() {
        Intent serviceIntent = new Intent(this, SoundDetectionService.class);
        startService(serviceIntent);
        Log.d(TAG, "SoundDetectionService started from MainActivity");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSoundDetectionService();
            } else {
                Log.e(TAG, "Permission for audio recording denied");
            }
        }
    }
}