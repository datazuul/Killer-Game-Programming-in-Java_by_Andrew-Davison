package com.example.networkingbasics.threaded;

import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedScoreServer {

    private static final int PORT = 1234;
    private HighScores highScores;

    public ThreadedScoreServer() {
        highScores = new HighScores();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket clientSocket;
            String clientAddress;

            while (true) {
                System.out.println("Waiting for a client...");
                clientSocket = serverSocket.accept();
                clientAddress = clientSocket.getInetAddress().getHostAddress();
                new ThreadedScoreHandler(clientSocket, clientAddress, highScores).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new ThreadedScoreServer();
    }
}
