package com.example.jagajajan.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jagajajan.FormTitipan;
import com.example.jagajajan.R;
import com.example.jagajajan.model.ChatMessage;
import com.example.jagajajan.utils.ConstantsVariabels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final String idUserString;
    private final Context context;
    private final List<ChatMessage> chatList;
    private final int userId;
    private final RequestQueue requestQueue;

    public ChatAdapter(Context context, List<ChatMessage> chatList, int userId) {
        this.context = context;
        this.chatList = chatList;
        this.userId = userId;

        SharedPreferences profilePref = context.getSharedPreferences("user_pref", MODE_PRIVATE);
        this.idUserString = profilePref.getString("id", "0");


        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chat = chatList.get(position);
        if (chat.getIdLampiran() != 0) return 2;
        return (chat.getIdPengirim() == userId) ? 1 : 0;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = (viewType == 2)
                ? R.layout.item_chat_acivment
                : (viewType == 1 ? R.layout.item_chat_sent : R.layout.item_chat_received);

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ChatViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);
        holder.textMessage.setText(message.getIsiPesan());

        int idPemilik = message.getIdPenerima();
        int viewType = getItemViewType(position);
        int idUserParsed = parseUserId(idUserString);

        if (viewType == 2 && idUserParsed == message.getIdPenerima()) {
            if (holder.actionLayout != null) {
                holder.actionLayout.setVisibility(View.VISIBLE);
                holder.buttonSetujui.setOnClickListener(v ->
                        updateStatusProduk(holder, message.getIdLampiran(), "disetujui"));
                holder.buttonTolak.setOnClickListener(v ->
                        updateStatusProduk(holder, message.getIdLampiran(), "ditolak"));
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("ProductPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id_lampiran", String.valueOf(message.getIdLampiran()));

        } else if (holder.actionLayout != null) {
            ambilStatusProduk(message.getIdLampiran(), holder.isipenitipan, holder.buttonSetujui, holder.pengajuanDiTolak, holder.buttonTolak, holder.actionLayout, message.getIdPenerima());
            holder.actionLayout.setVisibility(View.GONE);
        }
    }

    private void ambilStatusProduk(int idProduk,AppCompatButton isiPenitipan, AppCompatButton btnSetujui,AppCompatButton pengajuanDiTolak, AppCompatButton btnTolak, LinearLayout actionLayout, int pemilikID) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PRODUK + "/" + idProduk;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String status = response.getString("status");

                        if (status.equals("disetujui")) {
                            pengajuanDiTolak.setVisibility(View.GONE);
                            isiPenitipan.setVisibility(View.VISIBLE);
                            isiPenitipan.setOnClickListener(view -> {
                                Intent intent = new Intent(context, FormTitipan.class);
                                intent.putExtra("idProduk", idProduk);
                                intent.putExtra("idPemilik", pemilikID);
                                Log.d("chatAdapter","tes id produk: "+ idProduk );
                                startActivity(intent);
                            });
                        } else if (status.equals("ditolak")) {
                            isiPenitipan.setVisibility(View.GONE);
                            pengajuanDiTolak.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("STATUS_PRODUK", "Error: " + error.getMessage())
        );
        requestQueue.add(request);
    }

    private void startActivity(Intent intent) {
        context.startActivity(intent);
    }

    private int parseUserId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateStatusProduk(ChatViewHolder holder, int idProduk, String status) {
        if (idProduk <= 0) {
            Toast.makeText(context, "Produk belum tersimpan", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_PRODUK + "/" + idProduk;

        JSONObject body = new JSONObject();
        try {
            body.put("status", status);
        } catch (JSONException e) {
            Toast.makeText(context, "Error membuat data JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                response -> {
                    Toast.makeText(context, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    if (status.equals("disetujui")) updateButtonUI(holder);
                },
                error -> Toast.makeText(context, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/json");
                return h;
            }
        };

        requestQueue.add(request);
    }

    private void updateButtonUI(ChatViewHolder holder) {
        holder.buttonTolak.setVisibility(View.GONE);
        holder.buttonSetujui.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        holder.buttonSetujui.setText("Selamat! Klik ini untuk lanjutkan");
        holder.buttonSetujui.setPadding(20, 10, 20, 10);
        holder.buttonSetujui.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        AppCompatButton buttonSetujui, buttonTolak, isipenitipan, pengajuanDiTolak;
        LinearLayout actionLayout;

        public ChatViewHolder(View itemView, int viewType) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            if (viewType == 2) {
                pengajuanDiTolak = itemView.findViewById(R.id.pengajuan_ditolak);
                buttonSetujui = itemView.findViewById(R.id.button_setujui);
                buttonTolak = itemView.findViewById(R.id.button_tolak);
                actionLayout = itemView.findViewById(R.id.action);
                isipenitipan = itemView.findViewById(R.id.isi_penitipan);
            }
        }
    }
}
