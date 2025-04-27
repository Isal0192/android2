package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.ConstantsVariabels;

import org.json.JSONException;
import org.json.JSONObject;


public class EditPerofileActivity extends AppCompatActivity {

    private ImageView imgProfile, previousPage;
    private EditText etFullName, etUsername, etPhone, etEmail, etAddress;
    private Button btnSave;
    private CardView daftarWarung;

    String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER;
    private static final String PREF_NAME = "user_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perofile);

        // Inisialisasi view menggunakan ViewUtils
        imgProfile = ViewUtils.findViewById(this, R.id.img_profile);
        previousPage = ViewUtils.findViewById(this, R.id.previous_page);
        etFullName = ViewUtils.findViewById(this, R.id.et_full_name);
        etUsername = ViewUtils.findViewById(this, R.id.et_username);
        etPhone = ViewUtils.findViewById(this, R.id.et_phone);
        etEmail = ViewUtils.findViewById(this, R.id.et_email);
        etAddress = ViewUtils.findViewById(this, R.id.et_address);
        btnSave = ViewUtils.findViewById(this, R.id.btn_save);
        daftarWarung = ViewUtils.findViewById(this, R.id.card_daftar_warung);


        ConstantsVariabels.hideSystemUI(getWindow());
        setInitialData();

        btnSave.setOnClickListener(v -> {
            String fullName = ViewUtils.getText(etFullName);
            String username = ViewUtils.getText(etUsername);
            String phone = ViewUtils.getText(etPhone);
            String email = ViewUtils.getText(etEmail);
            String address = ViewUtils.getText(etAddress);

            saveProfileToSharedPreferences(fullName, username, phone, email, address);
            updateProfileToServer(fullName, username, phone, email, address);

        });

        // Gunakan ViewUtils untuk Intent
        ViewUtils.setCardViewOnClickListener(daftarWarung, this, RegisterWarungActivity.class);
        ViewUtils.setImageViewOnClickListener(previousPage, this, PerofileActivity.class);
    }

    private void updateProfileToServer(String fullName, String username, String phone, String email, String address) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("full_name", fullName);
            jsonBody.put("username", username);
            jsonBody.put("no_hp", phone);
            jsonBody.put("email", email);
            jsonBody.put("alamat", address);


            String putUrl = url + id;

            JsonObjectRequest putRequest = new JsonObjectRequest(

                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> Toast.makeText(EditPerofileActivity.this, "Data berhasil diupdate ke server", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(EditPerofileActivity.this, "Gagal update ke server: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(putRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveProfileToSharedPreferences(String fullName, String username, String phone, String email, String address) {
        SharedPreferences.Editor editor = getSharedPreferences("user_profile", MODE_PRIVATE).edit();
        editor.putString("full_name", fullName);
        editor.putString("username", username);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.apply();
    }


    private void setInitialData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", MODE_PRIVATE);

        String fullName = sharedPreferences.getString("full_name", null);
        String username = sharedPreferences.getString("username", null);
        String phone = sharedPreferences.getString("phone", null);
        String email = sharedPreferences.getString("email", null);
        String address = sharedPreferences.getString("address", null);

        etFullName.setText(fullName);
        etUsername.setText(username);
        etPhone.setText(phone);
        etEmail.setText(email);
        etAddress.setText(address);

        imgProfile.setImageResource(R.drawable.ic_user);
    }
}
