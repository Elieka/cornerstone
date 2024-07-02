package com.example.maple;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {
    private static final String TAG = "AlertActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Log.d(TAG, "AlertActivity started");

        Button stopVibrationButton = findViewById(R.id.stopVibrationButton);
        stopVibrationButton.setOnClickListener(v -> {
            Intent intent = new Intent(AlertActivity.this, SoundDetectionService.class);
            intent.setAction("STOP_VIBRATION");
            startService(intent);
            Log.d(TAG, "Stop Vibration button clicked");
            finish();
        });
    }
}
