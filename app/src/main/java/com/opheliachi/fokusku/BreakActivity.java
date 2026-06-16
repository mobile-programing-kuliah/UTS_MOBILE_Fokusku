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

public class BreakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_break);
        
        // Inisialisasi ViewModel
        BreakViewModel viewModel = new ViewModelProvider(this).get(BreakViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI Elements
        TextView tvTimer = findViewById(R.id.tvTimer);
        CircularProgressIndicator breakProgress = findViewById(R.id.breakProgress);

        // Observasi Data dari ViewModel
        viewModel.getTimeText().observe(this, tvTimer::setText);
        viewModel.getProgress().observe(this, breakProgress::setProgress);
        viewModel.getIsFinished().observe(this, finished -> {
            if (finished) {
                startNextSession();
            }
        });

        // Start Timer via Service (Istirahat 5 menit = 300,000 ms)
        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.setAction("START");
        serviceIntent.putExtra("DURATION_MILLIS", 300000L);
        startForegroundService(serviceIntent);

        // Tombol Kembali Fokus
        MaterialButton btnBackToFocus = findViewById(R.id.btnBackToFocus);
        btnBackToFocus.setOnClickListener(v -> {
            stopService(new Intent(this, TimerService.class));
            startNextSession();
        });

        // Tombol Lewati Istirahat
        MaterialButton btnSkipBreak = findViewById(R.id.btnSkipBreak);
        btnSkipBreak.setOnClickListener(v -> {
            stopService(new Intent(this, TimerService.class));
            startNextSession();
        });
    }

    private void startNextSession() {
        Integer current = TimerService.currentSession.getValue();
        
        Intent intent = new Intent(this, TimerActivity.class);
        if (current != null) {
            intent.putExtra("CURRENT_SESSION", current + 1);
        }
        startActivity(intent);
        finish();
    }
}