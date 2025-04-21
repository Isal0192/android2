package com.example.jagajajan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class login_page extends AppCompatActivity {
    Button btnSubmit;
    TextView singin, forgetPassword;
    EditText username, password;
    ImageView eyeIcon;
    boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Inisialisasi komponen
        eyeIcon = findViewById(R.id.eyeIcon);
        btnSubmit = findViewById(R.id.submit);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        singin = findViewById(R.id.sigin);
        forgetPassword = findViewById(R.id.forget_password);

//   password unvisibel default
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        isPasswordVisible = false;

//password visibel
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

        // Aksi tombol "Submit"
        btnSubmit.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // Logika login bisa ditaruh di sini (sementara tampilkan pesan)
                Toast.makeText(this, "Login berhasil sebagai: " + user, Toast.LENGTH_SHORT).show();

                // Contoh: pindah ke halaman utama
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); // menutup login page
            }
        });

        // Aksi klik "Sign In"
        singin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), signin_page.class);
            startActivity(intent);
        });

        // Aksi klik "Forget Password"
        forgetPassword.setOnClickListener(v -> Toast.makeText(this, "Fitur lupa password belum tersedia", Toast.LENGTH_SHORT).show());
    }
}
