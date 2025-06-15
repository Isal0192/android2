package com.example.jagajajan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.jagajajan.DetailPenitipan;
import com.example.jagajajan.R;
import com.example.jagajajan.model.Titipan;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TitipanAdapter extends RecyclerView.Adapter<TitipanAdapter.TitipanViewHolder> {
    private Context context;
    private List<Titipan> titipanList;
    RequestQueue queue;
    // Set untuk menyimpan ID produk yang penjualannya sudah dicatat
    private Set<String> recordedProductIds = new HashSet<>();

    public TitipanAdapter(Context context, List<Titipan> titipanList) {
        this.context = context;
        this.titipanList = titipanList;
    }

    @NonNull
    @Override
    public TitipanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_titipan, parent, false);
        return new TitipanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TitipanViewHolder holder, int position) {
        Titipan titipan = titipanList.get(position);

        holder.itemNameTextView.setText(titipan.getNamaProduk());
        holder.itemHargaBeliTextView.setText("Harga Beli: Rp " + titipan.getHarga());
        holder.itemHargaJualTextView.setText("Harga Jual: Rp " + titipan.getHargaJual());
        holder.itemStokTextView.setText("Stok: " + titipan.getStok());
        String tangal = titipan.getGantiProduk().split("T")[0];
        holder.itemGantiProdukTextView.setText("Ganti Produk: " + tangal);

        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailPenitipan.class);
            Log.d("TitipanAdapter", "Item diklik: " + titipan.getNamaProduk());

            intent.putExtra("id_produk", titipan.getId());
            intent.putExtra("nama_produk", titipan.getNamaProduk());
            intent.putExtra("harga_beli", titipan.getHarga());
            intent.putExtra("harga_jual", titipan.getHargaJual());
            intent.putExtra("stok_awal", titipan.getStok());
            intent.putExtra("ganti_produk", titipan.getGantiProduk());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });


        if (titipan.getFotoBarang() != null && !titipan.getFotoBarang().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(titipan.getFotoBarang(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.itemImageView.setImageBitmap(bitmap);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.itemImageView.setImageResource(R.drawable.background_rauded_white);
            }
        } else {
            holder.itemImageView.setImageResource(R.drawable.gambar_logo);
        }
    }

    @Override
    public int getItemCount() {
        return titipanList.size();
    }


    public static class TitipanViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView itemNameTextView;
        TextView itemHargaBeliTextView;
        TextView itemHargaJualTextView;
        TextView itemStokTextView;
        TextView itemGantiProdukTextView;
        LinearLayout card;


        public TitipanViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);

            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemHargaBeliTextView = itemView.findViewById(R.id.itemHargaBeliTextView);
            itemHargaJualTextView = itemView.findViewById(R.id.itemHargaJualTextView);
            itemStokTextView = itemView.findViewById(R.id.itemStokTextView);
            itemGantiProdukTextView = itemView.findViewById(R.id.itemGantiProdukTextView);
            card = itemView.findViewById(R.id.card_details);
        }
    }

    public void updateData(List<Titipan> newList) {
        titipanList.clear();
        titipanList.addAll(newList);
        recordedProductIds.clear();
        notifyDataSetChanged();
    }
}