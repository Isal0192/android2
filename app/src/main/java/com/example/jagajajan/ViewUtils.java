package com.example.jagajajan; // Menggunakan package utils yang lebih sesuai

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.CardView;

public class ViewUtils {

    private static final String TAG = "ViewUtils"; // TAG untuk logging

    /**
     * Digunakan untuk mendapatkan referensi ke View dari layout.
     *
     * @param activity    Activity yang menggunakan ViewUtils.
     * @param viewId      ID dari View yang ingin ditemukan.
     * @param <T>         Jenis View yang diharapkan (misalnya, Button, TextView).
     * @return View yang ditemukan, atau null jika tidak ditemukan.
     */
    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
    public static <T extends View> T findViewById(Activity activity, int viewId) {
        View view = activity.findViewById(viewId);
        if (view == null) {
            Log.e(TAG, "View with ID " + viewId + " not found in " + activity.getClass().getSimpleName());
            return null;
        }
        return (T) view;
    }

    /**
     * Digunakan untuk mendapatkan referensi ke View dari layout.
     *
     * @param view    View yang menjadi root.
     * @param viewId      ID dari View yang ingin ditemukan.
     * @param <T>         Jenis View yang diharapkan (misalnya, Button, TextView).
     * @return View yang ditemukan, atau null jika tidak ditemukan.
     */
    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
    public static <T extends View> T findViewById(View view, int viewId) {
        View childView = view.findViewById(viewId);
        if (childView == null) {
            Log.e(TAG, "View with ID " + viewId + " not found in the provided view");
            return null;
        }
        return (T) childView;
    }

    /**
     * Metode untuk mengatur onClickListener untuk Button.
     *
     * @param button          Button yang akan diatur listenernya.
     * @param context         Context yang digunakan untuk membuat Intent.
     * @param destinationActivity Class dari Activity tujuan.
     */
    public static void setButtonOnClickListener(Button button, Context context, Class<?> destinationActivity) {
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, destinationActivity);
                    context.startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "Button is null");
        }
    }

    /**
     * Metode untuk mengatur onClickListener untuk ImageView.
     *
     * @param imageView       ImageView yang akan diatur listenernya.
     * @param context         Context yang digunakan untuk membuat Intent.
     * @param destinationActivity Class dari Activity tujuan.
     */
    public static void setImageViewOnClickListener(ImageView imageView, Context context, Class<?> destinationActivity) {
        if (imageView != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, destinationActivity);
                    context.startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "ImageView is null");
        }
    }

    /**
     * Metode untuk mengatur onClickListener untuk TextView.
     *
     * @param textView          TextView yang akan diatur listenernya.
     * @param context         Context yang digunakan untuk membuat Intent.
     * @param destinationActivity Class dari Activity tujuan.
     */
    public static void setTextViewOnClickListener(TextView textView, Context context, Class<?> destinationActivity) {
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, destinationActivity);
                    context.startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "TextView is null");
        }
    }

    /**
     * Metode untuk mengatur onClickListener untuk CardView.
     *
     * @param cardView          CardView yang akan diatur listenernya.
     * @param context         Context yang digunakan untuk membuat Intent.
     * @param destinationActivity Class dari Activity tujuan.
     */
    public static void setCardViewOnClickListener(CardView cardView, Context context, Class<?> destinationActivity) {
        if (cardView != null) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, destinationActivity);
                    context.startActivity(intent);
                }
            });
        } else {
            Log.e(TAG, "CardView is null");
        }
    }

    /**
     * Metode untuk mendapatkan teks dari EditText.
     *
     * @param editText EditText yang akan diambil teksnya.
     * @return Teks dari EditText, atau string kosong jika EditText null.
     */
    public static String getText(EditText editText) {
        if (editText != null) {
            return editText.getText().toString().trim();
        } else {
            Log.e(TAG, "EditText is null");
            return "";
        }
    }

    /**
     * Metode untuk mengatur teks pada TextView.
     *
     * @param textView TextView yang akan diatur teksnya.
     * @param text     Teks yang akan diatur.
     */
    public static void setText(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text);
        } else {
            Log.e(TAG, "TextView is null");
        }
    }
}
