package com.example.jagajajan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView perofile;
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<ItemsContens> dataItemList;
    private RequestQueue requestQueue;
    private List<WarungData> listWarung; // Simpan data Warung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ConstantsVariabels.hideSystemUI(getWindow());

        // Inisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Inisialisasi ImageView untuk profil
        perofile = findViewById(R.id.profile);

        recyclerView = findViewById(R.id.recycler_view_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        dataItemList = new ArrayList<>();
        dataAdapter = new DataAdapter(this, dataItemList);
        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {  // Set listener
            @Override
            public void onItemClick(View view, int position) {
                // Panggil method untuk pindah ke detail
                pindahKeDetailWarung(position);
            }
        });
        recyclerView.setAdapter(dataAdapter);

        // Inisialisasi Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", null);
        String id = sharedPreferences.getString("id", null);

        perofile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PerofileActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
//                        Toast.makeText(Home.this, "Home clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_search:
                        return true;
                    case R.id.nav_profile:
//                        Intent profileIntent = new Intent(Home.this, PerofileActivity.class);
//                        startActivity(profileIntent);
                        return true;
                }
                return false;
            }
        });

        callData();
    }

    private void callData() {
        String url = ConstantsVariabels.BASE_URL + ConstantsVariabels.ENPOINT_WARUNG;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());
                        listWarung = parseJson(response); // Simpan ke listWarung
                        prosesDataWarung(listWarung);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error: " + error.getMessage());
                Toast.makeText(Home.this, "Kesalahan jaringan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private List<WarungData> parseJson(JSONArray jsonArray) {
        List<WarungData> listWarung = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                WarungData warung = new WarungData();
                warung.setId(jsonObject.getInt("id"));
                warung.setId_user(jsonObject.getInt("id_user"));
                warung.setJenis_warung(jsonObject.getString("jenis_warung"));
                warung.setNo_hp(jsonObject.getString("no_hp"));
                warung.setEmail_bisnis(jsonObject.getString("email_bisnis"));
                warung.setAlamat(jsonObject.getString("alamat"));
                warung.setJam_buka(jsonObject.getString("jam_buka"));
                warung.setJam_tutup(jsonObject.getString("jam_tutup"));
                warung.setFoto_warung_url(jsonObject.getString("foto_warung_url"));
                listWarung.add(warung);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON Parse Error", "Error parsing JSON: " + e.getMessage());
            Toast.makeText(this, "Gagal memproses data dari server", Toast.LENGTH_SHORT).show();
        }
        return listWarung;
    }

    private void prosesDataWarung(List<WarungData> listWarung) {
        List<ItemsContens> itemsUntukCardView = new ArrayList<>();

        for (WarungData warung : listWarung) {
            String judul = warung.getJenis_warung();
            String deskripsi = warung.getAlamat();
            String waktuOperasi = warung.getJam_buka().substring(11, 16) + " - " + warung.getJam_tutup().substring(11, 16);

            ItemsContens itemCard = new ItemsContens(judul, deskripsi, waktuOperasi);
            itemsUntukCardView.add(itemCard);
        }
        this.listWarung = listWarung;
        tampilkanDataDiRecyclerView(itemsUntukCardView);
    }

    private void tampilkanDataDiRecyclerView(List<ItemsContens> data) {
        dataItemList.clear();
        dataItemList.addAll(data);
        dataAdapter.notifyDataSetChanged();
    }



    private void pindahKeDetailWarung(int position) {
        if (listWarung != null && position >= 0 && position < listWarung.size()) {
            WarungData warung = listWarung.get(position);
            Intent detailIntent = new Intent(Home.this, DetailWarungActivity.class);
            detailIntent.putExtra("id", warung.getId());
            detailIntent.putExtra("jenis_warung", warung.getJenis_warung());
            detailIntent.putExtra("no_hp", warung.getNo_hp());
            detailIntent.putExtra("email_bisnis", warung.getEmail_bisnis());
            detailIntent.putExtra("alamat", warung.getAlamat());
            detailIntent.putExtra("jam_buka", warung.getJam_buka());
            detailIntent.putExtra("jam_tutup", warung.getJam_tutup());
            detailIntent.putExtra("foto_warung_url", warung.getFoto_warung_url());
            startActivity(detailIntent);
        } else {
            Toast.makeText(this, "Warung tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ConstantsVariabels.hideSystemUI(getWindow());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ConstantsVariabels.hideSystemUI(getWindow());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantsVariabels.hideSystemUI(getWindow());
    }
}

