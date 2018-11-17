package com.example.networkingbasics.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiTimeClient {

    private static final String MHOST = "228.5.6.7";
    private static final int PORT = 6789;

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(MHOST);
        MulticastSocket multicastSocket = new MulticastSocket(PORT);
        multicastSocket.joinGroup(inetAddress);

        byte[] bytes = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
        String date;
        while (true) {
            multicastSocket.receive(datagramPacket);
            date = new String(datagramPacket.getData()).trim();
            System.out.println(datagramPacket.getAddress() + " : " + date);
        }
    }
}
