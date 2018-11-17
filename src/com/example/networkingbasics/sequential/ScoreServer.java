package com.example.networkingbasics.sequential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ScoreServer {

    private static final int PORT = 1234;
    private HighScores highScores;

    public ScoreServer() {
        highScores = new HighScores();
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket clientSocket;
            BufferedReader in;
            PrintWriter out;

            while (true) {
                System.out.println("Waiting for a client...");
                clientSocket = serverSocket.accept();
                System.out.println("Client connection from " + clientSocket.getInetAddress().getHostAddress());

                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                processClient(in, out);

                clientSocket.close();
                System.out.println("Client connection closed\n");
                highScores.saveScores();
            }
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

    public static void main(String[] args) {
        new ScoreServer();
    }
}
