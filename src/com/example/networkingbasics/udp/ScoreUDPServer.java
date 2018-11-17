package com.example.networkingbasics.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ScoreUDPServer {

    private static final int PORT = 1234;
    private static final int BUFSIZE = 1024;

    private HighScores highScores;
    private DatagramSocket serverSocket;

    public ScoreUDPServer() {
        try {
            serverSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println(e);
            System.exit(1);
        }

        waitForPackets();
    }

    private void waitForPackets() {
        DatagramPacket datagramPacket;
        byte data[];

        highScores = new HighScores();

        try {
            while (true) {
                data = new byte[BUFSIZE];
                datagramPacket = new DatagramPacket(data, data.length);

                System.out.println("Waiting for a packet...");
                serverSocket.receive(datagramPacket);

                processClient(datagramPacket);
                highScores.saveScores();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void processClient(DatagramPacket datagramPacket) {
        InetAddress inetAddress = datagramPacket.getAddress();
        int clientPort = datagramPacket.getPort();
        String clientMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        System.out.println("Client packet from " + inetAddress + ", " + clientPort);
        System.out.println("Client message : " + clientMessage);

        doRequest(inetAddress, clientPort, clientMessage);
    }

    private void doRequest(InetAddress inetAddress, int clientPort, String clientMessage) {
        if (clientMessage.trim().toLowerCase().equals("get")) {
            System.out.println("Processing 'get'");
            sendMessage(inetAddress, clientPort, highScores.toString());
        } else if ((clientMessage.length() >= 6) &&
                (clientMessage.substring(0, 5).toLowerCase().equals("score"))) {
            System.out.println("Processing 'score'");
            highScores.addScore(clientMessage.substring(5));
        } else {
            System.out.println("Ignoring input line");
        }
    }

    private void sendMessage(InetAddress inetAddress, int clientPort, String message) {
        byte messageData[] = message.getBytes();
        try {
            DatagramPacket datagramPacket = new DatagramPacket(messageData, messageData.length, inetAddress, clientPort);
            serverSocket.send(datagramPacket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        new ScoreUDPServer();
    }
}
