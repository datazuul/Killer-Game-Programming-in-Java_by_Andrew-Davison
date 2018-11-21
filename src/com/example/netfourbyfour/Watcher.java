package com.example.netfourbyfour;

import java.io.BufferedReader;
import java.util.StringTokenizer;

public class Watcher extends Thread {

    private NetFourByFour netFourByFour;
    private BufferedReader bufferedReader;

    public Watcher(NetFourByFour netFourByFour, BufferedReader bufferedReader) {
        this.netFourByFour = netFourByFour;
        this.bufferedReader = bufferedReader;
    }

    public void run() {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("ok")) {
                    extractID(line.substring(3));
                } else if (line.startsWith("full")) {
                    netFourByFour.disable("full game");
                } else if (line.startsWith("tooFewPlayers")) {
                    netFourByFour.disable("other player has left");
                } else if (line.startsWith("otherTurn")) {
                    extractOther(line.substring(10));
                } else if (line.startsWith("added")) {
                    netFourByFour.addPlayer();
                } else if (line.startsWith("removed")) {
                    netFourByFour.removePlayer();
                } else {
                    System.out.println("Error : " + line + "\n");
                }
            }
        } catch (Exception e) {
            netFourByFour.disable("server link lost");
        }
    }

    private void extractID(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        try {
            int id = Integer.parseInt(tokenizer.nextToken());
            netFourByFour.setPlayerID(id);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }

    private void extractOther(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        try {
            int playerID = Integer.parseInt(tokenizer.nextToken());
            int position = Integer.parseInt(tokenizer.nextToken());
            netFourByFour.doMove(position, playerID);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
    }
}
