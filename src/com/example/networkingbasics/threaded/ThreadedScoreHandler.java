package com.example.networkingbasics.threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadedScoreHandler extends Thread {

    private Socket clientSocket;
    private String clientAddress;
    private HighScores highScores;

    public ThreadedScoreHandler(Socket clientSocket, String clientAddress, HighScores highScores) {
        this.clientSocket = clientSocket;
        this.clientAddress = clientAddress;
        this.highScores = highScores;
        System.out.println("Client connection from " + clientAddress);
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            processClient(in, out);

            clientSocket.close();
            System.out.println("Client (" + clientAddress + ") connection closed\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processClient(BufferedReader in, PrintWriter out) {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    System.out.println("Client message : " + line);
                    if (line.trim().equals("bye")) {
                        done = true;
                    } else {
                        doRequest(line, out);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void doRequest(String line, PrintWriter out) {
        if (line.trim().toLowerCase().equals("get")) {
            System.out.println("Processing 'get");
            out.println(highScores.toString());
        } else if ((line.length() >= 6) && (line.substring(0, 5).toLowerCase().equals("score"))) {
            System.out.println("Processing 'score");
            highScores.addScore(line.substring(5));
        } else {
            System.out.println("Ignoring input line");
        }
    }
}
