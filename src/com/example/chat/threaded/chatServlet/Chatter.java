package com.example.chat.threaded.chatServlet;

public class Chatter {

    private static final int ID_MAX = 1024;

    private String userName;
    private int userId;
    private int messageIndex;

    public Chatter(String name) {
        userName = name;
        userId = (int) Math.round(Math.random() * ID_MAX);
        messageIndex = 0;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserId() {
        return userId;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public boolean matches(String name, int id) {
        return (userName.equals(name) && (userId == id));
    }
}
