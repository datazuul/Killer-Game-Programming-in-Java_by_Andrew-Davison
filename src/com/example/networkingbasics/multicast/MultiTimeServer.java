package com.example.networkingbasics.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;

public class MultiTimeServer {

    private static final String MHOST = "228.5.6.7";
    private static final int PORT = 6789;

    public static void main(String[] args) throws Exception {
        InetAddress inetAddress = InetAddress.getByName(MHOST);
        MulticastSocket multicastSocket = new MulticastSocket(PORT);
        multicastSocket.joinGroup(inetAddress);

        DatagramPacket datagramPacket;
        System.out.println("Ticking");
        while (true) {
            Thread.sleep(1000);
            System.out.print(".");
            String date = (new Date()).toString();
            datagramPacket = new DatagramPacket(date.getBytes(), date.length(), inetAddress, PORT);

            multicastSocket.send(datagramPacket);
        }
    }
}
