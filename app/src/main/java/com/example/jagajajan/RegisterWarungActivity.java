package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RegisterWarungActivity extends AppCompatActivity {

    private ImageView previousPage, imgWarung, cameraIcon;
    private EditText etNamaWarung, etAlamatWarung, etNoTeleponWarung, etKategoriWarung, etDeskripsiWarung, etJamBuka, etJamTutup;
    private Button btnSimpanWarung;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private RequestQueue requestQueue;
    private static final String URL_DAFTAR_WARUNG = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_WARUNG_REGISTER;
    private static final String PREF_NAME = "user_pref";
    private static final String TAG = "RegisterWarungActivity";
    private String base64Image;
    private String fileName = null; // Tambahkan variabel untuk nama file gambar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_warung);
        // Inisialisasi View menggunakan ViewUtils
        previousPage = ViewUtils.findViewById(this, R.id.previous_page);
        imgWarung = ViewUtils.findViewById(this, R.id.img_warung);
        cameraIcon = ViewUtils.findViewById(this, R.id.camera_icon);
        etNamaWarung = ViewUtils.findViewById(this, R.id.et_nama_warung);
        etAlamatWarung = ViewUtils.findViewById(this, R.id.et_alamat_warung);
        etNoTeleponWarung = ViewUtils.findViewById(this, R.id.et_no_telepon_warung);
        etKategoriWarung = ViewUtils.findViewById(this, R.id.et_kategori_warung);
        etDeskripsiWarung = ViewUtils.findViewById(this, R.id.et_deskripsi_warung);
        etJamBuka = ViewUtils.findViewById(this, R.id.et_jam_buka);
        etJamTutup = ViewUtils.findViewById(this, R.id.et_jam_tutup);
        btnSimpanWarung = ViewUtils.findViewById(this, R.id.btn_simpan_warung);

        requestQueue = Volley.newRequestQueue(this);
        ConstantsVariabels.hideSystemUI(getWindow());

        // Set OnClickListener untuk tombol kembali
        ViewUtils.setImageViewOnClickListener((ImageView) findViewById(R.id.previous_page), this, Home.class);

        // Set OnClickListener untuk memilih gambar
        cameraIcon.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        });

        // Set OnClickListener untuk tombol simpan
        btnSimpanWarung.setOnClickListener(v -> handleSimpanWarung());
    }

    // Metode untuk menangani hasil pemilihan gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgWarung.setImageURI(imageUri);
            // Konversi gambar ke Base64
            try {
                base64Image = uriToBase64(imageUri);
                // Mendapatkan nama file sederhana dari URI
                fileName = getFileNameFromUri(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal mengonversi gambar", Toast.LENGTH_SHORT).show();
                base64Image = null;
                fileName = null;
            }
        }
    }

    private void handleSimpanWarung() {
        String namaWarung = ViewUtils.getText(etNamaWarung);
        String alamatWarung = ViewUtils.getText(etAlamatWarung);
        String noTeleponWarung = ViewUtils.getText(etNoTeleponWarung);
        String kategoriWarung = ViewUtils.getText(etKategoriWarung);
        String deskripsiWarung = ViewUtils.getText(etDeskripsiWarung);
        String jamBuka = ViewUtils.getText(etJamBuka);
        String jamTutup = ViewUtils.getText(etJamTutup);

        if (validateInput(namaWarung, alamatWarung, noTeleponWarung, kategoriWarung, deskripsiWarung, jamBuka, jamTutup)) {
            // Mengirim `fileName` alih-alih `base64Image` ke backend untuk `foto_warung`
            daftarWarung(namaWarung, alamatWarung, noTeleponWarung, kategoriWarung, deskripsiWarung, jamBuka, jamTutup, fileName);
        }
    }

    private boolean validateInput(String namaWarung, String alamatWarung, String noTeleponWarung, String kategoriWarung, String deskripsiWarung, String jamBuka, String jamTutup) {
        if (namaWarung.isEmpty()) {
            etNamaWarung.setError("Nama Warung harus diisi");
            return false;
        }
        if (alamatWarung.isEmpty()) {
            etAlamatWarung.setError("Alamat Warung harus diisi");
            return false;
        }
        if (noTeleponWarung.isEmpty()) {
            etNoTeleponWarung.setError("Nomor Telepon harus diisi");
            return false;
        }
        if (kategoriWarung.isEmpty()) {
            etKategoriWarung.setError("Kategori Warung harus diisi");
            return false;
        }
        if (deskripsiWarung.isEmpty()) {
            etDeskripsiWarung.setError("Deskripsi Warung harus diisi");
            return false;
        }
        if (jamBuka.isEmpty()) {
            etJamBuka.setError("Jam Buka harus diisi");
            return false;
        }
        if (jamTutup.isEmpty()) {
            etJamTutup.setError("Jam Tutup harus diisi");
            return false;
        }
        return true;
    }

    // Metode yang diubah untuk mengirim data sesuai format yang diinginkan
    private void daftarWarung(String namaWarung, String alamatWarung, String noTeleponWarung, String kategoriWarung, String deskripsiWarung, String jamBuka, String jamTutup, String fotoWarungFileName) {
        // Ambil ID user dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id", null);

        // Buat objek JSON untuk data yang akan dikirim
        JSONObject jsonParams = new JSONObject();
        try {
            // **PERUBAHAN DI SINI:**
            jsonParams.put("id_pemilik", idUser); // Sesuai dengan "id_pemilik"
            jsonParams.put("nama_warung", namaWarung);
            jsonParams.put("jenis_warung", kategoriWarung);
            jsonParams.put("no_bisnis", String.valueOf(noTeleponWarung)); // Sesuai dengan "no_bisnis"
            jsonParams.put("alamat", alamatWarung);
            jsonParams.put("jam_buka", jamBuka); // Hanya jam (misal: "08:00")
            jsonParams.put("jam_tutup", jamTutup); // Hanya jam (misal: "17:00")
            // Mengirim nama file gambar. Jika tidak ada gambar, `fileName` akan null.
            jsonParams.put("foto_warung", fotoWarungFileName);
            // `email_bisnis` tidak ada di request yang diinginkan, jadi dihapus.
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterWarungActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DAFTAR_WARUNG, jsonParams,
                response -> {
                    Toast.makeText(RegisterWarungActivity.this, "Warung berhasil didaftarkan", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterWarungActivity.this, Home.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(RegisterWarungActivity.this, "Gagal mendaftarkan warung: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error: " + error.getMessage());
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Metode untuk mengonversi URI ke Base64 (tetap ada jika backend butuh Base64 di masa depan)
    private String uriToBase64(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Could not open InputStream");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    // Metode baru untuk mendapatkan nama file dari URI
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            // Gunakan ContentResolver untuk mendapatkan nama file dari Content URI
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            // Jika tidak bisa mendapatkan dari ContentResolver, coba dari path
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}