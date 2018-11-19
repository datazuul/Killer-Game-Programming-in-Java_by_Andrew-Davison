package com.example.chat.threaded.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class NameServer {

    private static final String GROUP_HOST = "228.5.6.7";

    private static final int PORT = 1234;
    private static final int BUFSIZE = 1024;

    private DatagramSocket serverSocket;
    private ArrayList groupMembers;

    public NameServer() {
        try {
            serverSocket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            System.out.println(e);
            System.exit(1);
        }

        groupMembers = new ArrayList();
        waitForPackets();
    }

    private void waitForPackets() {
        DatagramPacket datagramPacket;
        byte data[];

        System.out.println("Ready for client messages");
        try {
            while (true) {
                data = new byte[BUFSIZE];
                datagramPacket = new DatagramPacket(data, data.length);
                serverSocket.receive(datagramPacket);

                InetAddress inetAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();

                String clientMessage = new String(datagramPacket.getData(), 0, datagramPacket.getLength()).trim();
                System.out.println("Received : " + clientMessage);

                processClient(clientMessage, inetAddress, clientPort);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void processClient(String clientMessage, InetAddress inetAddress, int clientPort) {
        if (clientMessage.startsWith("hi")) {
            String name = clientMessage.substring(2).trim();
            if (name != null && isUniqueName(name)) {
                groupMembers.add(name);
                sendMessage(GROUP_HOST, inetAddress, clientPort);
            } else {
                sendMessage("no", inetAddress, clientPort);
            }
        } else if (clientMessage.startsWith("bye")) {
            String name = clientMessage.substring(3).trim();
            if (name != null) {
                removeName(name);
            }
        } else if (clientMessage.equals("who")) {
            sendMessage(listNames(), inetAddress, clientPort);
        } else {
            System.out.println("Do not understand the message");
        }
    }

    private void sendMessage(String message, InetAddress inetAddress, int port) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), inetAddress, port);
            serverSocket.send(datagramPacket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private boolean isUniqueName(String name) {
        String clientName;
        for (int i = 0; i < groupMembers.size(); i++) {
            clientName = (String) groupMembers.get(i);
            if (clientName.equals(name)) {
                return false;
            }
        }
        return true;
    }

    private void removeName(String name) {
        String clientName;
        for (int i = 0; i < groupMembers.size(); i++) {
            clientName = (String) groupMembers.get(i);
            if (clientName.equals(name)) {
                groupMembers.remove(i);
                break;
            }
        }
    }

    private String listNames() {
        String list = new String();
        String name;
        for (int i = 0; i < groupMembers.size(); i++) {
            name = (String) groupMembers.get(i);
            list += "" + (i + 1) + " " + name + "\n";
        }
        return list;
    }

    public static void main(String[] args) {
        new NameServer();
    }


}
