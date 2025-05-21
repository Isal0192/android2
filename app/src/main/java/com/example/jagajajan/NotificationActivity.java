package com.example.jagajajan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class NotificationActivity extends AppCompatActivity {

    private ImageView previouspagenf, messagenfn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        previouspagenf = ViewUtils.findViewById(this, R.id.previous_pagenfn);
        messagenfn = ViewUtils.findViewById(this, R.id.message_nfn);
    }
}