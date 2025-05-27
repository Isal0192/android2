package com.example.jagajajan.model;

public class ItemsContens {
    private final String nama;
    private final String deskripsi;
    private final String time;

    public  ItemsContens(String nama, String deskripsi,String time){
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.time = time;
    }
    public String getNama() {
        return nama;
    }
    public String getDeskripsi() {
        return deskripsi;
    }
    public String getTime() {
        return time;
    }

}
