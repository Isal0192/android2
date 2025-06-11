package com.example.jagajajan.model;

public class Titipan {
    private String id_titipan;
    private String namaProduk;
    private String harga;
    private String hargaJual;
    private int stok;
    private String gantiProduk;
    private String fotoBarang;

    public Titipan(String id_titipan, String namaProduk, String harga, String hargaJual, int stok, String gantiProduk, String fotoBarang) {
        this.id_titipan = id_titipan;
        this.namaProduk = namaProduk;
        this.harga = harga;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.gantiProduk = gantiProduk;
        this.fotoBarang = fotoBarang;
    }

    // Getter methods
    public String getId() { return id_titipan; }
    public String getNamaProduk() { return namaProduk; }
    public String getHarga() { return harga; }
    public String getHargaJual() { return hargaJual; }
    public int getStok() { return stok; }
    public String getGantiProduk() { return gantiProduk; }
    public String getFotoBarang() { return fotoBarang; }
}