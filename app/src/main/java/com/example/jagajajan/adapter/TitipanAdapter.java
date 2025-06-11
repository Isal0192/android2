package com.example.jagajajan.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.R;
import com.example.jagajajan.model.Titipan;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet; // Import baru
import java.util.List;
import java.util.Map;
import java.util.Set;    // Import baru
import java.util.jar.JarException;

public class TitipanAdapter extends RecyclerView.Adapter<TitipanAdapter.TitipanViewHolder> {
    private Context context;
    private List<Titipan> titipanList;
    RequestQueue queue;
    // Set untuk menyimpan ID produk yang penjualannya sudah dicatat
    private Set<String> recordedProductIds = new HashSet<>();

    public TitipanAdapter(Context context, List<Titipan> titipanList) {
        this.context = context;
        this.titipanList = titipanList;
    }

    @NonNull
    @Override
    public TitipanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_titipan, parent, false);
        return new TitipanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TitipanViewHolder holder, int position) {
        Titipan titipan = titipanList.get(position);

        holder.itemNameTextView.setText(titipan.getNamaProduk());
        holder.itemHargaBeliTextView.setText("Harga Beli: Rp " + titipan.getHarga());
        holder.itemHargaJualTextView.setText("Harga Jual: Rp " + titipan.getHargaJual());
        holder.itemStokTextView.setText("Stok: " + titipan.getStok());
        String tangal = titipan.getGantiProduk().split("T")[0];
        holder.itemGantiProdukTextView.setText("Ganti Produk: " + tangal);

        final boolean[] isDetailVisible = {false};
        final boolean[] isInputVisible = {false};

        holder.card.setOnClickListener(view -> {
            if (isDetailVisible[0]) {
                holder.detailInfo.setVisibility(View.GONE);
                isDetailVisible[0] = false;
            } else {
                holder.detailInfo.setVisibility(View.VISIBLE);
                getData(titipan.getId(), holder);
                isDetailVisible[0] = true;
                holder.inputUpdate.setVisibility(View.GONE);
                isInputVisible[0] = false;
            }
        });

        holder.updateStok.setOnClickListener(view -> {
            if (isInputVisible[0]) {
                holder.inputUpdate.setVisibility(View.GONE);
                isInputVisible[0] = false;
            } else {
                holder.inputUpdate.setVisibility(View.VISIBLE);
                isInputVisible[0] = true;
                holder.detailInfo.setVisibility(View.GONE);
                isDetailVisible[0] = false;
            }
        });

        // Logika untuk mencegah create data berulang
        if (recordedProductIds.contains(titipan.getId())) {
            holder.update.setEnabled(false); // Menonaktifkan tombol
            holder.update.setText("Sudah Tercatat"); // Mengubah teks tombol
            holder.sisa.setEnabled(false); // Menonaktifkan input jumlah
            holder.catetan.setEnabled(false); // Menonaktifkan input catatan
        } else {
            holder.update.setEnabled(true); // Mengaktifkan tombol jika belum tercatat
            holder.update.setText("Catat Penjualan"); // Mengembalikan teks tombol
            holder.sisa.setEnabled(true); // Mengaktifkan input jumlah
            holder.catetan.setEnabled(true); // Mengaktifkan input catatan
        }

        holder.update.setOnClickListener(v -> {
            // Menonaktifkan tombol sementara untuk mencegah double click
            holder.update.setEnabled(false);

            String newSisa = holder.sisa.getText().toString().trim();
            String newCatetan = holder.catetan.getText().toString().trim();

            if (newSisa.isEmpty()) {
                Toast.makeText(context, "Jumlah terjual tidak boleh kosong", Toast.LENGTH_SHORT).show();
                holder.update.setEnabled(true); // Aktifkan kembali tombol jika validasi gagal
                return;
            }
            // Memanggil createNewSale (nama metode diganti agar lebih jelas)
            createNewSale(titipan.getId(), newSisa, newCatetan, holder.update);
        });

        if (titipan.getFotoBarang() != null && !titipan.getFotoBarang().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(titipan.getFotoBarang(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.itemImageView.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.itemImageView.setImageResource(R.drawable.background_rauded_white);
            }
        } else {
            holder.itemImageView.setImageResource(R.drawable.background_rauded_white);
        }
    }

    @Override
    public int getItemCount() {
        return titipanList.size();
    }

    // Nama metode diganti agar lebih sesuai dengan fungsinya (CREATE data)
    public void createNewSale(String idProduk, String jumlahTerjualStr, String catatan, Button updateButton) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_PENJUALAN;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        SharedPreferences userPref = context.getSharedPreferences("user_pref", MODE_PRIVATE);
        String idWarungStr = userPref.getString("id", null);

        try {
            JSONObject jsonObject = new JSONObject();

            if (idWarungStr != null && !idWarungStr.isEmpty()) {
                jsonObject.put("id_warung", Integer.parseInt(idWarungStr));
            } else {
                Log.e("CreateNewSale", "ID Warung tidak ditemukan di SharedPreferences.");
                Toast.makeText(context, "ID Warung tidak ditemukan. Tidak dapat mencatat penjualan.", Toast.LENGTH_LONG).show();
                updateButton.setEnabled(true); // Aktifkan kembali tombol jika ada masalah
                return;
            }

            if (idProduk != null && !idProduk.isEmpty()) {
                jsonObject.put("id_produk", Integer.parseInt(idProduk));
            } else {
                Log.e("CreateNewSale", "ID Produk tidak valid.");
                Toast.makeText(context, "ID Produk tidak valid. Tidak dapat mencatat penjualan.", Toast.LENGTH_LONG).show();
                updateButton.setEnabled(true); // Aktifkan kembali tombol jika ada masalah
                return;
            }

            try {
                jsonObject.put("jumlah_terjual", Integer.parseInt(jumlahTerjualStr));
            } catch (NumberFormatException e) {
                Log.e("CreateNewSale", "Jumlah terjual tidak valid (bukan angka): " + jumlahTerjualStr);
                Toast.makeText(context, "Jumlah terjual harus angka. Tidak dapat mencatat penjualan.", Toast.LENGTH_LONG).show();
                updateButton.setEnabled(true); // Aktifkan kembali tombol jika ada masalah
                return;
            }

            jsonObject.put("catatan", catatan);

            JsonObjectRequest createRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    response -> {
                        try {
                            String message = response.getString("message");
                            JSONObject data = response.getJSONObject("data");

                            int idTransaksi = data.getInt("id_transaksi");
                            int idWarungResponse = data.getInt("id_warung");
                            int idProdukResponse = data.getInt("id_produk");
                            int jumlahTerjualResponse = data.getInt("jumlah_terjual");
                            String hargaSatuan = data.getString("harga_satuan");
                            String totalPenjualan = data.getString("total_penjualan");
                            String pendapatanWarung = data.getString("pendapatan_warung");
                            String pendapatanPenitip = data.getString("pendapatan_penitip");
                            String tanggalTransaksi = data.getString("tanggal_transaksi");
                            String catatanResponse = data.getString("catatan");

                            Log.d("CreateSaleSuccess", "Message: " + message);
                            Log.d("CreateSaleSuccess", "ID Transaksi: " + idTransaksi);
                            Log.d("CreateSaleSuccess", "ID Warung (Resp): " + idWarungResponse);
                            Log.d("CreateSaleSuccess", "ID Produk (Resp): " + idProdukResponse);
                            Log.d("CreateSaleSuccess", "Jumlah Terjual (Resp): " + jumlahTerjualResponse);
                            Log.d("CreateSaleSuccess", "Harga Satuan: " + hargaSatuan);
                            Log.d("CreateSaleSuccess", "Total Penjualan: " + totalPenjualan);
                            Log.d("CreateSaleSuccess", "Pendapatan Warung: " + pendapatanWarung);
                            Log.d("CreateSaleSuccess", "Pendapatan Penitip: " + pendapatanPenitip);
                            Log.d("CreateSaleSuccess", "Tanggal Transaksi: " + tanggalTransaksi);
                            Log.d("CreateSaleSuccess", "Catatan: " + catatanResponse);

                            Toast.makeText(context, "Penjualan berhasil dicatat!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Gagal mengurai respons server: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            updateButton.setEnabled(true); // Aktifkan kembali tombol jika parsing gagal
                        }
                    },
                    error -> {
                        Log.e("CreateSaleError", "Error: " + error.toString());
                        String errorMessage = "Terjadi kesalahan saat mencatat penjualan.";
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject jsonError = new JSONObject(responseBody);
                                if (jsonError.has("message")) {
                                    errorMessage = jsonError.getString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        updateButton.setEnabled(true); // Aktifkan kembali tombol saat ada error
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(createRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Terjadi kesalahan dalam membuat request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            updateButton.setEnabled(true); // Aktifkan kembali tombol jika ada kesalahan JSON
        }
    }


    public void getData(String id, TitipanViewHolder holder) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_PENJUALAN + "/" + id;

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray dataArray = jsonResponse.getJSONArray("data");

                        if (dataArray.length() > 0) {
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String idTransaksi = dataObject.getString("id_transaksi");
                            String jumlahTerjual = dataObject.getString("jumlah_terjual");
                            String hargaSatuan = dataObject.getString("harga_satuan");
                            String totalPenjualan = dataObject.getString("total_penjualan");
                            String pendapatanWarung = dataObject.getString("pendapatan_warung");
                            String pendapatanPenitip = dataObject.getString("pendapatan_penitip");
                            String catatan = dataObject.getString("catatan");

                            String tanggal = dataObject.getString("tanggal_transaksi").split("T")[0];
//
                            Log.d("data", "data masuk"+idTransaksi);

                            holder.hargaSatuan.setText("Harga Satuan Transaksi: Rp."+hargaSatuan);
                            holder.pendapatanWatung.setText("Pendapatan Warung: Rp."+pendapatanWarung);
                            holder.pendapatanPenitip.setText("Pendapatan Penitip: Rp."+pendapatanPenitip);
                            holder.totalPenjualan.setText("Jumlah Terjual: "+jumlahTerjual);
                            holder.tanggalTransaksi.setText("Tanggal Di Baut: "+tanggal);
                            holder.cacatanTransaksi.setText("catatan: "+catatan);
                            holder.totalPenjualan.setText("Total Pendapatan: Rp."+totalPenjualan);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("GET_DATA", "Error: " + error.toString());
                }
        );

        requestQueue.add(stringRequest);
    }

    public static class TitipanViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemHargaBeliTextView;
        TextView itemHargaJualTextView;
        TextView itemStokTextView;
        TextView itemGantiProdukTextView;
        // detail
        LinearLayout card, detailInfo, inputUpdate;
        TextView tanggalTransaksi, totalPenjualan, cacatanTransaksi, pendapatanPenitip, pendapatanWatung, hargaSatuan;
        //create data
        Button updateStok, update; // Nama "update" masih dipakai, tapi fungsinya sekarang "create"
        EditText sisa, catetan;



        public TitipanViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemHargaBeliTextView = itemView.findViewById(R.id.itemHargaBeliTextView);
            itemHargaJualTextView = itemView.findViewById(R.id.itemHargaJualTextView);
            itemStokTextView = itemView.findViewById(R.id.itemStokTextView);
            itemGantiProdukTextView = itemView.findViewById(R.id.itemGantiProdukTextView);
            // detail
            card = itemView.findViewById(R.id.card_details);
            detailInfo = itemView.findViewById(R.id.transactionDetailsLayout);
            tanggalTransaksi = itemView.findViewById(R.id.tvTanggalTransaksi);
            totalPenjualan = itemView.findViewById(R.id.tvTotalPenjualan);
            cacatanTransaksi = itemView.findViewById(R.id.tvCatatanTransaksi);
            pendapatanPenitip = itemView.findViewById(R.id.tvPendapatanPenitip);
            pendapatanWatung = itemView.findViewById(R.id.tvPendapatanWarung);
            hargaSatuan = itemView.findViewById(R.id.tvHargaSatuanTransaksi);
            updateStok = itemView.findViewById(R.id.updateStok);
            //create data
            inputUpdate = itemView.findViewById(R.id.input_update_data);
            sisa = itemView.findViewById(R.id.sisa);
            catetan = itemView.findViewById(R.id.catetan);
            update = itemView.findViewById(R.id.update_stok); // Ini tombol untuk mencatat penjualan
        }
    }

    public void updateData(List<Titipan> newList) {
        titipanList.clear();
        titipanList.addAll(newList);
        // Penting: Kosongkan recordedProductIds saat data di-refresh,
        // karena status "sudah dicatat" mungkin tidak berlaku lagi.
        // Atau, jika kamu ingin status ini persisten, pertimbangkan SharedPReferences.
        recordedProductIds.clear();
        notifyDataSetChanged();
    }
}