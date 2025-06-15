package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DetailPenitipan extends AppCompatActivity {

    TextView itemNameTextView, itemHargaBeliTextView, itemHargaJualTextView,
            itemStokTextView, itemGantiProdukTextView,
            tvJumlahTerjual, tvTanggalTransaksi, tvTotalPenjualan,
            tvCatatanTransaksi, tvPendapatanPenitip, tvPendapatanWarung, tvHargaSatuanTransaksi;

    EditText sisaEditText, catetanEditText;
    Button updateStokButton;
    ImageView imageBack;

    private String currentIdProduk;
    private int currentStokAwal;
    private String currentIdWarung;
    private int currentHargaBeli; // Menyimpan harga beli dari Intent
    private int currentHargaJual; // Menyimpan harga jual dari Intent

    // URL API Anda
    private static final String API_URL_POST_PENJUALAN = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_PENJUALAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penitipan);

        itemNameTextView = findViewById(R.id.itemNameTextView);
        itemHargaBeliTextView = findViewById(R.id.itemHargaBeliTextView);
        itemHargaJualTextView = findViewById(R.id.itemHargaJualTextView);
        itemStokTextView = findViewById(R.id.itemStokTextView);
        itemGantiProdukTextView = findViewById(R.id.itemGantiProdukTextView);

        tvJumlahTerjual = findViewById(R.id.gtvJumlahTerjual);
        tvTanggalTransaksi = findViewById(R.id.gtvTanggalTransaksi);
        tvTotalPenjualan = findViewById(R.id.gtvTotalPenjualan);
        tvCatatanTransaksi = findViewById(R.id.gtvCatatanTransaksi);
        tvPendapatanPenitip = findViewById(R.id.gtvPendapatanPenitip);
        tvPendapatanWarung = findViewById(R.id.gtvPendapatanWarung);
        tvHargaSatuanTransaksi = findViewById(R.id.gtvHargaSatuanTransaksi);

        sisaEditText = findViewById(R.id.sisa);
        catetanEditText = findViewById(R.id.catetan);
        updateStokButton = findViewById(R.id.update_stok);
        imageBack = findViewById(R.id.imageView);

        Intent intent = getIntent();
        currentIdProduk = intent.getStringExtra("id_produk");
        String namaProduk = intent.getStringExtra("nama_produk");
        String hargaBelii = intent.getStringExtra("harga_beli");
        String hargaJuall = intent.getStringExtra("harga_jual");
        currentStokAwal = intent.getIntExtra("stok_awal", 0);
        String gantiProduk = intent.getStringExtra("ganti_produk");
        getData(currentIdProduk);

        // Simpan harga beli dan jual sebagai integer
        currentHargaBeli = Integer.parseInt(hargaBelii);
        currentHargaJual = Integer.parseInt(hargaJuall);

        SharedPreferences userPref = getSharedPreferences("user_pref", MODE_PRIVATE);
        currentIdWarung = userPref.getString("id", null);

        if (currentIdWarung == null || currentIdWarung.isEmpty()) {
            Toast.makeText(this, "ID Warung tidak ditemukan. Mohon login ulang.", Toast.LENGTH_LONG).show();
            Log.e("DetailPenitipan", "ID Warung null atau kosong dari SharedPreferences");
            finish();
            return;
        }

        // Set data awal ke UI (TextView)
        itemNameTextView.setText(namaProduk != null ? namaProduk : "Nama Produk Tidak Ada");
        itemHargaBeliTextView.setText("Harga Beli: Rp " + currentHargaBeli);
        itemHargaJualTextView.setText("Harga Jual: Rp " + currentHargaJual);
        itemStokTextView.setText("Stok Awal: " + currentStokAwal);
        itemGantiProdukTextView.setText("Ganti Produk: " + formatDate(gantiProduk));
        tvHargaSatuanTransaksi.setText("Harga Satuan Transaksi: Rp " + currentHargaJual); // Menggunakan harga jual

        // Inisialisasi tampilan transaksi dengan nilai default
        tvJumlahTerjual.setText("Jumlah Terjual: 0");
        tvTanggalTransaksi.setText("Tanggal Transaksi: Belum Ada Transaksi");
        tvTotalPenjualan.setText("Total Penjualan: Rp 0");
        tvCatatanTransaksi.setText("Catatan: -");
        tvPendapatanPenitip.setText("Pendapatan Penitip: Rp 0");
        tvPendapatanWarung.setText("Pendapatan Warung: Rp 0");


        imageBack.setOnClickListener(view -> finish());

        updateStokButton.setOnClickListener(view -> {
            String sisaString = sisaEditText.getText().toString().trim();
            String catatan = catetanEditText.getText().toString().trim();

            if (sisaString.isEmpty()) {
                sisaEditText.setError("Sisa tidak boleh kosong!");
                Toast.makeText(this, "Mohon masukkan sisa stok.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int sisaInput = Integer.parseInt(sisaString);
                int jumlahTerjualUntukAPI = currentStokAwal - sisaInput;
                if (jumlahTerjualUntukAPI < 0) {
                    jumlahTerjualUntukAPI = 0;
                }

                updateStockAndCalculateProfit(currentIdWarung, currentIdProduk, jumlahTerjualUntukAPI, catatan);

            } catch (NumberFormatException e) {
                sisaEditText.setError("Masukkan angka yang valid untuk Sisa!");
                Toast.makeText(this, "Input Sisa harus berupa angka.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStockAndCalculateProfit(String idWarung, String idProduk, int jumlahTerjual, String catatan) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL_POST_PENJUALAN,
                response -> {
                    Log.d("API_RESPONSE_POST", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.optBoolean("success", false);
                        String message = jsonResponse.optString("message", "Unknown error");

                        if (success) {
                            Toast.makeText(DetailPenitipan.this, message, Toast.LENGTH_SHORT).show();

                            // Dapatkan data terbaru dari respons POST (jika ada, seperti yang diimplementasikan di backend)
                            if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                                JSONObject data = jsonResponse.getJSONObject("data");

                                // Ambil data dari respons POST
                                int newJumlahTerjual = data.optInt("jumlah_terjual", jumlahTerjual);
                                // Dapatkan stok terbaru dari respons (jika backend mengembalikan)
                                int newStok = data.optInt("stok_terbaru", currentStokAwal - newJumlahTerjual);
                                String newTanggalTransaksi = data.optString("tanggal_transaksi", "Tidak ada");
                                // Nilai-nilai ini harusnya sudah dihitung oleh backend dan dikembalikan di respons
                                String newTotalPenjualan = data.optString("total_penjualan", "0");
                                String newPendapatanPenitip = data.optString("pendapatan_penitip", "0");
                                String newPendapatanWarung = data.optString("pendapatan_warung", "0");
                                String newCatatanTransaksi = data.optString("catatan", "-");
                                String newHargaSatuanTransaksi = data.optString("harga_satuan", String.valueOf(currentHargaJual)); // Ambil harga satuan dari respons atau default

                            } else {
                                int calculatedTotalPenjualan = currentHargaJual * jumlahTerjual;
                                int calculatedPendapatanPenitip = currentHargaBeli * jumlahTerjual;
                                int calculatedPendapatanWarung = calculatedTotalPenjualan - calculatedPendapatanPenitip;
                                int calculatedStok = currentStokAwal - jumlahTerjual;
                                if (calculatedStok < 0) calculatedStok = 0;

                                tvJumlahTerjual.setText("Jumlah Terjual: " + jumlahTerjual);
                                tvTanggalTransaksi.setText("Tanggal Transaksi: " + formatDate(getCurrentTimestamp())); // Tanggal saat ini
                                tvTotalPenjualan.setText("Total Penjualan: Rp " + calculatedTotalPenjualan);
                                tvCatatanTransaksi.setText("Catatan: " + catatan);
                                tvPendapatanPenitip.setText("Pendapatan Penitip: Rp " + calculatedPendapatanPenitip);
                                tvPendapatanWarung.setText("Pendapatan Warung: Rp " + calculatedPendapatanWarung);
                                // Harga satuan tetap dari Intent atau nilai awal
                                tvHargaSatuanTransaksi.setText("Harga Satuan Transaksi: Rp " + currentHargaJual);

                                // Update currentStokAwal
                                currentStokAwal = calculatedStok;
                                itemStokTextView.setText("Stok Awal: " + currentStokAwal);

                                Toast.makeText(DetailPenitipan.this, "Data transaksi diperbarui secara manual.", Toast.LENGTH_SHORT).show();
                            }

                            // Reset input field setelah sukses
                            sisaEditText.setText("");
                            catetanEditText.setText("");

                        } else {
                            Toast.makeText(DetailPenitipan.this, "Gagal update stok: " + message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailPenitipan.this, "Error parsing JSON response POST: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR_POST", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("API_ERROR_POST", "Volley POST error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"));
                    Toast.makeText(DetailPenitipan.this, "Error koneksi (POST): " + (error.getMessage() != null ? error.getMessage() : "Periksa koneksi Anda"), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_warung", currentIdWarung);
                params.put("id_produk", currentIdProduk);
                params.put("jumlah_terjual", String.valueOf(jumlahTerjual));
                params.put("catatan", catatan);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void getData(String id){
        String url = API_URL_POST_PENJUALAN +"/" + id;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");

                        if (success) {
                            JSONObject data = response.getJSONObject("data");

                            int idTransaksi = data.getInt("id_transaksi");
                            int idWarung = data.getInt("id_warung");
                            int idProduk = data.getInt("id_produk");
                            int jumlahTerjual = data.getInt("jumlah_terjual");
                            String hargaSatuan = data.getString("harga_satuan");
                            String totalPenjualan = data.getString("total_penjualan");
                            String pendapatanWarung = data.getString("pendapatan_warung");
                            String pendapatanPenitip = data.getString("pendapatan_penitip");
                            String tanggalTransaksi = data.getString("tanggal_transaksi");
                            String catatan = data.getString("catatan");

                            // 🔥 Di sini kamu bisa menampilkan di UI, log, atau set ke TextView
                            Log.d("Transaksi", "ID: " + idTransaksi + ", Jumlah: " + jumlahTerjual);


                            tvJumlahTerjual.setText(String.valueOf("Jumlah Terjual:" +jumlahTerjual));
                            tvTanggalTransaksi.setText("Tanggal Transaksi:"+formatDate(tanggalTransaksi));
                            tvTotalPenjualan.setText("Total Penjualan: Rp." + totalPenjualan);
                            tvCatatanTransaksi.setText("Catatan: " + catatan);
                            tvPendapatanPenitip.setText("Pendapatan Penitip: Rp." + pendapatanPenitip);
                            tvPendapatanWarung.setText("Pendapatan Warung: Rp." + pendapatanWarung);
                            tvHargaSatuanTransaksi.setText("Harga Satuan Transaksi: Rp." + hargaSatuan);

                        } else {
                            Log.e("Transaksi", "Response success = false");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Gagal mengambil data: " + error.getMessage());
                }
        );

        queue.add(jsonObjectRequest);

    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty() || rawDate.equals("Belum Ada Transaksi")) {
            return rawDate;
        }

        // Coba parsing ISO 8601
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("id", "ID")); // Format Indonesia

        try {
            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Jika format ISO 8601 gagal, coba format sebagai tanggal sederhana (misal "2025-06-10")
            if (rawDate.contains("-") && rawDate.length() == 10) { // Contoh: "YYYY-MM-DD"
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date date = simpleDateFormat.parse(rawDate);
                    return outputFormat.format(date); // Format ulang ke output yang diinginkan
                } catch (ParseException ex) {
                    Log.e("DateFormatter", "Error parsing simple date: " + rawDate + " - " + ex.getMessage());
                }
            }
            Log.e("DateFormatter", "Error parsing date: " + rawDate + " - " + e.getMessage());
            return rawDate; // Kembalikan string asli jika semua format gagal
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    @Override protected void onResume()   {
        super.onResume();
        ConstantsVariabels.hideSystemUI(getWindow());
    }
    @Override protected void onPause()    {
        super.onPause();
        ConstantsVariabels.hideSystemUI(getWindow());
    }
    @Override protected void onDestroy()  {
        super.onDestroy();
        ConstantsVariabels.hideSystemUI(getWindow());
    }
}