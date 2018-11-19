package com.example.chat.threaded;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    private static final int PORT = 1234;

    private ChatGroup chatGroup;

    public ChatServer() {
        chatGroup = new ChatGroup();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket clientSocket;

            while (true) {
                System.out.println("Waiting for a client...");
                clientSocket = serverSocket.accept();
                new ChatServerHandler(clientSocket, chatGroup).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
