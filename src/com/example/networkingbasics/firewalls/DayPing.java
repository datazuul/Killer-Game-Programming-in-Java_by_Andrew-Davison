package com.example.networkingbasics.firewalls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DayPing {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("OK");
            System.exit(0);
        }

        Socket socket = new Socket(args[0], 13);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(args[0] + " is alive at ");
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        socket.close();
    }
}
