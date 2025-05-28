package com.example.jagajajan;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.Window;

public class ConstantsVariabels {
    public static final String BASE_URL = "http://192.168.50.235:3000/api/v1";

    //    /auth
    public static final String ENDPOINT_LOGIN = "/auth/login";
    public static final String ENDPPOINT_REGISTER = "/auth/register";
    //    auth/user
    public static final String ENDPOINT_USER = "/auth/user/";//GET/:id PUT:id

    //    /warung
    public static final String ENPOINT_WARUNG = "/warung";//GET. pake header id (PUT/:id)
    public static final String ENDPOINT_WARUNG_REGISTER= "/warung/register";//POST
    //    /pesan
    public static final String ENPOINT_PESAN = "/pesan";
    public static final String ENPOINT_NOTIVICATION = "/notifikasi";

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
    public static final String APP_PREF_NAME = "user_pref";
    public static final String USER_PROFILE_PREF = "user_profile";

    public static final String KEY_USER_ID = "id";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PROFILE_IMAGE = "profile_image";
}