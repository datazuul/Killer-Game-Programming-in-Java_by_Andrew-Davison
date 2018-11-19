package com.example.chat.threaded.chatServlet;

import java.util.ArrayList;

public class ChatGroup {

    private ArrayList chatUsers;
    private ArrayList messages;
    private int numUsers;

    public ChatGroup() {
        chatUsers = new ArrayList();
        messages = new ArrayList();
        numUsers = 0;
    }

    synchronized public int addUser(String name) {
        if (numUsers == 0) {
            messages.clear();
        }

        if (isUniqueName(name)) {
            Chatter chatter = new Chatter(name);
            chatUsers.add(chatter);
            messages.add("(" + name + ") has arrived");
            numUsers++;
            return chatter.getUserId();
        }
        return -1;
    }

    private boolean isUniqueName(String name) {
        Chatter chatter;
        for (int i = 0; i < chatUsers.size(); i++) {
            chatter = (Chatter) chatUsers.get(i);
            if (chatter.getUserName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    synchronized public boolean deleteUser(String name, int userId) {
        if (userId == -1) {
            return false;
        }

        Chatter chatter;
        for (int i = 0; i < chatUsers.size(); i++) {
            chatter = (Chatter) chatUsers.get(i);
            if (chatter.matches(name, userId)) {
                chatUsers.remove(i);
                messages.add("(" + name + ") has departed");
                numUsers--;
                return true;
            }
        }
        return false;
    }

    private Chatter findUser(String name, int userId) {
        if (userId == -1) {
            return null;
        }

        Chatter chatter;
        for (int i = 0; i < chatUsers.size(); i++) {
            chatter = (Chatter) chatUsers.get(i);
            if (chatter.matches(name, userId)) {
                return chatter;
            }
        }
        return null;
    }

    synchronized public boolean storeMessage(String name, int userId, String message) {
        Chatter chatter = findUser(name, userId);
        if (chatter != null) {
            messages.add("(" + name + ") " + message);
            return true;
        }
        return false;
    }

    synchronized public String read(String name, int userId) {
        StringBuffer stringBuffer = new StringBuffer();
        Chatter chatter = findUser(name, userId);

        if (chatter != null) {
            int messageIndex = chatter.getMessageIndex();
            String message;
            for (int i = messageIndex; i < messages.size(); i++) {
                message = (String) messages.get(i);
                if (isVisibleMessage(message, name)) {
                    stringBuffer.append(message + "\n");
                }
            }
            chatter.setMessageIndex(messages.size());
        }
        return stringBuffer.toString();
    }

    private boolean isVisibleMessage(String message, String name) {
        int index = message.indexOf("/");
        if (index == -1) {
            return true;
        }

        String toName = message.substring(index + 1).trim();
        if (toName.equals(name)) {
            return true;
        } else {
            if (message.startsWith("(" + name)) {
                return true;
            } else {
                return false;
            }
        }
    }

    synchronized public String who() {
        Chatter chatter;
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < chatUsers.size(); i++) {
            chatter = (Chatter) chatUsers.get(i);
            stringBuffer.append("" + (i + 1) + ". " + chatter.getUserName() + "\n");
        }
        return stringBuffer.toString();
    }
}
