package com.example.jagajajan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class DetailWarungActivity extends AppCompatActivity {

    private ImageView previousPagews, notifiWs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_warung);

        // Inisialisasi View
        previousPagews = ViewUtils.findViewById(this,R.id.previous_pagews);
        notifiWs = ViewUtils.findViewById(this,R.id.notificationws);

        // Set OnClickListener menggunakan ViewUtils
        ViewUtils.setImageViewOnClickListener(previousPagews, this, PerofileActivity.class);
        ViewUtils.setImageViewOnClickListener(notifiWs, this, NotificationActivity.class);
    }
}
