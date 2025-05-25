package com.example.jagajajan;

import android.content.Context;
import android.content.Intent;
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
    private OnItemClickListener mListener; // Tambahkan listener

    public interface OnItemClickListener { // Definisikan interface
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { // Set listener
        this.mListener = listener;
    }

    public DataAdapter(Context context, List<ItemsContens> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contens, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        ItemsContens item = dataList.get(position);
        holder.judulTextView.setText(item.getNama());
        holder.deskripsiTextView.setText(item.getDeskripsi());
        holder.waktuTextView.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView judulTextView;
        TextView deskripsiTextView;
        TextView waktuTextView;

        public DataViewHolder(View itemView) {
            super(itemView);
            judulTextView = itemView.findViewById(R.id.namaWarungTextView);
            deskripsiTextView = itemView.findViewById(R.id.deskripsiTextView);
            waktuTextView = itemView.findViewById(R.id.jamOperasionalTextView);
            itemView.setOnClickListener(this); // Set listener untuk item view
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition()); // Panggil listener
            }
        }
    }
}