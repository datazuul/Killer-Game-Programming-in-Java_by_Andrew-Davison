package com.example.networkingbasics.firewalls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class GetWebPage {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("OK");
            System.exit(0);
        }

        URL url = new URL(args[0]);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

        int numLine = 0;
        String line;
        while (((line = bufferedReader.readLine()) != null) && (numLine <= 10)) {
            System.out.println(line);
            numLine++;
        }
        if (line != null) {
            System.out.println("...");
        }

        bufferedReader.close();
    }
}
