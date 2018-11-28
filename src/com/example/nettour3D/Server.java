package com.example.nettour3D;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5555;

    private TourGroup tourGroup;

    public Server() {
        tourGroup = new TourGroup();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket;

            while (true) {
                System.out.println("Waiting for a client...");
                socket = serverSocket.accept();
                new ServerHandler(socket, tourGroup).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
