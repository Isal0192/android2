// Package utama aplikasi
package com.example.jagajajan;

// Import library Android yang dibutuhkan
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.CardView;
import android.view.View; // Import View
// Import library Volley untuk networking
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

// Import JSON handling
import org.json.JSONException;
import org.json.JSONObject;

//import api
import com.example.jagajajan.ConstantsVariabels;
import com.example.jagajajan.ViewUtils;

// Kelas utama untuk halaman profil
public class PerofileActivity extends AppCompatActivity {

    // Deklarasi komponen UI
    private ImageView imgProfile, previousPage, fabEdit;
    private TextView tvName, tvUsername, tvPhone, tvEmail, tvAddress, tempatNitip, ubahSndi, pengaturan, keluar;
    private CardView daftarWarung;

    // Konstanta URL dan nama SharedPreferences
    private static final String URL_PROFILE = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER;
    private static final String PREF_NAME = "user_pref";

    // Volley RequestQueue
    private RequestQueue requestQueue;

    // Fungsi yang dipanggil saat activity dibuat
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perofile);

        // Inisialisasi komponen UI
        imgProfile = findViewById(R.id.img_profile);
        tvName = findViewById(R.id.tv_name);
        tvUsername = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.tv_phone_value);
        tvEmail = findViewById(R.id.tv_email_value);
        tvAddress = findViewById(R.id.tv_address_value);
        previousPage = findViewById(R.id.previous_page);
        keluar = findViewById(R.id.keluar);


        ConstantsVariabels.hideSystemUI(getWindow());

        // Inisialisasi request queue Volley
        requestQueue = Volley.newRequestQueue(this);

        // Ambil data dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        String id = sharedPreferences.getString("id", null);

        // Tampilkan nama depan jika ada
        if (name != null && !name.isEmpty()) {
            String firstName = name.split(" ")[0];
            tvName.setText(firstName.length() >= 10 ? "fulan" : firstName);
        }

        // Ambil data user dari API jika ID tersedia
        if (id != null && !id.isEmpty()) {
            getUserProfile(id);
        } else {
            Toast.makeText(this, "Token tidak ditemukan, silakan login kembali.", Toast.LENGTH_SHORT).show();
        }


//        INTEN INTEN
        ViewUtils.setCardViewOnClickListener((CardView) findViewById(R.id.card_daftar_warung), this, RegisterWarungActivity.class);
        ViewUtils.setImageViewOnClickListener((ImageView) findViewById(R.id.fab_edit), this, EditPerofileActivity.class);
        ViewUtils.setImageViewOnClickListener((ImageView) findViewById(R.id.previous_page), this, Home.class);

        // Tombol keluar (logout)
        keluar.setOnClickListener(v -> {
            // Hapus SharedPreferences user_pref
            SharedPreferences userPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPref.edit();
            userEditor.clear();
            userEditor.apply();

            // Hapus SharedPreferences user_profile
            SharedPreferences userProfile = getSharedPreferences("user_profile", MODE_PRIVATE);
            SharedPreferences.Editor profileEditor = userProfile.edit();
            profileEditor.clear();
            profileEditor.apply();

            // Tampilkan notifikasi berhasil logout
            Toast.makeText(PerofileActivity.this, "Berhasil keluar.", Toast.LENGTH_SHORT).show();

            // Arahkan ke halaman login
            Intent intent = new Intent(PerofileActivity.this, LoginActivity.class); // Ganti sesuai nama class login kamu
            startActivity(intent);
            finishAffinity(); // Tutup semua activity sebelumnya
        });
    }

    // Fungsi untuk mengambil profil pengguna dari server
    private void getUserProfile(final String id) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_PROFILE + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            String fullName = data.getString("full_name");
                            String username = data.getString("username");
                            String phone = data.getString("no_hp");
                            String email = data.getString("email");
                            String address = data.getString("alamat");

                            tvUsername.setText(username);
                            tvPhone.setText(phone);
                            tvEmail.setText(email);
                            tvAddress.setText(address);
                            tvName.setText(fullName);

                            saveProfileToSharedPreferences(fullName, username, phone, email, address);
                        } catch (JSONException e) {
                            Log.e("UserProfile", "Error parsing JSON: " + e.getMessage(), e);
                            Toast.makeText(PerofileActivity.this, "Gagal memproses data profil.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("UserProfileError", "Error fetching user profile: " + error.getMessage());
                        String errorMessage = "Gagal mengambil data profil.";

                        // Menangani berbagai jenis error dari Volley
                        if (error instanceof TimeoutError) {
                            errorMessage = "Waktu koneksi habis. Silakan coba lagi.";
                        } else if (error instanceof NoConnectionError) {
                            errorMessage = "Tidak ada koneksi internet.";
                        } else if (error instanceof AuthFailureError) {
                            errorMessage = "Otentikasi gagal. Silakan login kembali.";
                        } else if (error instanceof ServerError) {
                            errorMessage = "Terjadi kesalahan di server.";
                        } else if (error instanceof NetworkError) {
                            errorMessage = "Kesalahan jaringan.";
                        } else if (error instanceof ParseError) {
                            errorMessage = "Gagal memproses respons dari server.";
                        }

                        Toast.makeText(PerofileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        jsonObjectRequest.setTag("profileRequest");
        requestQueue.add(jsonObjectRequest);
    }

    // Simpan data profil ke SharedPreferences user_profile
    private void saveProfileToSharedPreferences(String fullName, String username, String phone, String email, String address) {
        SharedPreferences perofile = getSharedPreferences("user_profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = perofile.edit();

        editor.putString("full_name", fullName);
        editor.putString("username", username);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("address", address);

        editor.apply();
    }

    // Batalkan semua request saat activity dihentikan
    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll("profileRequest");
        }
    }

    // Periksa apakah koneksi internet tersedia
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

