package com.example.jagajajan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity; // Perubahan di sini
import android.support.v7.widget.LinearLayoutManager; // Perubahan di sini
import android.support.v7.widget.RecyclerView; // Perubahan di sini
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.adapter.TitipanAdapter;
import com.example.jagajajan.model.Titipan;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListDagangan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TitipanAdapter adapter;
    private List<Titipan> titipanList;
    private RequestQueue queue;
    private ProgressBar progressBar;
    private TextView titleTextView;
    private String currentIdTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dagangan);

        recyclerView = findViewById(R.id.recyclerViewTitipan);
        progressBar = findViewById(R.id.progressBar);
        titleTextView = findViewById(R.id.titleTextView);
        ImageView imageBack = findViewById(R.id.imageView);

        imageBack.setOnClickListener(view -> finish());

        titipanList = new ArrayList<>();
        adapter = new TitipanAdapter(getApplicationContext(), titipanList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        queue = Volley.newRequestQueue(this);

        fetchTitipanData();
    }

    private void fetchTitipanData() {
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences userPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        String idWarung = userPref.getString("id", null);

        if (idWarung == null || idWarung.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "ID warung tidak ditemukan.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_FORMTITIPAN + "/" + idWarung;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    titipanList.clear();

                    try {
                        // Ambil data titipan
                        JSONArray titipanArray = response.getJSONArray("titipan");
                        for (int i = 0; i < titipanArray.length(); i++) {
                            JSONObject titipanJson = titipanArray.getJSONObject(i);
                            String id = titipanJson.optString("id_titipan", "");
                            String namaProduk = titipanJson.optString("nama_produk", "Tidak diketahui");
                            String harga = titipanJson.optString("harga", "0");
                            String hargaJual = titipanJson.optString("harga_jual", "0");
                            int stok = titipanJson.optInt("stok", 0);
                            String gantiProduk = titipanJson.optString("ganti_produk", "");
                            String fotoBarang = titipanJson.optString("foto_barang", "");

                            Titipan titipan = new Titipan(id, namaProduk, harga, hargaJual, stok, gantiProduk, fotoBarang);
                            titipanList.add(titipan);
                        }

                        // Ambil id_transaksi (jika perlu dipakai)
                        JSONArray penjualanArray = response.getJSONArray("idPenjualan");
                        if (penjualanArray.length() > 0) {
                            String idTransaksi = penjualanArray.getJSONObject(0).optString("id_transaksi", null);
                            Log.d("fetchTitipanData", "ID Transaksi: " + idTransaksi);

                             this.currentIdTransaksi = idTransaksi;
                             Log.d("dad","data id: "+idTransaksi);
                        }

                        adapter.notifyDataSetChanged();

                        if (titipanList.isEmpty()) {
                            Toast.makeText(this, "Tidak ada barang titipan yang ditemukan.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        Log.e("DaftarTitipanActivity", "JSON Parsing error", e);
                        Toast.makeText(this, "Gagal memproses data titipan.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMessage = "Gagal memuat data. ";

                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        errorMessage += "Kode: " + statusCode;

                        try {
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject errorJson = new JSONObject(responseBody);
                            if (errorJson.has("message")) {
                                errorMessage += " - " + errorJson.getString("message");
                            }
                            Log.e("DaftarTitipanActivity", "Error: " + responseBody);
                        } catch (Exception e) {
                            Log.e("DaftarTitipanActivity", "Gagal memproses response error", e);
                        }
                    } else {
                        errorMessage += "Tidak ada respons dari jaringan: " + error.getMessage();
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("DaftarTitipanActivity", "Volley Error", error);
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}