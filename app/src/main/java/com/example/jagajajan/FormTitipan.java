package com.example.jagajajan;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormTitipan extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 100;
    private ImageView imageView;
    private String currentPhotoPath; // Jalur foto yang diambil
    private EditText namaProdukEditText, hargaEditText, hargaJualEditText, stokEditText, gantiProdukEditText;

    private int idLampiranInt;
    private int idPemilikInt;

    private RequestQueue queue; // Volley RequestQueue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_titipan);

        SharedPreferences sharedPreferences = getSharedPreferences("ProductPrefs", MODE_PRIVATE);
        String savedNamaProduk = sharedPreferences.getString("namaProduk", "");

        idPemilikInt = getIntent().getIntExtra("idPemilik", -1);
        idLampiranInt = getIntent().getIntExtra("idProduk", -1);

        // Inisialisasi View
        imageView = findViewById(R.id.imageviewdf);
        ImageView cameraButton = findViewById(R.id.camera_icondf);
        Button kirimButton = findViewById(R.id.btn_simpandf);
        namaProdukEditText = findViewById(R.id.add_barangdf);
        hargaEditText = findViewById(R.id.add_hargadf);
        hargaJualEditText = findViewById(R.id.add_kentungan_pemilikdf);
        stokEditText = findViewById(R.id.add_stockdf);
        gantiProdukEditText = findViewById(R.id.pergantian_produckdf);

        namaProdukEditText.setText(savedNamaProduk);

        Log.d("FormTitipanDebug", "idLampiran diterima (int): " + idLampiranInt);
        Log.d("FormTitipanDebug", "idPemilik diterima (int): " + idPemilikInt);

        // Inisialisasi Volley RequestQueue
        queue = Volley.newRequestQueue(this);

        // --- Aktifkan kembali permission untuk kamera dan penyimpanan ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermissions()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }

        // --- Aktifkan kembali tombol kamera ---
        cameraButton.setVisibility(View.VISIBLE); // Pastikan tombol terlihat
        imageView.setVisibility(View.VISIBLE); // Pastikan ImageView terlihat
        cameraButton.setOnClickListener(view -> dispatchTakePictureIntent()); // Set listener kamera

        // Klik tombol kirim
        kirimButton.setOnClickListener(view -> {
            String namaProduk = namaProdukEditText.getText().toString();
            String harga = hargaEditText.getText().toString();
            String hargaJual = hargaJualEditText.getText().toString();
            String stok = stokEditText.getText().toString();
            String restokFromEditText = gantiProdukEditText.getText().toString();

            String formattedGantiProdukDate = "";
            if (!restokFromEditText.isEmpty()) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                    Date date = inputFormat.parse(restokFromEditText);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // ISO 8601
                    formattedGantiProdukDate = outputFormat.format(date);
                } catch (ParseException e) {
                    Log.e("FormTitipan", "Error parsing ganti_produk date: " + restokFromEditText, e);
                    runOnUiThread(() -> Toast.makeText(FormTitipan.this, "Format tanggal pergantian produk tidak valid (contoh: MM/DD/YY).", Toast.LENGTH_SHORT).show());
                    return;
                }
            } else {
                formattedGantiProdukDate = "";
            }

            if (namaProduk.isEmpty() || harga.isEmpty() || hargaJual.isEmpty() || stok.isEmpty() || formattedGantiProdukDate.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi semua data.", Toast.LENGTH_SHORT).show();
                return;
            }

            sendDataToServer(idLampiranInt, idPemilikInt, namaProduk, harga, hargaJual, stok, formattedGantiProdukDate, currentPhotoPath);
        });
    }

    // --- Metode terkait permission kamera diaktifkan kembali ---
    private boolean checkPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && storage == PackageManager.PERMISSION_GRANTED;
    }

    // --- Metode terkait kamera diaktifkan kembali ---
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Gagal membuat file gambar: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FormTitipan", "Error creating image file", ex);
                return;
            }

            if (photoFile != null) {
                // Menggunakan FileProvider untuk keamanan
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.jagajajan.provider", // Ganti dengan authority FileProvider Anda
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Direktori Pictures di penyimpanan publik
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("FormTitipan", "Failed to create directory: " + storageDir.getAbsolutePath());
                throw new IOException("Failed to create pictures directory.");
            }
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath(); // Simpan jalur file
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Gambar berhasil diambil dan disimpan di currentPhotoPath
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                // Tampilkan gambar di ImageView
                imageView.setImageURI(Uri.fromFile(imgFile));
                Toast.makeText(this, "Foto berhasil diambil!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "File gambar tidak ditemukan. Mungkin gagal disimpan.", Toast.LENGTH_SHORT).show();
                Log.e("FormTitipan", "Image file not found at: " + currentPhotoPath);
                currentPhotoPath = null; // Reset path jika gagal
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Pengambilan foto dibatalkan.", Toast.LENGTH_SHORT).show();
            currentPhotoPath = null; // Kosongkan path jika dibatalkan
        }
    }

    private void sendDataToServer(final int idLampiran, final int idPemilik, final String namaProduk, final String harga,
                                  final String hargaJual, final String stok, final String gantiProdukDate, final String fotoPath) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_FORMTITIPAN;

        int stokInt = 0;
        if (stok != null && !stok.isEmpty()) {
            try {
                stokInt = Integer.parseInt(stok);
            } catch (NumberFormatException e) {
                runOnUiThread(() -> Toast.makeText(FormTitipan.this, "Format stok tidak valid.", Toast.LENGTH_SHORT).show());
                Log.e("FormTitipan", "Invalid stock format", e);
                return;
            }
        }

        // --- Konversi gambar ke Base64 (Aktifkan kembali) ---
        String base64Image = "";
        if (fotoPath != null && !fotoPath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(fotoPath);
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // Kompresi gambar: coba 60% kualitas untuk mengurangi ukuran file
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] imageBytes = baos.toByteArray();
                base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                bitmap.recycle(); // Penting: Bebaskan memori bitmap
                Log.d("FormTitipanDebug", "Foto Barang (Base64 length): " + base64Image.length());

                if (base64Image.length() > (10 * 1024 * 1024 / 0.75)) { // Estimasi 10MB
                    runOnUiThread(() -> Toast.makeText(FormTitipan.this, "Ukuran foto terlalu besar. Coba ambil foto dengan resolusi lebih rendah.", Toast.LENGTH_LONG).show());
                    return;
                }

            } else {
                Log.e("FormTitipan", "Bitmap is null. Could not decode file: " + fotoPath);
                runOnUiThread(() -> Toast.makeText(FormTitipan.this, "Gagal memproses gambar. Ambil ulang foto.", Toast.LENGTH_SHORT).show());
                return;
            }
        } else {
            Log.d("FormTitipanDebug", "Tidak ada foto yang diambil.");
        }


        // Buat JSON object untuk request body
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("id_lampiran", String.valueOf(idLampiran));
            jsonParam.put("id_pemilikwarung", String.valueOf(idPemilik));
            jsonParam.put("nama_produk", namaProduk);
            jsonParam.put("harga", harga);
            jsonParam.put("harga_jual", hargaJual);
            jsonParam.put("stok", String.valueOf(stokInt));
            jsonParam.put("ganti_produk", gantiProdukDate);
            jsonParam.put("foto_barang", base64Image);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(FormTitipan.this, "Kesalahan dalam membuat JSON.", Toast.LENGTH_SHORT).show());
            return;
        }

        // Log JSON body lengkap sebelum dikirim
        Log.d("FormTitipanAndroid", "JSON Body to be sent: " + jsonParam.toString());

        // Buat request POST menggunakan Volley
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonParam, // Langsung berikan JSONObject sebagai body
                response -> {
                    // Penanganan response sukses
                    runOnUiThread(() -> {
                        Toast.makeText(FormTitipan.this, "Form Titipan berhasil dikirim", Toast.LENGTH_LONG).show();
                        // Reset currentPhotoPath setelah berhasil dikirim
                        currentPhotoPath = null;
                        finish();
                    });
                },
                error -> {
                    // Penanganan error
                    runOnUiThread(() -> {
                        String errorMessage = "Gagal mengirim data. ";
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            errorMessage += "Kode: " + statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                Log.e("FormTitipan", "Error response body: " + responseBody);
                                // Coba parse error JSON dari backend jika ada
                                JSONObject errorJson = new JSONObject(responseBody);
                                if (errorJson.has("error")) {
                                    errorMessage += " - " + errorJson.getString("error");
                                } else if (errorJson.has("message")) { // Beberapa backend menggunakan "message"
                                    errorMessage += " - " + errorJson.getString("message");
                                }
                            } catch (Exception e) {
                                Log.e("FormTitipan", "Error parsing error response body", e);
                            }
                        } else {
                            errorMessage += "Tidak ada respons jaringan: " + error.getMessage();
                        }
                        Toast.makeText(FormTitipan.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("FormTitipan", "Volley Error: " + error.toString());
                    });
                }
        ) {
            // Tambahkan header Content-Type secara eksplisit
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Tambahkan request ke antrean Volley
        queue.add(request);
    }

    // --- Metode onRequestPermissionsResult diaktifkan kembali ---
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (!checkPermissions()) {
                Toast.makeText(this, "Izin kamera dan penyimpanan ditolak. Fitur foto tidak akan berfungsi.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Izin kamera dan penyimpanan diberikan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}