package com.example.netfourbyfour;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 1234;

    private static final int MAX_PLAYERS = 2;
    private static final int PLAYER1 = 1;
    private static final int PLAYER2 = 2;

    private ServerHandler[] handlers;
    private int numPlayers;

    public Server() {
        handlers = new ServerHandler[MAX_PLAYERS];
        handlers[0] = null;
        handlers[1] = null;
        numPlayers = 0;

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket;
            while (true) {
                System.out.println("Waiting for a client...");
                socket = serverSocket.accept();
                new ServerHandler(socket, this).start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    synchronized public boolean enoughPlayers() {
        return (numPlayers == MAX_PLAYERS);
    }

    synchronized public int addPlayer(ServerHandler handler) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (handlers[i] == null) {
                handlers[i] = handler;
                numPlayers++;
                return i + 1;
            }
        }
        return -1;
    }

    synchronized public void removePlayer(int playerID) {
        handlers[playerID - 1] = null;
        numPlayers--;
    }

    synchronized public void tellOther(int playerID, String message) {
        int otherID = ((playerID == PLAYER1) ? PLAYER2 : PLAYER1);
        if (handlers[otherID - 1] != null) {
            handlers[otherID - 1].sendMessage(message);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
