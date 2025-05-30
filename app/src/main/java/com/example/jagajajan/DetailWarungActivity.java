package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity; // Reverted to android.support.v7.app.AppCompatActivity
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
import com.example.jagajajan.utils.ViewUtils; // Assuming this utility class exists

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DetailWarungActivity extends AppCompatActivity {

    TextView namaWarung, alamat, no_hp, email, jam_buka, jam_tutup, tvAlamat, tvJamOprasi, jenisWarung;
    ImageButton btnChat;
    ImageView btnBack, arrow;
    LinearLayout detailInfo, detailPeroduk, formPengajuan;
    Button pengajuan;
    EditText produkSaya, deskripsiPerodukSaya, kategoriPerodukSaya;
    private String currentIdPemilik, namaWarungFromIntent;
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

        final boolean[] trig = {false};
        final boolean[] isFormVisible = {false}; // status form apakah sudah tampil

        detailInfo.setOnClickListener(view -> {
            if (detailPeroduk.getVisibility() == View.GONE) {
                detailPeroduk.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_arrow_up);
                trig[0] = true;
            } else {
                detailPeroduk.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_arrow_down);
                trig[0] = false;
            }
        });

        produkSaya = findViewById(R.id.produk_saya);
        deskripsiPerodukSaya = findViewById(R.id.deskripsi_produk_saya);
        kategoriPerodukSaya = findViewById(R.id.katagori_produk_saya);

        pengajuan.setOnClickListener(view -> {
            if (trig[0]) {
                if (!isFormVisible[0]) {
                    formPengajuan.setVisibility(View.VISIBLE);
                    isFormVisible[0] = true;
                } else {
                    // Validask isi
                    String produksaya = produkSaya.getText().toString().trim();
                    String deskripsiproduksaya = deskripsiPerodukSaya.getText().toString().trim();
                    String kategoriproduksaya = kategoriPerodukSaya.getText().toString().trim();


                    if (produksaya.isEmpty() || deskripsiproduksaya.isEmpty() || kategoriproduksaya.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "isi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("namaProduk", produksaya);
                        intent.putExtra("deskripsi", deskripsiproduksaya);
                        intent.putExtra("kategori", kategoriproduksaya);
                        intent.putExtra("id",currentIdPemilik);
                        intent.putExtra("nama", namaWarungFromIntent);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Lihaat detail warung dulu", Toast.LENGTH_SHORT).show();
            }
        });



        // Set OnClickListener for back button to navigate to Home activity
        ViewUtils.spesialImgViewOnClick(btnBack, this, Home.class,PREFS_NAME );

        // Get SharedPreferences instance
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Retrieve data from Intent
        Intent intent = getIntent();
        String idPemilikFromIntent = intent.getStringExtra("id_pemilik");
        String namaWarungFromIntent = intent.getStringExtra("nama_warung");
        String jenisWarungFromIntent = intent.getStringExtra("jenis_warung");
        String alamatFromIntent = intent.getStringExtra("alamat");
        String jamBukaFromIntent = intent.getStringExtra("jam_buka");
        String jamTutupFromIntent = intent.getStringExtra("jam_tutup");



        // Check if data is available from Intent
        if (idPemilikFromIntent != null) {
            // Data from Intent is primary, save it to SharedPreferences
            currentIdPemilik = idPemilikFromIntent;
            editor.putString(KEY_ID_PEMILIK, idPemilikFromIntent);
            editor.putString(KEY_NAMA_WARUNG, namaWarungFromIntent);
            editor.putString(KEY_JENIS_WARUNG, jenisWarungFromIntent);
            editor.putString(KEY_ALAMAT, alamatFromIntent);
            editor.putString(KEY_JAM_BUKA, jamBukaFromIntent);
            editor.putString(KEY_JAM_TUTUP, jamTutupFromIntent);
            editor.apply(); // Apply changes asynchronously

            // Set TextViews with data from Intent
            namaWarung.setText(namaWarungFromIntent);
            jenisWarung.setText(jenisWarungFromIntent);
            alamat.setText("alamat: " + alamatFromIntent);
            tvAlamat.setText(alamatFromIntent);
            jam_buka.setText("Buka:   " + jamBukaFromIntent);
            jam_tutup.setText("Tutup:   " + jamTutupFromIntent);
            tvJamOprasi.setText(jamBukaFromIntent + " - " + jamTutupFromIntent);

        } else {
            // No data from Intent, try to load from SharedPreferences
            currentIdPemilik = prefs.getString(KEY_ID_PEMILIK, null);
            if (currentIdPemilik != null) {
                // Load and set TextViews with data from SharedPreferences
                namaWarung.setText(prefs.getString(KEY_NAMA_WARUNG, ""));
                jenisWarung.setText(prefs.getString(KEY_JENIS_WARUNG, ""));
                alamat.setText("alamat: " + prefs.getString(KEY_ALAMAT, ""));
                tvAlamat.setText(prefs.getString(KEY_ALAMAT, ""));
                jam_buka.setText("Buka:   " + prefs.getString(KEY_JAM_BUKA, ""));
                jam_tutup.setText("Tutup:   " + prefs.getString(KEY_JAM_TUTUP, ""));
                tvJamOprasi.setText(prefs.getString(KEY_JAM_BUKA, "") + " - " + prefs.getString(KEY_JAM_TUTUP, ""));
                no_hp.setText("no hp:   " + prefs.getString(KEY_NO_HP, ""));
                email.setText("email:   " + prefs.getString(KEY_EMAIL, ""));

                Toast.makeText(this, "Data loaded from previous session.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please go back to Home and select a warung.", Toast.LENGTH_LONG).show();
            }
        }

        // If a valid idPemilik is available (either from Intent or SharedPreferences), call data
        if (currentIdPemilik != null) {
            CallData(currentIdPemilik, editor); // Pass editor to save fetched data
            Bundle bundle = new Bundle();
            bundle.putString("id", currentIdPemilik);
            bundle.putString("nama", namaWarungFromIntent);
            ViewUtils.setButton(btnChat, this, ChatActivity.class, bundle);
        } else {
            // Disable chat button if no warung ID is available
            btnChat.setEnabled(false);
            btnChat.setAlpha(0.5f); // Visually indicate it's disabled
        }
    }


    /**
     * Fetches additional warung details (no_hp, email) from the API using the provided ID.
     *
     * @param id The ID of the warung owner.
     * @param editor SharedPreferences.Editor to save fetched data.
     */
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
                        Toast.makeText(getApplicationContext(), "Failed to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        // Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }
}
