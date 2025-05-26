package com.example.jagajajan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class DetailWarungActivity extends AppCompatActivity {

    TextView namaWarung, alamat, no_hp, email, jam_buka, jam_tutup, tvAlamat, tvJamOprasi, jenisWarung;
    ImageButton btnChat;
    ImageView btnBack, arrow;
    LinearLayout detailInfo, detailPeroduk;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_warung);
        requestQueue = Volley.newRequestQueue(this);

        // Inisialisasi TextView dari layout
        namaWarung = findViewById(R.id.tv_nama_warung);
        alamat = findViewById(R.id.tv_alamat_warung);
        no_hp = findViewById(R.id.tv_no_hp);
        email = findViewById(R.id.tv_email);
        jam_buka = findViewById(R.id.tv_jam_buka);
        jam_tutup = findViewById(R.id.tv_jam_tutup);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvJamOprasi = findViewById(R.id.tv_jam_oprasi);


        btnChat = findViewById(R.id.btn_chat);
        btnBack =findViewById(R.id.btn_back);
        detailInfo = findViewById(R.id.detail_informasi_warung);
        detailPeroduk = findViewById(R.id.card_produk2);
        arrow = findViewById(R.id.arrow);
        jenisWarung = findViewById(R.id.jenis_warung);

        detailInfo.setOnClickListener(view -> {
            if (detailPeroduk.getVisibility() == View.GONE) {
                detailPeroduk.setVisibility(View.VISIBLE);
                arrow.setImageResource(R.drawable.ic_arrow_up);

            } else {
                detailPeroduk.setVisibility(View.GONE);
                arrow.setImageResource(R.drawable.ic_arrow_down);
            }
        });


        ViewUtils.setImageViewOnClickListener(btnBack, this, Home.class);


        // Ambil data dari Intent
        Intent intent = getIntent();
        String idPemilik = getIntent().getStringExtra("id_pemilik");
        if (idPemilik != null) {
            CallData(idPemilik);
        } else {
            Toast.makeText(this, "kembali ke home dan masuk lagi", Toast.LENGTH_SHORT).show();
        }

        Bundle bundle = new Bundle();
        bundle.putString("pemilik_warung_id", idPemilik);
        ViewUtils.setButton(btnChat, this, ChatActivity.class, bundle);

        namaWarung.setText(intent.getStringExtra("nama_warung"));
        jenisWarung.setText(intent.getStringExtra("jenis_warung"));
        alamat.setText("alamat: "+intent.getStringExtra("alamat"));
        tvAlamat.setText(intent.getStringExtra("alamat"));

        jam_buka.setText("Buka:   " + intent.getStringExtra("jam_buka"));
        jam_tutup.setText("Tutup:   " + intent.getStringExtra("jam_tutup"));
        tvJamOprasi.setText(intent.getStringExtra("jam_buka")+" - "+intent.getStringExtra("jam_tutup"));
    }
    private void CallData(String id) {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENDPOINT_USER + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String noHpUser = "no hp:   " + data.getString("no_hp");
                        String emailUser ="email:   " + data.getString("email");

                        no_hp.setText(noHpUser);
                        email.setText(emailUser);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Gagal ambil data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        // tambahkan request ke queue langsung
        requestQueue.add(jsonObjectRequest);
    }

}
