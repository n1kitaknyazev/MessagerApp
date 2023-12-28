package com.example.messagerapp.messages;

public class MessagesList {

    private String name, phone, lastMessage, profileImg, chatKey;

    private int unseenMessages;

    public MessagesList(String name, String phone, String lastMessage, String profileImg, int unseenMessages, String chatKey) {
        this.name = name;
        this.phone = phone;
        this.lastMessage = lastMessage;
        this.profileImg = profileImg;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }
}
