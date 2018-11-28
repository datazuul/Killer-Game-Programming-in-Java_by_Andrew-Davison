package com.example.nettour3D;

import java.io.PrintWriter;

public class TouristInfo {

    String clientAddress;
    int port;
    PrintWriter printWriter;

    public TouristInfo(String clientAddress, int port, PrintWriter printWriter) {
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
}
