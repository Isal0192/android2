
package com.example.jagajajan;

import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DaftarTitipanActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "DaftarTitipanActivity";
    private static final String URL_DAFTAR_TITIPAN = ConstantsVariabels.BASE_URL + "/titipan"; // Ganti sesuai endpoint

    private ImageView previusPagedt, imgDaftarBarang, cameraDf;
    private EditText addBarangdf, addHargadf, addPcsPack, keuntungandf, addPcsdf, addStockdf, prgntianProduck;
    private Button btnSimpandf;

    private Uri imageUri;
    private String base64Image = null;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_titipan);

        // Inisialisasi view
        previusPagedt = ViewUtils.findViewById(this, R.id.previous_pagedttp);
        imgDaftarBarang = ViewUtils.findViewById(this, R.id.img_daftar_titipan);
        cameraDf = ViewUtils.findViewById(this, R.id.camera_icondf);
        addBarangdf = ViewUtils.findViewById(this, R.id.add_barangdf);
        addHargadf = ViewUtils.findViewById(this, R.id.add_hargadf);
        addPcsPack = ViewUtils.findViewById(this, R.id.add_pcs_packdf);
        keuntungandf = ViewUtils.findViewById(this, R.id.add_kentungan_pemilikdf);
        addPcsdf = ViewUtils.findViewById(this, R.id.add_pcsdf);
        addStockdf = ViewUtils.findViewById(this, R.id.add_stockdf);
        prgntianProduck = ViewUtils.findViewById(this, R.id.pergantian_produckdf);
        btnSimpandf = ViewUtils.findViewById(this, R.id.btn_simpandf);

        requestQueue = Volley.newRequestQueue(this);

        // Kembali ke halaman sebelumnya
       // ViewUtils.setImageViewOnClickListener((ImageView) findViewById(R.id.previous_pagedttp), this, PerofileActivity.class);

        // Pilih gambar
        cameraDf.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
        });

        // Simpan data
        btnSimpandf.setOnClickListener(v -> handleSimpanTitipan());
    }

    // Ambil hasil gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgDaftarBarang.setImageURI(imageUri);
            try {
                base64Image = uriToBase64(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal mengonversi gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSimpanTitipan() {
        String namaBarang = ViewUtils.getText(addBarangdf);
        String harga = ViewUtils.getText(addHargadf);
        String pcsPack = ViewUtils.getText(addPcsPack);
        String keuntungan = ViewUtils.getText(keuntungandf);
        String pcs = ViewUtils.getText(addPcsdf);
        String stok = ViewUtils.getText(addStockdf);
        String penggantiProduk = ViewUtils.getText(prgntianProduck);

        // Validasi sederhana
        if (namaBarang.isEmpty() || harga.isEmpty() || base64Image == null) {
            Toast.makeText(this, "Lengkapi semua data termasuk gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat JSON data
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("nama_barang", namaBarang);
            jsonParams.put("harga", harga);
            jsonParams.put("pcs_pack", pcsPack);
            jsonParams.put("keuntungan", keuntungan);
            jsonParams.put("pcs", pcs);
            jsonParams.put("stok", stok);
            jsonParams.put("pengganti_produk", penggantiProduk);
            jsonParams.put("foto_barang", base64Image); // base64 gambar
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Kirim request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DAFTAR_TITIPAN, jsonParams,
                response -> {
                    Toast.makeText(this, "Barang titipan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish(); // kembali
                },
                error -> {
                    Toast.makeText(this, "Gagal menyimpan: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley error: " + error.getMessage());
                });

        requestQueue.add(jsonObjectRequest);
    }

    private String uriToBase64(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Tidak bisa membuka gambar");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}

