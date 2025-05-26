package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        Intent intent = getIntent();
        String penitip_id = sharedPreferences.getString("id", null);
        String pemilik_warung_id = intent.getStringExtra("pemilik_warung_id");

    }
}