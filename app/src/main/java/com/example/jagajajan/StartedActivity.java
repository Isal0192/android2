package com.example.jagajajan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class StartedActivity extends AppCompatActivity {
    Button btnContinue, skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started);
        btnContinue = findViewById(R.id.btn_continue);
        skip = findViewById(R.id.skip);

        skip.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });


        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }
}