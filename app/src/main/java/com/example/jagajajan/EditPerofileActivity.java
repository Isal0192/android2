
package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditPerofileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile, previousPage;
    private EditText etFullName, etUsername, etPhone, etEmail, etAddress;
    private Button btnSave;
    private Uri imageUriEP;
    private String base64ImageEP;
    private CardView daftarWarung;

    String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER;
    private static final String PREF_NAME = "user_pref";
    private static final String USER_PROFILE_PREF = "user_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perofile);

        // Inisialisasi view
        imgProfile = findViewById(R.id.img_profile);
        previousPage = findViewById(R.id.previous_page);
        etFullName = findViewById(R.id.et_full_name);
        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
        daftarWarung = ViewUtils.findViewById(this, R.id.card_daftar_warung);

        ConstantsVariabels.hideSystemUI(getWindow());
        setInitialData();

        // Pilih gambar
        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        });

        // Simpan profil
        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();
            String username = etUsername.getText().toString();
            String phone = etPhone.getText().toString();
            String email = etEmail.getText().toString();
            String address = etAddress.getText().toString();

            // Simpan ke SharedPreferences
            saveProfileToSharedPreferences(fullName, username, phone, email, address, base64ImageEP);

            // Kirim ke server
            updateProfileToServer(fullName, username, phone, email, address, () -> {
                Toast.makeText(this, "Data berhasil disimpan dan diupdate", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, PerofileActivity.class));
                finish();
            });
        });

        // Navigasi
        previousPage.setOnClickListener(v -> startActivity(new Intent(this, PerofileActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriEP = data.getData();
            imgProfile.setImageURI(imageUriEP);
            try {
                base64ImageEP = uriToBase64(imageUriEP);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal mengonversi gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setInitialData() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PROFILE_PREF, MODE_PRIVATE);
        etFullName.setText(sharedPreferences.getString("full_name", ""));
        etUsername.setText(sharedPreferences.getString("username", ""));
        etPhone.setText(sharedPreferences.getString("phone", ""));
        etEmail.setText(sharedPreferences.getString("email", ""));
        etAddress.setText(sharedPreferences.getString("address", ""));

        String base64Image = sharedPreferences.getString("profile_image", null);
        if (base64Image != null) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imgProfile.setImageBitmap(bitmap);
        } else {
            imgProfile.setImageResource(R.drawable.ic_user); // gambar default
        }
    }

    private void saveProfileToSharedPreferences(String fullName, String username, String phone, String email, String address, String base64Image) {
        SharedPreferences.Editor editor = getSharedPreferences(USER_PROFILE_PREF, MODE_PRIVATE).edit();
        editor.putString("full_name", fullName);
        editor.putString("username", username);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("address", address);
        if (base64Image != null) {
            editor.putString("profile_image", base64Image);
        }
        editor.apply();
    }

    private void updateProfileToServer(String fullName, String username, String phone, String email, String address, Runnable onSuccess) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String id = sharedPreferences.getString("id", null);

        if (id == null) {
            Toast.makeText(this, "ID user tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("full_name", fullName);
            jsonBody.put("username", username);
            jsonBody.put("no_hp", phone);
            jsonBody.put("email", email);
            jsonBody.put("alamat", address);
            // Jika ingin kirim base64ImageEP ke server, bisa tambahkan:
            // jsonBody.put("image", base64ImageEP);

            String putUrl = url + "/" + id;

            JsonObjectRequest putRequest = new JsonObjectRequest(
                    Request.Method.PUT,
                    putUrl,
                    jsonBody,
                    response -> {
                        if (onSuccess != null) onSuccess.run();
                    },
                    error -> {
                        String errMsg = "Gagal update ke server";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            errMsg += ": " + new String(error.networkResponse.data);
                        } else if (error.getMessage() != null) {
                            errMsg += ": " + error.getMessage();
                        }
                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(putRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String uriToBase64(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
