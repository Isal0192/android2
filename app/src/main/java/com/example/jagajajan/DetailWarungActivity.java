package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class DetailWarungActivity extends AppCompatActivity {

    // TextViews for displaying warung details
    TextView namaWarung, alamat, no_hp, email, jam_buka, jam_tutup, tvAlamat, tvJamOprasi, jenisWarung;
    ImageButton btnChat;
    ImageView btnBack, arrow;
    LinearLayout detailInfo, detailPeroduk, formPengajuan;
    Button pengajuan;
    EditText produkSaya, deskripsiPerodukSaya, kategoriPerodukSaya;
    private String currentIdPemilik;
    private String currentNamaWarung; // Mengganti nama agar lebih jelas
    private RequestQueue requestQueue;

    // Constants for SharedPreferences keys
    private static final String PREFS_NAME = "WarungDetailPrefs";
    private static final String KEY_ID_PEMILIK = "id_pemilik";
    private static final String KEY_NAMA_WARUNG = "nama_warung";
    private static final String KEY_JENIS_WARUNG = "jenis_warung";
    private static final String KEY_ALAMAT = "alamat";
    private static final String KEY_JAM_BUKA = "jam_buka";
    private static final String KEY_JAM_TUTUP = "jam_tutup";
    private static final String KEY_NO_HP = "no_hp";
    private static final String KEY_EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_warung);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize TextViews from layout
        namaWarung = findViewById(R.id.tv_nama_warung);
        alamat = findViewById(R.id.tv_alamat_warung);
        no_hp = findViewById(R.id.tv_no_hp);
        email = findViewById(R.id.tv_email);
        jam_buka = findViewById(R.id.tv_jam_buka);
        jam_tutup = findViewById(R.id.tv_jam_tutup);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvJamOprasi = findViewById(R.id.tv_jam_oprasi);
        jenisWarung = findViewById(R.id.jenis_warung);
        pengajuan = findViewById(R.id.btn_ajukan_penitipan);
        formPengajuan = findViewById(R.id.pengajuan);

        // Initialize Buttons and Layouts
        btnChat = findViewById(R.id.btn_chat);
        btnBack = findViewById(R.id.btn_back);
        detailInfo = findViewById(R.id.detail_informasi_warung);
        detailPeroduk = findViewById(R.id.card_produk2);
        arrow = findViewById(R.id.arrow);

        // Initialize EditTexts
        produkSaya = findViewById(R.id.produk_saya);
        deskripsiPerodukSaya = findViewById(R.id.deskripsi_produk_saya);
        kategoriPerodukSaya = findViewById(R.id.katagori_produk_saya);

        // Set OnClickListener for back button
        ViewUtils.spesialImgViewOnClick(btnBack, this, Home.class, PREFS_NAME);

        // Get SharedPreferences instance
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Retrieve data from Intent
        Intent intent = getIntent();
        String idPemilikFromIntent = intent.getStringExtra("id_pemilik");
        String namaWarungTemp = intent.getStringExtra("nama_warung"); // Gunakan variabel temp lokal
        String jenisWarungFromIntent = intent.getStringExtra("jenis_warung");
        String alamatFromIntent = intent.getStringExtra("alamat");
        String jamBukaFromIntent = intent.getStringExtra("jam_buka");
        String jamTutupFromIntent = intent.getStringExtra("jam_tutup");

        // Check if data is available from Intent
        if (idPemilikFromIntent != null) {
            // Data from Intent is primary, save it to SharedPreferences
            currentIdPemilik = idPemilikFromIntent;
            currentNamaWarung = namaWarungTemp; // Inisialisasi variabel anggota kelas
            editor.putString(KEY_ID_PEMILIK, idPemilikFromIntent);
            editor.putString(KEY_NAMA_WARUNG, namaWarungTemp);
            editor.putString(KEY_JENIS_WARUNG, jenisWarungFromIntent);
            editor.putString(KEY_ALAMAT, alamatFromIntent);
            editor.putString(KEY_JAM_BUKA, jamBukaFromIntent);
            editor.putString(KEY_JAM_TUTUP, jamTutupFromIntent);
            editor.apply();

            // Set TextViews with data from Intent
            namaWarung.setText(currentNamaWarung);
            jenisWarung.setText(jenisWarungFromIntent);
            alamat.setText("alamat: " + alamatFromIntent);
            tvAlamat.setText(alamatFromIntent);
            jam_buka.setText("Buka:   " + jamBukaFromIntent);
            jam_tutup.setText("Tutup:   " + jamTutupFromIntent);
            tvJamOprasi.setText(jamBukaFromIntent + " - " + jamTutupFromIntent);

        } else {
            // No data from Intent, try to load from SharedPreferences
            currentIdPemilik = prefs.getString(KEY_ID_PEMILIK, null);
            currentNamaWarung = prefs.getString(KEY_NAMA_WARUNG, ""); // Muat juga ke variabel anggota kelas

            if (currentIdPemilik != null) {
                // Load and set TextViews with data from SharedPreferences
                namaWarung.setText(currentNamaWarung);
                jenisWarung.setText(prefs.getString(KEY_JENIS_WARUNG, ""));
                alamat.setText("alamat: " + prefs.getString(KEY_ALAMAT, ""));
                tvAlamat.setText(prefs.getString(KEY_ALAMAT, ""));
                jam_buka.setText("Buka:   " + prefs.getString(KEY_JAM_BUKA, ""));
                jam_tutup.setText("Tutup:   " + prefs.getString(KEY_JAM_TUTUP, ""));
                tvJamOprasi.setText(prefs.getString(KEY_JAM_BUKA, "") + " - " + prefs.getString(KEY_JAM_TUTUP, ""));
                no_hp.setText("no hp:   " + prefs.getString(KEY_NO_HP, ""));
                email.setText("email:   " + prefs.getString(KEY_EMAIL, ""));

                Toast.makeText(this, "Data dimuat dari sesi sebelumnya.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Silakan kembali ke Beranda dan pilih warung.", Toast.LENGTH_LONG).show();
            }
        }

        // Jika idPemilik valid tersedia (dari Intent atau SharedPreferences), panggil data tambahan
        if (currentIdPemilik != null) {
            CallData(currentIdPemilik, editor); // editor ini akan digunakan untuk menyimpan no_hp dan email
            Bundle bundle = new Bundle();
            bundle.putString("id", currentIdPemilik);
            bundle.putString("nama", currentNamaWarung); // Gunakan variabel anggota kelas yang sudah terinisialisasi
            ViewUtils.setButton(btnChat, this, ChatActivity.class, bundle);
        } else {
            btnChat.setEnabled(false);
            btnChat.setAlpha(0.5f);
        }

        // Listeners for UI interaction
        detailInfo.setOnClickListener(view -> {
            if (detailPeroduk.getVisibility() == View.GONE) {
                detailPeroduk.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_arrow_up);
            } else {
                detailPeroduk.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_arrow_down);
            }
        });

        pengajuan.setOnClickListener(view -> {
            // Selalu tampilkan form pengajuan saat tombol "Ajukan Penitipan" diklik
            formPengajuan.setVisibility(View.VISIBLE);

            String produksaya = produkSaya.getText().toString().trim();
            String deskripsiproduksaya = deskripsiPerodukSaya.getText().toString().trim();
            String kategoriproduksaya = kategoriPerodukSaya.getText().toString().trim();

            if (produksaya.isEmpty() || deskripsiproduksaya.isEmpty() || kategoriproduksaya.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Harap isi semua data terlebih dahulu.", Toast.LENGTH_SHORT).show();
                return; // Berhenti di sini jika ada input yang kosong
            }

            // Simpan data produk ke SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editorProduct = sharedPreferences.edit();
            editorProduct.putString("id_pemilik", currentIdPemilik);
            editorProduct.putString("namaProduk", produksaya);
            editorProduct.putString("deskripsi", deskripsiproduksaya);
            editorProduct.putString("kategori", kategoriproduksaya);
            editorProduct.apply();

            // Navigasi ke ChatActivity dengan data produk
            Intent intentChat = new Intent(getApplicationContext(), ChatActivity.class);
            intentChat.putExtra("namaProduk", produksaya);
            intentChat.putExtra("deskripsi", deskripsiproduksaya);
            intentChat.putExtra("kategori", kategoriproduksaya);
            intentChat.putExtra("id", currentIdPemilik);
            intentChat.putExtra("nama", currentNamaWarung); // Gunakan variabel anggota kelas
            startActivity(intentChat);
        });
    }

    private void CallData(String id, SharedPreferences.Editor editor) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String noHpUser = data.getString("no_hp");
                        String emailUser = data.getString("email");

                        // Update TextViews
                        no_hp.setText("no hp:   " + noHpUser);
                        email.setText("email:   " + emailUser);

                        // Save fetched no_hp and email to SharedPreferences
                        editor.putString(KEY_NO_HP, noHpUser);
                        editor.putString(KEY_EMAIL, emailUser);
                        editor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Gagal mengurai data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Detail error yang lebih baik
                    String errorMessage = "Gagal mengambil data.";
                    if (error.networkResponse != null) {
                        errorMessage += " Kode: " + error.networkResponse.statusCode;
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            errorMessage += " Pesan: " + responseBody;
                        } catch (Exception e) {
                            // Ignored
                        }
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }
}