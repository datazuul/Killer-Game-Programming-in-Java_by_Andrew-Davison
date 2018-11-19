package com.example.chat.threaded;

import java.io.PrintWriter;

public class Chatter {

    private String clientAddress;
    private int port;
    private PrintWriter printWriter;

    public Chatter(String clientAddress, int port, PrintWriter printWriter) {
        this.clientAddress = clientAddress;
        this.port = port;
        this.printWriter = printWriter;
    }

    public boolean matches(String clientAddress, int port) {
        if (this.clientAddress.equals(clientAddress) && (this.port == port)) {
            return true;
        }
        return false;
    }

    public void sendMessage(String message) {
        printWriter.println(message);
    }

    public String toString() {
        return clientAddress + " & " + port + " & ";
    }
}
