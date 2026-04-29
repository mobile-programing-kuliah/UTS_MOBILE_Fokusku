package com.opheliachi.fokusku;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView card15, card25, card30, cardCustom;
    private TextView tv15, tv25, tv30, tvCustom;

    private MaterialCardView cardSession1, cardSession2, cardSession4, cardSessionEdit;
    private TextView tvSession1, tvSession2, tvSession4, tvSessionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi Views
        initDurationViews();
        initSessionViews();

        // Tombol Mulai
        Button btnStartFocus = findViewById(R.id.btnStartFocus);
        btnStartFocus.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        });
    }

    private void initDurationViews() {
        card15 = findViewById(R.id.card15);
        card25 = findViewById(R.id.card25);
        card30 = findViewById(R.id.card30);
        cardCustom = findViewById(R.id.cardCustom);
        tv15 = findViewById(R.id.tv15);
        tv25 = findViewById(R.id.tv25);
        tv30 = findViewById(R.id.tv30);
        tvCustom = findViewById(R.id.tvCustom);

        card15.setOnClickListener(v -> updateSelection(card15, tv15, "duration"));
        card25.setOnClickListener(v -> updateSelection(card25, tv25, "duration"));
        card30.setOnClickListener(v -> updateSelection(card30, tv30, "duration"));
        cardCustom.setOnClickListener(v -> updateSelection(cardCustom, tvCustom, "duration"));
    }

    private void initSessionViews() {
        cardSession1 = findViewById(R.id.cardSession1);
        cardSession2 = findViewById(R.id.cardSession2);
        cardSession4 = findViewById(R.id.cardSession4);
        cardSessionEdit = findViewById(R.id.cardSessionEdit);
        tvSession1 = findViewById(R.id.tvSession1);
        tvSession2 = findViewById(R.id.tvSession2);
        tvSession4 = findViewById(R.id.tvSession4);
        tvSessionEdit = findViewById(R.id.tvSessionEdit);

        cardSession1.setOnClickListener(v -> updateSelection(cardSession1, tvSession1, "session"));
        cardSession2.setOnClickListener(v -> updateSelection(cardSession2, tvSession2, "session"));
        cardSession4.setOnClickListener(v -> updateSelection(cardSession4, tvSession4, "session"));
        cardSessionEdit.setOnClickListener(v -> updateSelection(cardSessionEdit, tvSessionEdit, "session"));
    }

    private void updateSelection(MaterialCardView selectedCard, TextView selectedTv, String type) {
        // Reset kelompok kartu yang sesuai
        if (type.equals("duration")) {
            resetCard(card15, tv15);
            resetCard(card25, tv25);
            resetCard(card30, tv30);
            resetCard(cardCustom, tvCustom);
        } else {
            resetCard(cardSession1, tvSession1);
            resetCard(cardSession2, tvSession2);
            resetCard(cardSession4, tvSession4);
            resetCard(cardSessionEdit, tvSessionEdit);
        }

        // Set state Aktif (Primary Green)
        float density = getResources().getDisplayMetrics().density;
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_green));
        selectedCard.setCardElevation(8 * density);
        selectedCard.setStrokeWidth(0);
        selectedTv.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void resetCard(MaterialCardView card, TextView tv) {
        // Set state Tidak Aktif (Transparent dengan Border)
        float density = getResources().getDisplayMetrics().density;
        card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        card.setCardElevation(0f);
        card.setStrokeWidth((int) (2 * density)); // Kembali ke 2dp seperti di XML
        card.setStrokeColor(ContextCompat.getColor(this, R.color.soft_green));
        tv.setTextColor(ContextCompat.getColor(this, R.color.dark_green));
    }
}