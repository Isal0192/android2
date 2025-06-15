package com.example.jagajajan.model; // Sesuaikan dengan paket Anda

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RiwayatTitipan {
    private int idTransaksi;
    private int idWarung;
    private int idProdukTitipan; // Ini id_produk dari formtitipan
    private int jumlahTerjual;
    private String hargaSatuan;
    private String totalPenjualan;
    private String pendapatanWarung;
    private String pendapatanPenitip;
    private String tanggalTransaksi;
    private String catatan;
    private String namaWarung;
    private String namaPemilikWarung;
    private String namaProdukTitipan; // Nama produk dari formtitipan
    private String namaProdukPenitipAsli; // Nama produk dari produkpenitip
    private String namaPenitip; // Nama penitip dari produkpenitip

    // Konstruktor
    public RiwayatTitipan(int idTransaksi, int idWarung, int idProdukTitipan, int jumlahTerjual,
                            String hargaSatuan, String totalPenjualan, String pendapatanWarung,
                            String pendapatanPenitip, String tanggalTransaksi, String catatan,
                            String namaWarung, String namaPemilikWarung, String namaProdukTitipan,
                            String namaProdukPenitipAsli, String namaPenitip) {
        this.idTransaksi = idTransaksi;
        this.idWarung = idWarung;
        this.idProdukTitipan = idProdukTitipan;
        this.jumlahTerjual = jumlahTerjual;
        this.hargaSatuan = hargaSatuan;
        this.totalPenjualan = totalPenjualan;
        this.pendapatanWarung = pendapatanWarung;
        this.pendapatanPenitip = pendapatanPenitip;
        this.tanggalTransaksi = tanggalTransaksi;
        this.catatan = catatan;
        this.namaWarung = namaWarung;
        this.namaPemilikWarung = namaPemilikWarung;
        this.namaProdukTitipan = namaProdukTitipan;
        this.namaProdukPenitipAsli = namaProdukPenitipAsli;
        this.namaPenitip = namaPenitip;
    }

    // Metode untuk membuat objek TransaksiPenitip dari JSONObject (untuk parsing API response)
    public static RiwayatTitipan fromJson(JSONObject jsonObject) throws JSONException {
        // Parsing data dasar transaksi
        int idTransaksi = jsonObject.getInt("id_transaksi");
        int idWarung = jsonObject.getInt("id_warung");
        int idProdukTitipan = jsonObject.getInt("id_produk"); // ini id_titipan
        int jumlahTerjual = jsonObject.getInt("jumlah_terjual");
        String hargaSatuan = jsonObject.getString("harga_satuan");
        String totalPenjualan = jsonObject.getString("total_penjualan");
        String pendapatanWarung = jsonObject.getString("pendapatan_warung");
        String pendapatanPenitip = jsonObject.getString("pendapatan_penitip");
        String tanggalTransaksi = jsonObject.getString("tanggal_transaksi");
        String catatan = jsonObject.optString("catatan", "-"); // Catatan bisa null di backend

        // Parsing data warung
        JSONObject warungJson = jsonObject.getJSONObject("warung");
        String namaWarung = warungJson.getString("nama_warung");
        JSONObject pemilikWarungJson = warungJson.getJSONObject("pengguna");
        String namaPemilikWarung = pemilikWarungJson.getString("nama_lengkap");

        // Parsing data produk (formtitipan)
        JSONObject produkJson = jsonObject.getJSONObject("produk");
        String namaProdukTitipan = produkJson.getString("nama_produk");
        JSONObject produkPenitipJson = produkJson.getJSONObject("produkpenitip");
        String namaProdukPenitipAsli = produkPenitipJson.getString("nama_produk");
        JSONObject penitipJson = produkPenitipJson.getJSONObject("pengguna");
        String namaPenitip = penitipJson.getString("nama_lengkap");

        return new RiwayatTitipan(
                idTransaksi, idWarung, idProdukTitipan, jumlahTerjual,
                hargaSatuan, totalPenjualan, pendapatanWarung, pendapatanPenitip,
                tanggalTransaksi, catatan,
                namaWarung, namaPemilikWarung, namaProdukTitipan,
                namaProdukPenitipAsli, namaPenitip
        );
    }

    // --- Getter Methods ---
    public int getIdTransaksi() { return idTransaksi; }
    public int getIdWarung() { return idWarung; }
    public int getIdProdukTitipan() { return idProdukTitipan; }
    public int getJumlahTerjual() { return jumlahTerjual; }
    public String getHargaSatuan() { return hargaSatuan; }
    public String getTotalPenjualan() { return totalPenjualan; }
    public String getPendapatanWarung() { return pendapatanWarung; }
    public String getPendapatanPenitip() { return pendapatanPenitip; }
    public String getTanggalTransaksi() { return tanggalTransaksi; }
    public String getCatatan() { return catatan; }
    public String getNamaWarung() { return namaWarung; }
    public String getNamaPemilikWarung() { return namaPemilikWarung; }
    public String getNamaProdukTitipan() { return namaProdukTitipan; }
    public String getNamaProdukPenitipAsli() { return namaProdukPenitipAsli; }
    public String getNamaPenitip() { return namaPenitip; }

    // Metode pembantu untuk memformat tanggal
    public String getFormattedTanggalTransaksi() {
        // Implementasi formatDate dari DetailPenitipan.java bisa disalin ke sini atau dibuat utility
        // Untuk saat ini, kita akan lakukan formatting sederhana.
        // Sebaiknya buat kelas utilitas tanggal terpisah jika banyak digunakan.
        if (tanggalTransaksi == null || tanggalTransaksi.isEmpty()) {
            return "N/A";
        }
        try {
            // Contoh format: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(tanggalTransaksi);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("id", "ID"));
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("TransaksiPenitip", "Error parsing date: " + tanggalTransaksi + " - " + e.getMessage());
            return tanggalTransaksi; // Kembalikan string asli jika gagal parse
        }
    }
}