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

public class BreakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_break);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tombol Kembali Fokus (Ke Timer)
        MaterialButton btnBackToFocus = findViewById(R.id.btnBackToFocus);
        btnBackToFocus.setOnClickListener(v -> {
            Intent intent = new Intent(BreakActivity.this, TimerActivity.class);
            startActivity(intent);
            finish();
        });

        // Tombol Lewati Istirahat (Sama dengan kembali fokus untuk prototype)
        MaterialButton btnSkipBreak = findViewById(R.id.btnSkipBreak);
        btnSkipBreak.setOnClickListener(v -> {
            Intent intent = new Intent(BreakActivity.this, TimerActivity.class);
            startActivity(intent);
            finish();
        });
    }
}