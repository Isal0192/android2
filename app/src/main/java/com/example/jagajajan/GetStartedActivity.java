package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class GetStartedActivity extends AppCompatActivity {
    Button Lanjut, Lewati;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);
        hideSystemUI();

        Lanjut = findViewById(R.id.lanjutkan);
        Lewati = findViewById(R.id.lewati);

        // Cek apakah sudah login atau belum
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);  // Default: false

        if (isLoggedIn) {
            // Sudah login, langsung ke Home
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish(); // agar tidak bisa kembali ke halaman ini
        }

        // Jika belum login, tampilkan tombol dan izinkan user memilih
        Lewati.setOnClickListener(v -> {
            // Langsung ke halaman Login
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Lanjut.setOnClickListener(v -> {
            // Lanjut ke onboarding/main info
            Intent intent = new Intent(getApplicationContext(), GetStartedInfoActivity.class);
            startActivity(intent);
        });
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
