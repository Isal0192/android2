

package com.example.jagajajan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PerofileActivity extends AppCompatActivity {

    private ImageView imgProfile, previousPage, tvFabEdit;
    private TextView tvName, tvUsername, tvPhone, tvEmail, tvAddress, tvTempatNtip, keluar;
    private CardView daftarWarung;

    private static final String URL_PROFILE = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER;
    private static final String PREF_PROFILE = "user_profile";
    private static final String PREF_USER = "user_pref";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perofile);

        // Init views
        imgProfile = findViewById(R.id.img_profile);
        tvName = findViewById(R.id.tv_name);
        tvUsername = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.tv_phone_value);
        tvEmail = findViewById(R.id.tv_email_value);
        tvAddress = findViewById(R.id.tv_address_value);
        tvTempatNtip = findViewById(R.id.tempat_nitip);
        tvFabEdit = findViewById(R.id.fab_edit);
        previousPage = findViewById(R.id.previous_page);
        keluar = findViewById(R.id.keluar);
        daftarWarung = findViewById(R.id.card_daftar_warung);

        ConstantsVariabels.hideSystemUI(getWindow());
        requestQueue = Volley.newRequestQueue(this);


        ViewUtils.setTextViewOnClickListener(tvTempatNtip, this, DetailWarungActivity.class);
        ViewUtils.setCardViewOnClickListener(daftarWarung, this, RegisterWarungActivity.class);
        ViewUtils.setImageViewOnClickListener(tvFabEdit, this, EditPerofileActivity.class);
        ViewUtils.setImageViewOnClickListener(previousPage, this, Home.class);

        keluar.setOnClickListener(v -> {
            getSharedPreferences(PREF_USER, MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(this, "Berhasil keluar.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PerofileActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfileFromSharedPreferences();
        loadProfileImageFromSharedPreferences();

        SharedPreferences userPref = getSharedPreferences(PREF_USER, MODE_PRIVATE);
        String id = userPref.getString("id", null);
        if (id !=    null && !id.isEmpty()) {
            getUserProfile(id);
        } else {
            Toast.makeText(this, "Token tidak ditemukan, silakan login kembali.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfileFromSharedPreferences() {
        SharedPreferences profilePref = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);

        tvName.setText(profilePref.getString("full_name", "Nama Lengkap"));
        tvUsername.setText(profilePref.getString("username", "Username"));
        tvPhone.setText(profilePref.getString("phone", "-"));
        tvEmail.setText(profilePref.getString("email", "-"));
        tvAddress.setText(profilePref.getString("address", "-"));
    }

    private void loadProfileImageFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);
        String base64Image = sharedPreferences.getString("profile_image", null);
        if (base64Image != null) {
            try {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                imgProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("ProfileImage", "Error decoding profile image", e);
            }
        }
    }
    private void setWarungVisibility(){
        SharedPreferences profilePref = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);
        String peran = profilePref.getString("peran", null);

        if ("pemilik_warung".equalsIgnoreCase(peran)) {
            daftarWarung.setVisibility(View.GONE);
        } else {
            daftarWarung.setVisibility(View.VISIBLE);
        }
    }

    //    mengambil perofile
    private void getUserProfile(final String id) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_PROFILE + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String fullName = data.getString("full_name");
                        String username = data.getString("username");
                        String phone = data.getString("no_hp");
                        String email = data.getString("email");
                        String address = data.getString("alamat");
                        String peran = data.getString("peran");

                        tvName.setText(fullName);
                        tvUsername.setText(username);
                        tvPhone.setText(phone);
                        tvEmail.setText(email);
                        tvAddress.setText(address);

                        saveProfileToSharedPreferences(fullName, username, phone, email, address, peran);
                        setWarungVisibility();
                    } catch (JSONException e) {
                        Log.e("UserProfile", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "Gagal memproses data profil.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("UserProfileError", "Volley error: " + error.getMessage());
                    String errorMessage = "Gagal mengambil data profil.";
                    if (error instanceof TimeoutError) errorMessage = "Waktu koneksi habis. Silakan coba lagi.";
                    else if (error instanceof NoConnectionError) errorMessage = "Tidak ada koneksi internet.";
                    else if (error instanceof AuthFailureError) errorMessage = "Otentikasi gagal. Silakan login kembali.";
                    else if (error instanceof ServerError) errorMessage = "Terjadi kesalahan di server.";
                    else if (error instanceof NetworkError) errorMessage = "Kesalahan jaringan.";
                    else if (error instanceof ParseError) errorMessage = "Gagal memproses respons dari server.";

                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        jsonObjectRequest.setTag("profileRequest");
        requestQueue.add(jsonObjectRequest);
    }

    private void saveProfileToSharedPreferences(String fullName, String username, String phone, String email, String address, String peran) {
        SharedPreferences profilePref = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = profilePref.edit();

        editor.putString("full_name", fullName);
        editor.putString("username", username);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.putString("peran", peran);

        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("profileRequest");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}








