package com.example.jagajajan;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;

public class ConstantsVariabels {
    public static final String BASE_URL = "http://192.168.129.180:3000/api/";
    public static final String ENDPOINT_LOGIN = "auth/login";
    public static final String ENDPPOINT_REGISTER = "auth/register";
    public static final String ENDPOINT_USER = "user/";
    public static final String ENDPOINT_WARUNG = "warung/register";

    public static void hideSystemUI(Window window) {
        if (window != null) {
            new Handler().postDelayed(() -> {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }, 0);
        }
    }
}