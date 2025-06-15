package com.example.jagajajan.adapter; // Sesuaikan dengan paket Anda

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jagajajan.R; // Sesuaikan dengan R Anda
import com.example.jagajajan.model.RiwayatTitipan; // Sesuaikan dengan paket model Anda

import java.util.List;

public class RiwayatTitipanAdapter extends RecyclerView.Adapter<RiwayatTitipanAdapter.ViewHolder> {

    private List<RiwayatTitipan> transaksiList;

    public RiwayatTitipanAdapter(List<RiwayatTitipan> transaksiList) {
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaksi_penitipan, parent, false); // Akan kita buat layout ini
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatTitipan transaksi = transaksiList.get(position);

        holder.tvNamaProduk.setText("Produk: " + transaksi.getNamaProdukTitipan());
        holder.tvNamaWarung.setText("Warung: " + transaksi.getNamaWarung() + " (" + transaksi.getNamaPemilikWarung() + ")");
        holder.tvJumlahTerjual.setText("Terjual: " + transaksi.getJumlahTerjual() + " unit");
        holder.tvTotalPenjualan.setText("Total: Rp " + transaksi.getTotalPenjualan());
        holder.tvPendapatanPenitip.setText("Pendapatan Anda: Rp " + transaksi.getPendapatanPenitip());
        holder.tvTanggalTransaksi.setText("Tanggal: " + transaksi.getFormattedTanggalTransaksi());
        holder.tvCatatan.setText("Catatan: " + transaksi.getCatatan());
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaProduk, tvNamaWarung, tvJumlahTerjual, tvTotalPenjualan,
                tvPendapatanPenitip, tvTanggalTransaksi, tvCatatan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaProduk = itemView.findViewById(R.id.tvNamaProdukTransaksi);
            tvNamaWarung = itemView.findViewById(R.id.tvNamaWarungTransaksi);
            tvJumlahTerjual = itemView.findViewById(R.id.tvJumlahTerjualTransaksi);
            tvTotalPenjualan = itemView.findViewById(R.id.tvTotalPenjualanTransaksi);
            tvPendapatanPenitip = itemView.findViewById(R.id.tvPendapatanPenitipTransaksi);
            tvTanggalTransaksi = itemView.findViewById(R.id.tvTanggalTransaksi);
            tvCatatan = itemView.findViewById(R.id.tvCatatanTransaksi);
        }
    }

    public void updateData(List<RiwayatTitipan> newTransaksiList) {
        this.transaksiList.clear();
        this.transaksiList.addAll(newTransaksiList);
        notifyDataSetChanged();
    }
}