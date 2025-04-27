package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton perofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ConstantsVariabels.hideSystemUI(getWindow());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        perofile = findViewById(R.id.profile_icon);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        String id = sharedPreferences.getString("id", null);


        perofile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PerofileActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(Home.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_search:
                        Toast.makeText(Home.this, "Search clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_profile:
                        Toast.makeText(Home.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    // Agar immersive mode kembali saat layar disentuh
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ConstantsVariabels.hideSystemUI(getWindow());
    }

    // Agar immersive mode kembali saat user pindah balik ke layar ini
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ConstantsVariabels.hideSystemUI(getWindow());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantsVariabels.hideSystemUI(getWindow());
    }

}
