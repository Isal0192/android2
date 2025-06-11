package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jagajajan.adapter.ChatAdapter;
import com.example.jagajajan.model.ChatMessage;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private final ArrayList<ChatMessage> chatList = new ArrayList<>();

    private int penitipId;
    private int pemilikId;

    private EditText editTextPesan;
    private ImageView buttonKirim;
    private ImageView btnBack;
    private TextView title;

    private static final int REFRESH_INTERVAL = 2_000; // 2 s
    private final Handler handler = new Handler();
    private Runnable refreshChatRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        getIntentData();
        initRecyclerView();
        setupChatPolling();
        setupSendButton();

        sendPesanOtomatisJikaAda();
    }

    /* -------------------------------------------------------
       1.  Inisialisasi View & Data
       ------------------------------------------------------- */
    private void initViews() {
        editTextPesan = findViewById(R.id.Pesan_chat);
        buttonKirim   = findViewById(R.id.button_kirim);
        btnBack       = findViewById(R.id.btn_back);
        title         = findViewById(R.id.title);

        ViewUtils.setImageViewOnClickListener(btnBack, this, Home.class);
    }

    private void getIntentData() {
        SharedPreferences prefUser = getSharedPreferences("user_pref", MODE_PRIVATE);
        penitipId = Integer.parseInt(prefUser.getString("id", "0"));

        Intent intent = getIntent();
        pemilikId = Integer.parseInt(intent.getStringExtra("id"));
        title.setText(intent.getStringExtra("nama"));
    }

    private void initRecyclerView() {
        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, chatList, penitipId);
        recyclerChat.setAdapter(chatAdapter);
    }

    /* -------------------------------------------------------
       2.  Polling & Kirim Pesan
       ------------------------------------------------------- */
    private void setupChatPolling() {
        refreshChatRunnable = () -> {
            getChatMessages();
            handler.postDelayed(refreshChatRunnable, REFRESH_INTERVAL);
        };
    }

    private void setupSendButton() {
        buttonKirim.setOnClickListener(v -> {
            String isi = editTextPesan.getText().toString().trim();
            if (!isi.isEmpty()) {
                kirimPesan(isi);
                editTextPesan.setText("");
            }
        });
    }

    /* -------------------------------------------------------
       3.  Kirim Produk otomatis (POST)  🔧 DIPERBAIKI
       ------------------------------------------------------- */
    private void sendPesanOtomatisJikaAda() {
        Intent intent = getIntent();
        String namaProduk = intent.getStringExtra("namaProduk");
        if (namaProduk == null || namaProduk.isEmpty()) return; // tidak ada produk

        String deskripsi = intent.getStringExtra("deskripsi");
        String kategori  = intent.getStringExtra("kategori");

        Executors.newSingleThreadExecutor().execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PRODUK);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15_000);
                conn.setConnectTimeout(15_000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // payload JSON
                JSONObject payload = new JSONObject()
                        .put("id_penitip",  penitipId)
                        .put("id_penerima", pemilikId)
                        .put("nama_produk", namaProduk)
                        .put("deskripsi",   deskripsi)
                        .put("kategori",    kategori);

                // kirim body
                try (OutputStream os = conn.getOutputStream()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        os.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }

                // terima respons
                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED) {

                    /* 🔧  BACA BODY RESPON  */
                    StringBuilder sb = new StringBuilder();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                            String line;
                            while ((line = br.readLine()) != null) sb.append(line);
                        }
                    }

                    /* 🔧  PARSE JSON  */
                    JSONObject root          = new JSONObject(sb.toString());
                    JSONObject idProdukObj   = root.getJSONObject("id_produk");
                    int idProduk             = idProdukObj.getInt("id_produk");
                    String status            = idProdukObj.getString("status");

                    // simpan di SharedPreferences
                    SharedPreferences pref = getSharedPreferences("produk_pref", MODE_PRIVATE);
                    pref.edit()
                            .putInt("id_produk", idProduk)
                            .putInt("id_pemilikwarung", pemilikId)
                            .putString("nama_produk", namaProduk)
                            .putString("deskripsi", deskripsi)
                            .putString("kategori",  kategori)
                            .apply();

                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Produk dikirim! ID = " + idProduk + ", status = " + status,
                                    Toast.LENGTH_SHORT).show());

                } else {
                    Log.e("HTTP_ERROR", "Code " + code + " – " + conn.getResponseMessage());
                }
            } catch (Exception e) {
                Log.e("HTTP_ERROR", "Gagal kirim produk", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /* -------------------------------------------------------
       4.  Kirim Pesan Chat
       ------------------------------------------------------- */
    private void kirimPesan(String isiPesan) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PESAN);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject()
                        .put("id_pengirim", penitipId)
                        .put("id_penerima", pemilikId)
                        .put("isi_pesan",  isiPesan);

                try (OutputStream os = conn.getOutputStream()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK ||
                        conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    runOnUiThread(this::getChatMessages);
                } else {
                    Log.e("KIRIM_CHAT", "Gagal kirim, code: " + conn.getResponseCode());
                }
            } catch (Exception e) {
                Log.e("KIRIM_CHAT", "Error kirim pesan", e);
            }
        });
    }

    /* -------------------------------------------------------
       5.  Ambil Pesan
       ------------------------------------------------------- */
    private void getChatMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            HttpURLConnection conn = null;
            try {
                String urlStr = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PESAN +
                        "/" + pemilikId + "/" + penitipId;
                conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestMethod("GET");

                StringBuilder sb = new StringBuilder();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                    }
                }

                JSONArray arr = new JSONArray(sb.toString());
                chatList.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    ChatMessage m = new ChatMessage();
                    m.setIdPengirim(o.getInt("id_pengirim"));
                    m.setIdPenerima(o.getInt("id_penerima"));
                    m.setIsiPesan(o.getString("isi_pesan"));
                    m.setIdLampiran(o.optInt("id_lampiran"));
                    chatList.add(m);
                }

                runOnUiThread(() -> chatAdapter.notifyDataSetChanged());
            } catch (Exception e) {
                Log.e("CHAT_ERROR", "Gagal ambil data", e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    /* -------------------------------------------------------
       6.  Lifecycle → start/stop polling
       ------------------------------------------------------- */
    @Override protected void onResume()   {
        super.onResume();
        handler.post(refreshChatRunnable);
        ConstantsVariabels.hideSystemUI(getWindow());
    }
    @Override protected void onPause()    {
        super.onPause();
        handler.removeCallbacks(refreshChatRunnable);
        ConstantsVariabels.hideSystemUI(getWindow());
    }
    @Override protected void onDestroy()  {
        super.onDestroy();
        handler.removeCallbacks(refreshChatRunnable);
        ConstantsVariabels.hideSystemUI(getWindow());
    }

}
