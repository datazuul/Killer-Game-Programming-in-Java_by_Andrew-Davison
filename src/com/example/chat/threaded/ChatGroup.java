package com.example.chat.threaded;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ChatGroup {

    private ArrayList chatPeople;

    public ChatGroup() {
        chatPeople = new ArrayList();
    }

    synchronized public void addPerson(String clientAddress, int port, PrintWriter printWriter) {
        chatPeople.add(new Chatter(clientAddress, port, printWriter));
        broadcast("Welcome a new chatter (" + clientAddress + ", " + port + ")");
    }

    synchronized public void deletePerson(String clientAddress, int port) {
        Chatter chatter;
        for (int i = 0; i < chatPeople.size(); i++) {
            chatter = (Chatter) chatPeople.get(i);
            if (chatter.matches(clientAddress, port)) {
                chatPeople.remove(i);
                broadcast("(" + clientAddress + ", " + port + ") has departed");
                break;
            }
        }
    }

    synchronized public void broadcast(String message) {
        Chatter chatter;
        for (int i = 0; i < chatPeople.size(); i++) {
            chatter = (Chatter) chatPeople.get(i);
            chatter.sendMessage(message);
        }
    }

    synchronized public String who() {
        Chatter chatter;
        String whoList = "WHO$$";
        for (int i = 0; i < chatPeople.size(); i++) {
            chatter = (Chatter) chatPeople.get(i);
            whoList += chatter.toString();
        }
        return whoList;
    }
}
