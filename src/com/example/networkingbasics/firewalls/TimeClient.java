package com.example.networkingbasics.firewalls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class TimeClient {

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://localhost:8100/servlet/TimeServlet");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        bufferedReader.close();
    }
}
