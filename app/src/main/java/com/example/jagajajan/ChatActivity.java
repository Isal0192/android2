package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jagajajan.adapter.ChatAdapter;
import com.example.jagajajan.model.ChatMessage;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatList = new ArrayList<>();

    private int penitipId;
    private int pemilikId;

    private EditText editTextPesan;
    private ImageView buttonKirim;
    private ImageView btnBack;
    private TextView title;

    private final int REFRESH_INTERVAL = 2000; // 2 seconds
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

    private void initViews() {
        editTextPesan = findViewById(R.id.Pesan_chat);
        buttonKirim = findViewById(R.id.button_kirim);
        btnBack = findViewById(R.id.btn_back);
        title = findViewById(R.id.title);

        ViewUtils.setImageViewOnClickListener(btnBack, this, DetailWarungActivity.class);
    }

    private void getIntentData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        penitipId = Integer.parseInt(sharedPreferences.getString("id", "0"));

        Intent intent = getIntent();
        String idStr = intent.getStringExtra("id");
        pemilikId = Integer.parseInt(idStr);

        title.setText(intent.getStringExtra("nama"));
    }

    private void initRecyclerView() {
        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, chatList, penitipId);
        recyclerChat.setAdapter(chatAdapter);
    }

    private void setupChatPolling() {
        refreshChatRunnable = () -> {
            getChatMessages();
            handler.postDelayed(refreshChatRunnable, REFRESH_INTERVAL);
        };
    }

    private void setupSendButton() {
        buttonKirim.setOnClickListener(view -> {
            String isiPesan = editTextPesan.getText().toString().trim();
            if (!isiPesan.isEmpty()) {
                kirimPesan(isiPesan);
                editTextPesan.setText("");
            }
        });
    }

    private void sendPesanOtomatisJikaAda() {
        Intent intent = getIntent();
        String namaProduk = intent.getStringExtra("namaProduk");
        String deskripsi = intent.getStringExtra("deskripsi");
        String kategori = intent.getStringExtra("kategori");
        Log.d("INTENT_DATA", "namaProduk: " + namaProduk + ", deskripsi: " + deskripsi + ", kategori: " + kategori);


        new Thread(() -> {
            try {
                URL url = new URL(ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_KIRIM_PENGAJUAN);
                Log.d("POST_URL", url.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id_penitip", penitipId);
                jsonParam.put("id_penerima", pemilikId);
                jsonParam.put("nama_produk", namaProduk);
                jsonParam.put("deskripsi", deskripsi);
                jsonParam.put("kategori", kategori);


                conn.getOutputStream().write(jsonParam.toString().getBytes("UTF-8"));
                int responseCode = conn.getResponseCode();
                Log.d("HTTP_RESPONSE", "Code: " + responseCode);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void kirimPesan(String isiPesan) {
        new Thread(() -> {
            try {
                URL url = new URL(ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PESAN);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id_pengirim", penitipId);
                jsonParam.put("id_penerima", pemilikId);
                jsonParam.put("isi_pesan", isiPesan);

                conn.getOutputStream().write(jsonParam.toString().getBytes("UTF-8"));

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    runOnUiThread(this::getChatMessages);
                } else {
                    Log.e("KIRIM_CHAT", "Gagal kirim. Kode: " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("KIRIM_CHAT", "Error kirim pesan: " + e.getMessage());
            }
        }).start();
    }

    private void getChatMessages() {
        new Thread(() -> {
            try {
                String urlString = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PESAN + "/" + pemilikId + "/" + penitipId;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONArray array = new JSONArray(builder.toString());
                chatList.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    ChatMessage message = new ChatMessage();
                    message.setIdPengirim(obj.getInt("id_pengirim"));
                    message.setIdPenerima(obj.getInt("id_penerima"));
                    message.setIsiPesan(obj.getString("isi_pesan"));
                    message.setIdLampiran(obj.optInt("id_lampiran"));
                    chatList.add(message);
                }

                runOnUiThread(() -> chatAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e("CHAT_ERROR", "Gagal ambil data: " + e.getMessage());
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startChatPolling();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopChatPolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopChatPolling();
    }

    private void startChatPolling() {
        handler.post(refreshChatRunnable);
    }

    private void stopChatPolling() {
        handler.removeCallbacks(refreshChatRunnable);
    }
}
