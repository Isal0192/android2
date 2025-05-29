package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONException;
import org.json.JSONObject;

//import variabel

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //Deklarasi variabel UI
    Button btnSubmit;
    TextView singin;
    EditText username, password;
    ImageView eyeIcon;
    boolean isPasswordVisible;

    //URL login dan inisialisasi request queue
    String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_LOGIN;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi semua komponen UI
        eyeIcon = findViewById(R.id.eyeIcon);
        btnSubmit = findViewById(R.id.submit);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        singin = findViewById(R.id.sigin);

        // Inisialisasi Volley request queue
        queue = Volley.newRequestQueue(this);

        // Default: password tersembunyi
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        isPasswordVisible = false;

        // Toggle visibilitas password ketika ikon diklik
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye_close);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeIcon.setImageResource(R.drawable.ic_eye_open);
            }
            password.setSelection(password.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // Submit login
        btnSubmit.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Siapkan body JSON
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("username", user);
                    jsonBody.put("password", pass);

                    // Buat request login
                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonBody,
                            response -> {
                                Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_SHORT).show();

                                try {
                                    // 🔐 Ambil data dari respons
                                    String id = response.getString("id");
                                    String nama = response.getString("name");

                                    // 💾 Simpan ke SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putString("id", id);
                                    editor.putString("name", nama);
                                    editor.apply();

                                    // 👉 Arahkan ke halaman Home
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Gagal memproses respons login", Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    Toast.makeText(getApplicationContext(), "Login gagal. Error " + statusCode, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Gagal koneksi: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    ) {
                        // Tambahkan header ke request
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };

                    // Tambahkan ke antrean request
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Kesalahan JSON", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Klik "Belum punya akun? Daftar"
        singin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }
}
