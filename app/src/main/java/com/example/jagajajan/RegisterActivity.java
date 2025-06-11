package com.example.jagajajan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log; // <-- Tambahkan ini
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity"; // <-- Tambahkan tag log

    Button submit;
    TextView login;
    EditText full_name, username, number, email, password, konfirm_pass;
    CheckBox agree;
    RequestQueue queue;

    String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPPOINT_REGISTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "onCreate: Memulai RegisterActivity");

        // Inisialisasi semua komponen
        submit = findViewById(R.id.submit);
        login = findViewById(R.id.login);

        full_name = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        number = findViewById(R.id.number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        konfirm_pass = findViewById(R.id.konfirm_password);
        agree = findViewById(R.id.accpt_privasy_policy);

        // Inisialisasi Volley queue
        queue = Volley.newRequestQueue(this);

        // Ketika tombol submit diklik
        submit.setOnClickListener(v -> {
            Log.d(TAG, "Submit button clicked");
            if (validateInputs()) {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("nama_lengkap", full_name.getText().toString());
                    jsonBody.put("username", username.getText().toString());
                    jsonBody.put("no_hp", number.getText().toString());
                    jsonBody.put("email", email.getText().toString());
                    jsonBody.put("password", password.getText().toString());

                    Log.d(TAG, "JSON Body: " + jsonBody.toString());

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonBody,
                            response -> {
                                Log.d(TAG, "Response: " + response.toString());
                                Toast.makeText(getApplicationContext(), "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            },
                            error -> {
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    Log.e(TAG, "Error Response Code: " + statusCode);
                                    Toast.makeText(getApplicationContext(), "Error " + statusCode, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e(TAG, "Error: " + error.getMessage(), error);
                                    Toast.makeText(getApplicationContext(), "Gagal: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                    queue.add(request);

                } catch (JSONException e) {
                    Log.e(TAG, "JSONException: ", e);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan JSON", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ketika teks login diklik
        ViewUtils.setTextViewOnClickListener((TextView) findViewById(R.id.login), this, LoginActivity.class);
    }

    // Validasi input
    private boolean validateInputs() {
        Log.d(TAG, "Memulai validasi input");
        if (full_name.getText().toString().trim().isEmpty()) {
            showToast("Nama lengkap harus diisi");
            return false;
        }
        if (username.getText().toString().trim().isEmpty()) {
            showToast("Username harus diisi");
            return false;
        }
        if (number.getText().toString().trim().isEmpty()) {
            showToast("Nomor HP harus diisi");
            return false;
        }
        if (email.getText().toString().trim().isEmpty()) {
            showToast("Email harus diisi");
            return false;
        }
        if (password.getText().toString().trim().isEmpty()) {
            showToast("Password harus diisi");
            return false;
        }
        if (!password.getText().toString().equals(konfirm_pass.getText().toString())) {
            showToast("Konfirmasi password tidak sesuai");
            return false;
        }
        if (!agree.isChecked()) {
            showToast("Anda harus menyetujui kebijakan privasi");
            return false;
        }
        Log.d(TAG, "Input valid");
        return true;
    }

    private void showToast(String message) {
        Log.d(TAG, "Show toast: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
