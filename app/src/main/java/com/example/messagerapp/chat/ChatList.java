package com.example.messagerapp.chat;

public class ChatList {

    private String phone, name, message, date, time;

    public ChatList(String phone, String name, String message, String date, String time) {
        this.phone = phone;
        this.name = name;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
