package com.example.jagajajan.model;

public class ChatMessage {
    private int idPengirim;
    private int idPenerima;
    private String isiPesan;

    // Getter
    public int getIdPengirim() {
        return idPengirim;
    }

    public int getIdPenerima() {
        return idPenerima;
    }

    public String getIsiPesan() {
        return isiPesan;
    }

    // Setter
    public void setIdPengirim(int idPengirim) {
        this.idPengirim = idPengirim;
    }

    public void setIdPenerima(int idPenerima) {
        this.idPenerima = idPenerima;
    }

    public void setIsiPesan(String isiPesan) {
        this.isiPesan = isiPesan;
    }
}
