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
import com.example.jagajajan.adapter.DataAdapter;
import com.example.jagajajan.model.ItemsContens;
import com.example.jagajajan.model.WarungData;

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
    private List<WarungData> listWarung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ConstantsVariabels.hideSystemUI(getWindow());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        perofile = findViewById(R.id.profile);

        recyclerView = findViewById(R.id.recycler_view_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        dataItemList = new ArrayList<>();
        dataAdapter = new DataAdapter(this, dataItemList);
        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pindahKeDetailWarung(position);
            }
        });
        recyclerView.setAdapter(dataAdapter);

        requestQueue = Volley.newRequestQueue(this);


        perofile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PerofileActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent1 = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_search:
                        return true;
                    case R.id.nav_profile:
                        Intent intent3 = new Intent(getApplicationContext(), PerofileActivity.class);
                        startActivity(intent3);
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
                        listWarung = parseJson(response);
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
                warung.setId_warung(jsonObject.getString("id_warung"));
                warung.setId_pemilik(jsonObject.getString("id_pemilik"));
                warung.setNama_warung(jsonObject.getString("nama_warung"));
                warung.setFoto_warung_url(jsonObject.getString("foto_warung"));
                warung.setAlamat(jsonObject.getString("alamat"));
                warung.setJenis_warung(jsonObject.getString("jenis_warung"));
                warung.setNo_bisnis(jsonObject.getString("no_bisnis"));
                warung.setJam_buka(jsonObject.getString("jam_buka"));
                warung.setJam_tutup(jsonObject.getString("jam_tutup"));
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
            String waktuOperasi = warung.getJam_buka()+ " - " + warung.getJam_tutup();

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

            detailIntent.putExtra("id_warung", warung.getId_warung());
            detailIntent.putExtra("id_pemilik", warung.getId_pemilik());
            detailIntent.putExtra("nama_warung", warung.getNama_warung());
            detailIntent.putExtra("foto_warung", warung.getFoto_warung_url());
            detailIntent.putExtra("alamat", warung.getAlamat());
            detailIntent.putExtra("jenis_warung", warung.getJenis_warung());
            detailIntent.putExtra("no_bisnis", warung.getNo_bisnis());
            detailIntent.putExtra("jam_buka", warung.getJam_buka());
            detailIntent.putExtra("jam_tutup", warung.getJam_tutup());

            Log.d("INTENT_DATA", "ID Pemilik: " + warung.getId_pemilik());
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
