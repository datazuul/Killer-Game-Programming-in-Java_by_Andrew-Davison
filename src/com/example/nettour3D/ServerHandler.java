package com.example.nettour3D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerHandler extends Thread {

    private Socket clientSocket;
    private String clientAddress;
    private String userName;
    private int port;

    private TourGroup tourGroup;

    public ServerHandler(Socket clientSocket, TourGroup tourGroup) {
        this.clientSocket = clientSocket;
        this.tourGroup = tourGroup;
        userName = "?";
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        port = clientSocket.getPort();
        System.out.println("Client connection from ( " + clientAddress + ", " + port + ")");
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            tourGroup.addPerson(clientAddress, port, printWriter);

            processClient(bufferedReader, printWriter);

            tourGroup.deletePerson(clientAddress, port, userName + " bye");
            clientSocket.close();
            System.out.println("Client " + userName + " (" + clientAddress + ", " + port + ") connection closed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processClient(BufferedReader bufferedReader, PrintWriter printWriter) {
        String line;
        boolean isDone = false;
        try {
            while (!isDone) {
                if ((line = bufferedReader.readLine()) == null) {
                    isDone = true;
                } else {
                    if (line.trim().equals("bye")) {
                        isDone = true;
                    } else {
                        doRequest(line.trim(), printWriter);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void doRequest(String line, PrintWriter printWriter) {
        if (line.startsWith("create")) {
            sendCreate(line);
        } else if (line.startsWith("detailsFor")) {
            sendDetails(line);
        } else {
            tourGroup.broadcast(clientAddress, port, userName + " " + line);
        }
    }

    private void sendCreate(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        userName = tokenizer.nextToken();
        String xPosition = tokenizer.nextToken();
        String zPosition = tokenizer.nextToken();

        tourGroup.broadcast(clientAddress, port, "want details " + clientAddress + " " + port);
        tourGroup.broadcast(clientAddress, port, "create " + userName + " " + xPosition + " " + zPosition);
    }

    private void sendDetails(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        String toAddress = tokenizer.nextToken();
        int toPort = Integer.parseInt(tokenizer.nextToken());
        String xPosition = tokenizer.nextToken();
        String zPosition = tokenizer.nextToken();
        String rotationRadians = tokenizer.nextToken();

        tourGroup.sendTo(toAddress, toPort, "details for " + userName + " " + xPosition + " " + zPosition +
                " " + rotationRadians);
    }
}
