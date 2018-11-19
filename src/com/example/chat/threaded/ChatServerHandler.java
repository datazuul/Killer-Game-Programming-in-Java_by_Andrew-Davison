package com.example.chat.threaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerHandler extends Thread {

    private Socket clientSocket;
    private String clientAddress;
    private int port;

    private ChatGroup chatGroup;

    public ChatServerHandler(Socket clientSocket, ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
        this.clientSocket = clientSocket;
        clientAddress = clientSocket.getInetAddress().getHostAddress();
        port = clientSocket.getPort();
        System.out.println("Client connection from (" + clientAddress + ", " + port + ")");
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            chatGroup.addPerson(clientAddress, port, out);
            processClient(in, out);

            chatGroup.deletePerson(clientAddress, port);
            clientSocket.close();
            System.out.println("Client (" + clientAddress + ", " + port + ") connection closed\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processClient(BufferedReader bufferedReader, PrintWriter printWriter) {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = bufferedReader.readLine()) == null) {
                    done = true;
                } else {
                    System.out.println("Client (" + clientAddress + ", " + port + ") : " + line);

                    if (line.trim().equals("bye")) {
                        done = true;
                    } else {
                        doRequest(line, printWriter);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void doRequest(String line, PrintWriter printWriter) {
        if (line.trim().toLowerCase().equals("who")) {
            System.out.println("Processing 'who'");
            printWriter.println(chatGroup.who());
        } else {
            chatGroup.broadcast("(" + clientAddress + ", " + port + ") : " + line);
        }
    }
}
