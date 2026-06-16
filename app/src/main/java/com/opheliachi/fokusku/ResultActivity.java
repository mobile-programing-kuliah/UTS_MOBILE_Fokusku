package com.opheliachi.fokusku;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.TextView;
import android.content.SharedPreferences;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tampilkan Statistik
        TextView tvTotalMinutes = findViewById(R.id.tvTotalMinutes);
        TextView tvTotalSessions = findViewById(R.id.tvTotalSessions);

        SharedPreferences prefs = getSharedPreferences("FokuskuPrefs", MODE_PRIVATE);
        int totalMinutes = prefs.getInt("total_focus_minutes", 0);
        
        tvTotalMinutes.setText(String.valueOf(totalMinutes));
        tvTotalSessions.setText(getString(R.string.btn_finish)); // Using existing "Selesai" string

        // Tombol Kembali ke Beranda
        MaterialButton btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            // Kembali ke MainActivity dan membersihkan stack activity
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}