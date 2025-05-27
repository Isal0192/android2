package com.example.jagajajan.model;

public class WarungData {
    private String id_warung;
    private String id_pemilik;
    private String nama_warung;
    private String foto_warung_url;
    private String alamat;
    private String jenis_warung;
    private String no_bisnis;
    private String jam_buka;
    private String jam_tutup;

    public WarungData() {
    }

    public String getId_warung() {
        return id_warung;
    }

    public void setId_warung(String id_warung) {
        this.id_warung = id_warung;
    }

    public String getId_pemilik() {return id_pemilik;}

    public void setId_pemilik(String id_pemilik) {this.id_pemilik = id_pemilik;}

    public String getNama_warung() {
        return nama_warung;
    }

    public void setNama_warung(String nama_warung) {
        this.nama_warung = nama_warung;
    }

    public String getFoto_warung_url() {
        return foto_warung_url;
    }

    public void setFoto_warung_url(String foto_warung_url) {this.foto_warung_url = foto_warung_url;}

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJenis_warung() {
        return jenis_warung;
    }

    public void setJenis_warung(String jenis_warung) {
        this.jenis_warung = jenis_warung;
    }

    public String getNo_bisnis() {
        return no_bisnis;
    }

    public void setNo_bisnis(String no_bisnis) {
        this.no_bisnis = no_bisnis;
    }

    public String getJam_buka() {
        return jam_buka;
    }

    public void setJam_buka(String jam_buka) {
        this.jam_buka = jam_buka;
    }

    public String getJam_tutup() {
        return jam_tutup;
    }

    public void setJam_tutup(String jam_tutup) {
        this.jam_tutup = jam_tutup;
    }
}
