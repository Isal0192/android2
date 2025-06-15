package com.example.jagajajan; // Sesuaikan dengan paket utama Anda

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.adapter.RiwayatTitipanAdapter; // Sesuaikan dengan paket adapter Anda
import com.example.jagajajan.model.RiwayatTitipan; // Sesuaikan dengan paket model Anda
import com.example.jagajajan.utils.ConstantsVariabels; // Pastikan ini ada dan berisi BASE_URL

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class riwayat_titipan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RiwayatTitipanAdapter adapter;
    private List<RiwayatTitipan> transaksiList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private ImageView imageViewBack;

    private String idPenitip;
    private static final String TAG = "RiwayatTransaksiPenitip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_titipan);

        recyclerView = findViewById(R.id.recyclerViewTransaksiPenitip);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        imageViewBack = findViewById(R.id.imageViewBack);

        // Inisialisasi RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transaksiList = new ArrayList<>();
        adapter = new RiwayatTitipanAdapter(transaksiList);
        recyclerView.setAdapter(adapter);

        // Dapatkan ID Penitip dari Intent
        // Contoh: ID Penitip bisa dikirim dari halaman login atau profil
        idPenitip = getIntent().getStringExtra("id_penitip"); // Default -1 jika tidak ada
        int intIdPenitip = Integer.parseInt(idPenitip);
        if (intIdPenitip == -1) {
            Toast.makeText(this, "ID Penitip tidak ditemukan.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "ID Penitip tidak diterima melalui Intent.");
            finish(); // Tutup activity jika ID tidak valid
            return;
        }

        // Panggil API untuk mendapatkan data transaksi
        fetchTransaksiPenitipData(intIdPenitip);

        // Atur listener untuk tombol kembali
        imageViewBack.setOnClickListener(v -> finish());
    }

    private void fetchTransaksiPenitipData(int idPenitip) {
        progressBar.setVisibility(View.VISIBLE); // Tampilkan progress bar
        tvEmptyState.setVisibility(View.GONE); // Sembunyikan empty state

        // URL API Anda untuk mengambil transaksi berdasarkan ID Penitip
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_PENJUALAN +"/penitip/" + idPenitip;
        Log.d(TAG, "Fetching data from: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE); // Sembunyikan progress bar
                    Log.d(TAG, "API Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Unknown error");

                        if (success) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            transaksiList.clear(); // Bersihkan data lama
                            if (dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject transactionObject = dataArray.getJSONObject(i);
                                    RiwayatTitipan transaksi = RiwayatTitipan.fromJson(transactionObject);
                                    transaksiList.add(transaksi);
                                }
                                adapter.notifyDataSetChanged(); // Beri tahu adapter bahwa data berubah
                                recyclerView.setVisibility(View.VISIBLE);
                                tvEmptyState.setVisibility(View.GONE);
                            } else {
                                tvEmptyState.setVisibility(View.VISIBLE); // Tampilkan empty state
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(riwayat_titipan.this, "Gagal memuat data: " + message, Toast.LENGTH_LONG).show();
                            tvEmptyState.setText(message); // Tampilkan pesan error di empty state
                            tvEmptyState.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(riwayat_titipan.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        tvEmptyState.setText("Terjadi kesalahan saat memproses data.");
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Sembunyikan progress bar
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Log.e(TAG, "Volley error: " + errorMessage);

                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject errorJson = new JSONObject(responseBody);
                            errorMessage = errorJson.optString("message", errorMessage);
                            Log.e(TAG, "Server error response: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        }

                        if (statusCode == 404) {
                            tvEmptyState.setText("Tidak ada riwayat transaksi ditemukan untuk penitip ini.");
                        } else if (statusCode == 400) {
                            tvEmptyState.setText("Permintaan tidak valid: " + errorMessage);
                        } else {
                            tvEmptyState.setText("Error server: " + errorMessage + " (Kode: " + statusCode + ")");
                        }
                    } else {
                        tvEmptyState.setText("Tidak dapat terhubung ke server. Periksa koneksi internet Anda.");
                    }
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Toast.makeText(riwayat_titipan.this, "Error koneksi: " + errorMessage, Toast.LENGTH_LONG).show();
                });

        queue.add(stringRequest);
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