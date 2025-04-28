package com.example.jagajajan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context context;
    private List<ItemsContens> dataList;

    public DataAdapter(Context context, List<ItemsContens> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contens, parent, false);
        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        ItemsContens currentItem = dataList.get(position);
        holder.textNama.setText(currentItem.getNama());
        holder.textDeskripsi.setText(currentItem.getDeskripsi());
        holder.jamOprasi.setText(currentItem.getTime());
        // Set data lain ke elemen UI di CardView jika ada
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView textNama;
        TextView textDeskripsi;
        TextView jamOprasi;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            textNama = itemView.findViewById(R.id.namaWarungTextView);
            textDeskripsi = itemView.findViewById(R.id.deskripsiTextView);
            jamOprasi = itemView.findViewById(R.id.jamOperasionalTextView);
            // Inisialisasi elemen UI lain dari item_data.xml jika ada
        }
    }
}