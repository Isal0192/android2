package com.example.jagajajan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GetStartedInfoActivity extends AppCompatActivity {
    Button Daftar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_info);
        Daftar = findViewById(R.id.daftar);

        Daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent KeDaftar = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(KeDaftar);
            }
        });
    }
}