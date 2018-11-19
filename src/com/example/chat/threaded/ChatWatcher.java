package com.example.chat.threaded;

import javax.swing.*;
import java.io.BufferedReader;
import java.util.StringTokenizer;

public class ChatWatcher extends Thread {

    private ChatClient chatClient;
    private BufferedReader bufferedReader;

    public ChatWatcher(ChatClient chatClient, BufferedReader bufferedReader) {
        this.chatClient = chatClient;
        this.bufferedReader = bufferedReader;
    }

    public void run() {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.length() >= 6) && (line.substring(0, 5).equals("WHO$$"))) {
                    showWho(line.substring(5).trim());
                } else {
                    chatClient.showMessage(line + "\n");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Link to server lost!!!", "connection closed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void showWho(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, "&");
        String address;
        int port;
        int i = 1;
        try {
            while (tokenizer.hasMoreTokens()) {
                address = tokenizer.nextToken().trim();
                port = Integer.parseInt(tokenizer.nextToken().trim());
                chatClient.showMessage("" + i + ". " + address + " : " + port + "\n");
                i++;
            }
        } catch (Exception e) {
            chatClient.showMessage("Problem parsing who info.\n");
            System.out.println("Parsing error with who info : \n" + e);
        }
    }
}
