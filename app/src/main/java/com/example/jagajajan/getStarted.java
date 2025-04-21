package com.example.jagajajan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class getStarted extends AppCompatActivity {
    Button btnContinue, sigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        btnContinue = findViewById(R.id.btn_continue);
        sigin = findViewById(R.id.sigin);

        sigin.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), signin_page.class);
            startActivity(intent);
        });


        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }
}