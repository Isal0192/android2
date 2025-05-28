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
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    private int penitipId, pemilikId;
    TextView title;

    private final int REFRESH_INTERVAL = 3000; // Refresh every 3 seconds
    private Handler handler = new Handler();
    private Runnable refreshChatRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EditText editTextPesan = findViewById(R.id.Pesan_chat);
        ImageView buttonKirim = findViewById(R.id.button_kirim);
        ImageView btnBack = findViewById(R.id.btn_back);
        title = findViewById(R.id.title);

        ViewUtils.setImageViewOnClickListener(btnBack, this, DetailWarungActivity.class);

        recyclerChat = findViewById(R.id.recycler_chat);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        penitipId = Integer.parseInt(sharedPreferences.getString("id","0"));

        Intent intent = getIntent();
        String idStr = getIntent().getStringExtra("id");
        Log.d("ChatActivity", "Received id_pengguna: " + idStr);
        int userId = Integer.parseInt(idStr);
        pemilikId = userId;
        title.setText(intent.getStringExtra("nama"));


        chatAdapter = new ChatAdapter(this, chatList, penitipId);
        recyclerChat.setAdapter(chatAdapter);

        refreshChatRunnable = new Runnable() {
            @Override
            public void run() {
                getChatMessages();
                handler.postDelayed(this, REFRESH_INTERVAL); // Schedule again
            }
        };

        buttonKirim.setOnClickListener(view -> {
            String isiPesan = editTextPesan.getText().toString().trim();
            if (!isiPesan.isEmpty()) {
                kirimPesan(isiPesan);
                editTextPesan.setText(""); // Kosongkan input setelah kirim
            }
        });
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
                    runOnUiThread(() -> getChatMessages()); // Refresh chat setelah kirim
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
                String urlString = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PESAN +"/"+ pemilikId + "/" + penitipId;
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
        startChatPolling(); // Start polling when the activity is resumed
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopChatPolling(); // Stop polling when the activity is paused
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopChatPolling(); // Stop polling when the activity is destroyed
    }

    private void startChatPolling() {
        handler.post(refreshChatRunnable);
    }

    private void stopChatPolling() {
        handler.removeCallbacks(refreshChatRunnable);
    }
}

