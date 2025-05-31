package com.example.jagajajan.model;

public class NotificationItem {
    private String title;
    private String message;
    private String timestamp;
    private int userId;
    private int pengirimId;
    private int chatId;



    public NotificationItem(String title, String message, String timestamp, int userId, int chatId, int pengirimId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
        this.chatId = chatId;
        this.pengirimId = pengirimId;
    }


    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public int getUserId() { return userId; }
    public int getChatId() { return chatId; }
    public int getPengirimId() { return pengirimId; }
}