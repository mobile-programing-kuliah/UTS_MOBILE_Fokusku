package com.opheliachi.fokusku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;
import java.util.Objects;

import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        
        // Inisialisasi ViewModel
        TimerViewModel viewModel = new ViewModelProvider(this).get(TimerViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Elements
        TextView tvTimer = findViewById(R.id.tvTimer);
        CircularProgressIndicator timerProgress = findViewById(R.id.timerProgress);
        TextView tvSessionProgress = findViewById(R.id.tvSessionProgress);
        MaterialButton btnFinish = findViewById(R.id.btnFinish);

        // Observasi Data
        viewModel.getTimeText().observe(this, tvTimer::setText);
        viewModel.getProgress().observe(this, progress -> {
            // Gunakan animasi perlahan agar transisi bar tidak merusak efek wavy
            timerProgress.setProgress(progress, true);
        });
        
        TimerService.currentSession.observe(this, current -> {
            int total = Objects.requireNonNullElse(TimerService.totalSessions.getValue(), 4);
            tvSessionProgress.setText(getString(R.string.session_progress_template, current, total));
        });

        TimerService.isPaused.observe(this, paused -> {
            btnFinish.setText(paused ? getString(R.string.btn_resume) : getString(R.string.btn_pause));
            btnFinish.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, paused ? R.color.primary_green : R.color.dark_green)
            ));
        });

        viewModel.getIsFinished().observe(this, finished -> {
            if (finished) {
                saveFocusTime();
                handleNavigation();
            }
        });

        // Ambil durasi & sesi dari Intent
        int durationMinutes = getIntent().getIntExtra("DURATION_MINUTES", 25);
        int totalSessions = getIntent().getIntExtra("TOTAL_SESSIONS", 4);
        long durationMillis = (long) durationMinutes * 60 * 1000;

        // Jalankan Timer via Service
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.setAction("START");
        serviceIntent.putExtra("DURATION_MILLIS", durationMillis);
        serviceIntent.putExtra("TOTAL_SESSIONS", totalSessions);
        startForegroundService(serviceIntent);

        // Tombol Kembali
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Tombol Jeda / Lanjutkan (Menggunakan btnFinish)
        btnFinish.setOnClickListener(v -> {
            Boolean paused = TimerService.isPaused.getValue();
            Intent actionIntent = new Intent(this, TimerService.class);
            actionIntent.setAction(paused != null && paused ? "RESUME" : "PAUSE");
            startService(actionIntent);
        });

        // Tombol Batalkan
        MaterialButton btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            stopService(new Intent(this, TimerService.class));
            finish();
        });
    }

    private void saveFocusTime() {
        int durationMinutes = getIntent().getIntExtra("DURATION_MINUTES", 25);
        Integer current = TimerService.currentSession.getValue();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault());
        String currentDate = sdf.format(new java.util.Date());

        try (DatabaseHelper db = new DatabaseHelper(this)) {
            db.addHistory(currentDate, durationMinutes, current != null ? current : 1);
        }
        
        // Simpan juga total menit ke SharedPreferences untuk statistik cepat di ResultActivity
        android.content.SharedPreferences prefs = getSharedPreferences("FokuskuPrefs", MODE_PRIVATE);
        int totalTime = prefs.getInt("total_focus_minutes", 0);
        prefs.edit().putInt("total_focus_minutes", totalTime + durationMinutes).apply();
    }

    private void handleNavigation() {
        Integer current = TimerService.currentSession.getValue();
        Integer total = TimerService.totalSessions.getValue();
        
        if (current != null && total != null && current >= total) {
            startActivity(new Intent(this, LongBreakActivity.class));
        } else {
            startActivity(new Intent(this, BreakActivity.class));
        }
        finish();
    }
}