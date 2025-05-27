package com.example.jagajajan;

public class WarungData {
    private int id;
    private int id_user;
    private String jenis_warung;
    private String no_hp;
    private String email_bisnis;
    private String alamat;
    private String jam_buka;
    private String jam_tutup;
    private String foto_warung_url;

    public WarungData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getJenis_warung() {
        return jenis_warung;
    }

    public void setJenis_warung(String jenis_warung) {
        this.jenis_warung = jenis_warung;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }

    public String getEmail_bisnis() {
        return email_bisnis;
    }

    public void setEmail_bisnis(String email_bisnis) {
        this.email_bisnis = email_bisnis;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
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

    public String getFoto_warung_url() {
        return foto_warung_url;
    }

    public void setFoto_warung_url(String foto_warung_url) {
        this.foto_warung_url = foto_warung_url;
    }
}