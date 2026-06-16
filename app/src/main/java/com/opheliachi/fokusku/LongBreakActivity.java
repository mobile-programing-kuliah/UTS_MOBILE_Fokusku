package com.opheliachi.fokusku;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.widget.TextView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class LongBreakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_long_break);
        
        // Inisialisasi ViewModel
        LongBreakViewModel viewModel = new ViewModelProvider(this).get(LongBreakViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Elements
        TextView tvTimer = findViewById(R.id.tvTimer);
        CircularProgressIndicator longBreakProgress = findViewById(R.id.longBreakProgress);

        // Observasi Data
        viewModel.getTimeText().observe(this, tvTimer::setText);
        viewModel.getProgress().observe(this, longBreakProgress::setProgress);
        viewModel.getIsFinished().observe(this, finished -> {
            if (finished) {
                navigateToResult();
            }
        });

        // Start Timer via Service (Istirahat Panjang 15 menit = 900,000 ms)
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("DURATION_MILLIS", 900000L);
        startForegroundService(serviceIntent);

        // Tombol Ke Beranda
        MaterialButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            stopService(new Intent(this, TimerService.class));
            navigateToResult();
        });

        // Tombol Lewati Istirahat
        MaterialButton btnSkipBreak = findViewById(R.id.btnSkipBreak);
        btnSkipBreak.setOnClickListener(v -> {
            stopService(new Intent(this, TimerService.class));
            navigateToResult();
        });
    }

    private void navigateToResult() {
        Intent intent = new Intent(LongBreakActivity.this, ResultActivity.class);
        startActivity(intent);
        finish();
    }
}