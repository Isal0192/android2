package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity; // Kembali menggunakan support library
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
    private TextView namaWarung, alamat, no_hp, email, jam_buka, jam_tutup, tvAlamat, tvJamOprasi, jenisWarung;
    private ImageButton btnChat;
    private ImageView btnBack, arrow;
    private LinearLayout detailInfoLayout, cardProduk2Layout, formPengajuanLayout;
    private Button btnAjukanPenitipan;
    private EditText produkSaya, deskripsiProdukSaya, kategoriProdukSaya;
    private String currentIdPemilik;
    private String currentNamaWarung;
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

        // Inisialisasi Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Inisialisasi Views dari layout
        namaWarung = findViewById(R.id.tv_nama_warung);
        alamat = findViewById(R.id.tv_alamat_warung);
        no_hp = findViewById(R.id.tv_no_hp);
        email = findViewById(R.id.tv_email);
        jam_buka = findViewById(R.id.tv_jam_buka);
        jam_tutup = findViewById(R.id.tv_jam_tutup);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvJamOprasi = findViewById(R.id.tv_jam_oprasi);
        jenisWarung = findViewById(R.id.jenis_warung);
        btnAjukanPenitipan = findViewById(R.id.btn_ajukan_penitipan);
        formPengajuanLayout = findViewById(R.id.pengajuan);

        btnChat = findViewById(R.id.btn_chat);
        btnBack = findViewById(R.id.btn_back);
        detailInfoLayout = findViewById(R.id.detail_informasi_warung);
        cardProduk2Layout = findViewById(R.id.card_produk2);
        arrow = findViewById(R.id.arrow);

        produkSaya = findViewById(R.id.produk_saya);
        deskripsiProdukSaya = findViewById(R.id.deskripsi_produk_saya);
        kategoriProdukSaya = findViewById(R.id.katagori_produk_saya);

        // Set OnClickListener untuk tombol kembali
        ViewUtils.spesialImgViewOnClick(btnBack, this, Home.class, PREFS_NAME);

        // Ambil instance SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Ambil data dari Intent
        Intent intent = getIntent();
        String idPemilikFromIntent = intent.getStringExtra("id_pemilik");
        String namaWarungFromIntent = intent.getStringExtra("nama_warung");
        String jenisWarungFromIntent = intent.getStringExtra("jenis_warung");
        String alamatFromIntent = intent.getStringExtra("alamat");
        String jamBukaFromIntent = intent.getStringExtra("jam_buka");
        String jamTutupFromIntent = intent.getStringExtra("jam_tutup");

        // Cek apakah data tersedia dari Intent
        if (idPemilikFromIntent != null && namaWarungFromIntent != null) {
            // Data dari Intent adalah primer, simpan ke SharedPreferences
            currentIdPemilik = idPemilikFromIntent;
            currentNamaWarung = namaWarungFromIntent;
            editor.putString(KEY_ID_PEMILIK, idPemilikFromIntent);
            editor.putString(KEY_NAMA_WARUNG, namaWarungFromIntent);
            editor.putString(KEY_JENIS_WARUNG, jenisWarungFromIntent);
            editor.putString(KEY_ALAMAT, alamatFromIntent);
            editor.putString(KEY_JAM_BUKA, jamBukaFromIntent);
            editor.putString(KEY_JAM_TUTUP, jamTutupFromIntent);

            // Set TextViews dengan data dari Intent
            namaWarung.setText(currentNamaWarung);
            jenisWarung.setText("Jenis Warung: " + jenisWarungFromIntent);
            alamat.setText("Alamat: " + alamatFromIntent);
            tvAlamat.setText(alamatFromIntent);
            jam_buka.setText("Jam Buka: " + jamBukaFromIntent);
            jam_tutup.setText("Jam Tutup: " + jamTutupFromIntent);
            tvJamOprasi.setText(jamBukaFromIntent + " - " + jamTutupFromIntent);

            // Panggil CallData untuk mengambil no_hp dan email
            CallData(currentIdPemilik, editor);

        } else {
            // Tidak ada data dari Intent, coba muat dari SharedPreferences
            currentIdPemilik = prefs.getString(KEY_ID_PEMILIK, null);
            currentNamaWarung = prefs.getString(KEY_NAMA_WARUNG, "");

            if (currentIdPemilik != null) {
                // Muat dan set TextViews dengan data dari SharedPreferences
                namaWarung.setText(currentNamaWarung);
                jenisWarung.setText("Jenis Warung: " + prefs.getString(KEY_JENIS_WARUNG, ""));
                alamat.setText("Alamat: " + prefs.getString(KEY_ALAMAT, ""));
                tvAlamat.setText(prefs.getString(KEY_ALAMAT, ""));
                jam_buka.setText("Jam Buka: " + prefs.getString(KEY_JAM_BUKA, ""));
                jam_tutup.setText("Jam Tutup: " + prefs.getString(KEY_JAM_TUTUP, ""));
                tvJamOprasi.setText(prefs.getString(KEY_JAM_BUKA, "") + " - " + prefs.getString(KEY_JAM_TUTUP, ""));
                no_hp.setText("No. HP: " + prefs.getString(KEY_NO_HP, ""));
                email.setText("Email: " + prefs.getString(KEY_EMAIL, ""));

                Toast.makeText(this, "Data dimuat dari sesi sebelumnya.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Silakan kembali ke Beranda dan pilih warung.", Toast.LENGTH_LONG).show();
            }
        }

        // Jika currentIdPemilik valid tersedia, atur tombol chat
        if (currentIdPemilik != null) {
            Bundle bundle = new Bundle();
            bundle.putString("id", currentIdPemilik);
            bundle.putString("nama", currentNamaWarung);
            ViewUtils.setButton(btnChat, this, ChatActivity.class, bundle);
        } else {
            btnChat.setEnabled(false);
            btnChat.setAlpha(0.5f);
        }

        // Listener untuk menampilkan/menyembunyikan detail informasi warung
        detailInfoLayout.setOnClickListener(view -> {
            if (cardProduk2Layout.getVisibility() == View.GONE) {
                cardProduk2Layout.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_arrow_up);
                formPengajuanLayout.setVisibility(View.VISIBLE);
            } else {
                cardProduk2Layout.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_arrow_down);
                formPengajuanLayout.setVisibility(View.GONE);
            }
        });

        // Listener untuk tombol "Ajukan Penitipan"
        btnAjukanPenitipan.setOnClickListener(view -> {
            String produksaya = produkSaya.getText().toString().trim();
            String deskripsiproduksaya = deskripsiProdukSaya.getText().toString().trim();
            String kategoriproduksaya = kategoriProdukSaya.getText().toString().trim();

            if (produksaya.isEmpty() || deskripsiproduksaya.isEmpty() || kategoriproduksaya.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Harap isi semua data produk terlebih dahulu.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simpan data produk ke SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editorProduct = sharedPreferences.edit();
            editorProduct.putString("id_pemilik", currentIdPemilik);
            editorProduct.putString("namaProduk", produksaya);
            editorProduct.putString("deskripsi", deskripsiproduksaya);
            editorProduct.putString("kategori", kategoriproduksaya); // Typo corrected here
            editorProduct.apply();

            // Navigasi ke ChatActivity dengan data produk
            Intent intentChat = new Intent(getApplicationContext(), ChatActivity.class);
            intentChat.putExtra("namaProduk", produksaya);
            intentChat.putExtra("deskripsi", deskripsiproduksaya);
            intentChat.putExtra("kategori", kategoriproduksaya);
            intentChat.putExtra("id", currentIdPemilik);
            intentChat.putExtra("nama", currentNamaWarung);
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
                        no_hp.setText("No. HP: " + noHpUser);
                        email.setText("Email: " + emailUser);

                        // Simpan no_hp dan email yang diambil ke SharedPreferences
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
                    String errorMessage = "Gagal mengambil data.";
                    if (error.networkResponse != null) {
                        errorMessage += " Kode: " + error.networkResponse.statusCode;
                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            errorMessage += " Pesan: " + responseBody;
                        } catch (Exception e) {
                            // Abaikan
                        }
                    }
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
        // Tambahkan request ke antrean
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ConstantsVariabels.hideSystemUI(getWindow());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ConstantsVariabels.hideSystemUI(getWindow());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantsVariabels.hideSystemUI(getWindow());
    }
}