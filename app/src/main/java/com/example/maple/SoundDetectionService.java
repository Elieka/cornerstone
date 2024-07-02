package com.example.maple;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.Nullable;

public class SoundDetectionService extends Service {
    private static final String TAG = "SoundDetectionService";

    private MediaRecorder mediaRecorder;
    private Vibrator vibrator;
    private Handler handler = new Handler();
    private boolean isVibrating = false;
    private static final int SOUND_THRESHOLD = 10;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");

        try {
            initializeMediaRecorder();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MediaRecorder: " + e.getMessage());
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        handler.postDelayed(monitorSoundRunnable, 1000);
    }

    private void initializeMediaRecorder() throws Exception {
        if (mediaRecorder != null) {
            mediaRecorder.release();
        }
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile("/dev/null");
        mediaRecorder.prepare();
        mediaRecorder.start();
        Log.d(TAG, "MediaRecorder started");
    }

    private Runnable monitorSoundRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                double amplitude = mediaRecorder.getMaxAmplitude();
                double decibel = 20 * Math.log10(amplitude / 32767.0);
                Log.d(TAG, "Decibel level: " + decibel);

                if (decibel > SOUND_THRESHOLD && !isVibrating) {
                    Log.d(TAG, "Sound threshold exceeded");
                    startVibration();
                    wakeUpScreen();
                    Intent alertIntent = new Intent(SoundDetectionService.this, AlertActivity.class);
                    alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(alertIntent);
                    Log.d(TAG, "AlertActivity started");
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "MediaRecorder getMaxAmplitude called in an invalid state: " + e.getMessage());
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void startVibration() {
        isVibrating = true;
        long[] pattern = {0, 1000, 1000};
        vibrator.vibrate(pattern, 0);
        Log.d(TAG, "Vibration started");
    }

    private void stopVibration() {
        isVibrating = false;
        vibrator.cancel();
        Log.d(TAG, "Vibration stopped");
    }

    private void wakeUpScreen() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myApp:myWakelockTag"
        );
        wakeLock.acquire(3000); // 3 seconds
        Log.d(TAG, "Screen woken up");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_VIBRATION".equals(intent.getAction())) {
            stopVibration();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(monitorSoundRunnable);
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
