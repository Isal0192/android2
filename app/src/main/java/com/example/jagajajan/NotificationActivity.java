package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.adapter.NotificationAdapter;
import com.example.jagajajan.model.NotificationItem;
import com.example.jagajajan.utils.ConstantsVariabels;
import com.example.jagajajan.utils.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private ImageView previousPage, messageNfn;
    private RecyclerView recyclerViewPenitipan, recyclerViewChat;
    private NotificationAdapter penitipanAdapter, chatAdapter;
    private List<NotificationItem> penitipanList, chatList;
    private BottomNavigationView bottomNavigationView;
    private RequestQueue requestQueue;

    private static final String TAG = "NotificationActivity";

    // URL untuk notifikasi, pastikan ini valid
    private static final String URL_CHAT_NOTIFICATIONS = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_NOTIVICATION;
//    private static final String URL_STATUS_PENITIPAN = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_STATUS_PENITIPAN;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Inisialisasi view
        previousPage = ViewUtils.findViewById(this, R.id.previous_pagenfn);
        messageNfn = ViewUtils.findViewById(this, R.id.message_nfn);
        recyclerViewPenitipan = ViewUtils.findViewById(this, R.id.recycler_view_notofication);
        recyclerViewChat = ViewUtils.findViewById(this, R.id.recycler_view_chat);
        bottomNavigationView = ViewUtils.findViewById(this, R.id.bottom_navigation);

        requestQueue = Volley.newRequestQueue(this);
        ConstantsVariabels.hideSystemUI(getWindow());

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("id", null);

        if (currentUserId == null) {
            Toast.makeText(this, "User ID tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show();
            // Arahkan user ke halaman login jika perlu
            finish();
            return;
        }

        // Inisialisasi data & adapter
        penitipanList = new ArrayList<>();
        chatList = new ArrayList<>();

        penitipanAdapter = new NotificationAdapter(this, penitipanList);
        recyclerViewPenitipan.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPenitipan.setAdapter(penitipanAdapter);

        chatAdapter = new NotificationAdapter(this, chatList);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        // Navigasi
        ViewUtils.setImageViewOnClickListener(previousPage, this, Home.class);
        ViewUtils.setImageViewOnClickListener(messageNfn, this, ChatActivity.class);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    return true;
                case R.id.nav_search:
                    // Implementasi pencarian jika ada
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), PerofileActivity.class));
                    return true;
                default:
                    return false;
            }
        });


        fetchChatNotifications(currentUserId);
    }

    private void fetchChatNotifications(String userId) {
        String url = URL_CHAT_NOTIFICATIONS + "?id_pengguna=" + userId + "&unread=true";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    chatList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int penerimaId = obj.optInt("id_pengguna");
                            int chatId = obj.optInt("-");
                            int pengirimId = obj.optInt("id_pengirim");
                            String senderName = obj.optString("isi");
                            String lastMessage = obj.optString("judul");
                            String timestamp = obj.optString( "waktu_dibuat");
                            String relatedChatId = obj.optString( "id_notifikasi");

                            chatList.add(new NotificationItem(senderName, lastMessage, timestamp, penerimaId, chatId , pengirimId));
                        }
                        chatAdapter.notifyDataSetChanged();

                        if (chatList.isEmpty()) {
                            Toast.makeText(this, "Tidak ada notifikasi chat baru.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for chat: " + e.getMessage());
                        Toast.makeText(this, "Gagal memproses notifikasi chat.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error fetching chat: " + error.getMessage());
                    Toast.makeText(this, "Gagal mengambil notifikasi chat.", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(request);
    }


//    private void fetchPenitipanNotifications(String userId) {
//        String url = URL_CHAT_NOTIFICATIONS + "/" + userId;
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    chatList.clear();
//                    try {
//                        for (int i = 0; i < response.length(); i++) {
//                            JSONObject obj = response.getJSONObject(i);
//                            String senderName = obj.optString("senderName", "Pengirim");
//                            String lastMessage = obj.optString("lastMessage", "Pesan baru.");
//                            String timestamp = obj.optString("timestamp", "");
////                            chatList.add(new NotificationItem("Chat dari " + senderName, lastMessage, timestamp));
//                        }
////                        penitipAdapter.notifyDataSetChanged();
//
//                        if (chatList.isEmpty()) {
//                            // Tampilkan indikator tidak ada pesan
//                        }
//                    } catch (JSONException e) {
//                        Log.e(TAG, "JSON Error Chat: " + e.getMessage());
//                        Toast.makeText(this, "Gagal memproses notifikasi Penitip.", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    Log.e(TAG, "Volley Error Chat: " + error.getMessage());
//                    Toast.makeText(this, "Gagal mengambil notifikasi penitip.", Toast.LENGTH_SHORT).show();
//                }
//        );
//
//        requestQueue.add(request);
//    }
}
