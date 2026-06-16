package com.opheliachi.fokusku;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import java.util.Locale;

public class TimerService extends Service {

    public static final String CHANNEL_ID = "TimerServiceChannel";
    private final IBinder binder = new TimerBinder();

    public static MutableLiveData<String> timeText = new MutableLiveData<>("00:00");
    public static MutableLiveData<Integer> progress = new MutableLiveData<>(100);
    public static MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);
    public static MutableLiveData<Boolean> isPaused = new MutableLiveData<>(false);
    
    // Logika Sesi
    public static MutableLiveData<Integer> currentSession = new MutableLiveData<>(1);
    public static MutableLiveData<Integer> totalSessions = new MutableLiveData<>(4);

    private CountDownTimer countDownTimer;
    private long totalTimeMillis;
    private long remainingTimeMillis;

    public class TimerBinder extends Binder {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case "START":
                        long duration = intent.getLongExtra("DURATION_MILLIS", 0);
                        int sessions = intent.getIntExtra("TOTAL_SESSIONS", 4);
                        int current = intent.getIntExtra("CURRENT_SESSION", 1);

                        totalSessions.postValue(sessions);
                        currentSession.postValue(current);

                        startTimer(duration);
                        break;
                    case "PAUSE":
                        pauseTimer();
                        break;
                    case "RESUME":
                        resumeTimer();
                        break;
                    case "STOP":
                        stopSelf();
                        break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void startTimer(long durationMillis) {
        this.totalTimeMillis = durationMillis;
        this.remainingTimeMillis = durationMillis;
        isPaused.postValue(false);
        runTimer(durationMillis);
        
        Notification notification = createNotification(getString(R.string.notif_timer_started));
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }
    }

    private void runTimer(long duration) {
        if (countDownTimer != null) countDownTimer.cancel();

        // Kembali ke 1 detik agar performa render wavy terjaga
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateUI(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                handleTimerFinished();
            }
        }.start();
    }

    private void updateUI(long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeText.postValue(timeFormatted);
        
        // Tetap gunakan skala 1000 untuk presisi
        int currentProgress = (int) ((millisUntilFinished * 1000) / totalTimeMillis);
        progress.postValue(currentProgress);
        updateNotification(getString(R.string.notif_remaining, timeFormatted));
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isPaused.postValue(true);
            updateNotification(getString(R.string.notif_timer_paused));
        }
    }

    public void resumeTimer() {
        isPaused.postValue(false);
        runTimer(remainingTimeMillis);
    }

    private void handleTimerFinished() {
        timeText.postValue("00:00");
        progress.postValue(0);
        isFinished.postValue(true);
        playNotificationFeedback();
        stopForeground(true);
        stopSelf();
    }

    private void playNotificationFeedback() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            RingtoneManager.getRingtone(getApplicationContext(), alarmSound).play();
        } catch (Exception e) {
            android.util.Log.e("TimerService", "Error playing notification", e);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID, "Fokusku Timer Service", NotificationManager.IMPORTANCE_LOW);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(serviceChannel);
    }

    private Notification createNotification(String contentText) {
        Intent notificationIntent = new Intent(this, TimerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notif_title))
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void updateNotification(String text) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.notify(1, createNotification(text));
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }
}