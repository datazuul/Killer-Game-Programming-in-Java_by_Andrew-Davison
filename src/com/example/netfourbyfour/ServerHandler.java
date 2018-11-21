package com.example.netfourbyfour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler extends Thread {

    private Server server;
    private Socket clientSocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    private int playerID;

    public ServerHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println("Player connection request");
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void run() {
        playerID = server.addPlayer(this);
        if (playerID != -1) {
            sendMessage("ok " + playerID);
            System.out.println("ok " + playerID);
            server.tellOther(playerID, "added " + playerID);

            processPlayerInput();

            server.removePlayer(playerID);
            server.tellOther(playerID, "removed " + playerID);
        } else {
            sendMessage("full");
        }

        try {
            clientSocket.close();
            System.out.println("Player " + playerID + " connection closed\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processPlayerInput() {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = bufferedReader.readLine()) == null) {
                    done = true;
                } else {
                    if (line.trim().equals("disconnect")) {
                        done = true;
                    } else {
                        doRequest(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Player " + playerID + " closed the connection");
            System.out.println(e);
        }
    }

    private void doRequest(String line) {
        if (line.startsWith("try")) {
            try {
                int position = Integer.parseInt(line.substring(4).trim());

                if (server.enoughPlayers()) {
                    server.tellOther(playerID, "otherTurn " + playerID + " " + position);
                } else {
                    sendMessage("tooFewPlayers");
                }
            } catch (NumberFormatException e) {
                System.out.println(e);
            }
        }
    }

    synchronized public void sendMessage(String message) {
        try {
            printWriter.println(message);
        } catch (Exception e) {
            System.out.println("Handler for player " + playerID + "\n" + e);
        }
    }
}
