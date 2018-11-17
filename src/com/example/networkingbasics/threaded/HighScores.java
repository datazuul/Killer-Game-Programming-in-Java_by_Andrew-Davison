package com.example.networkingbasics.threaded;

import java.io.*;
import java.util.StringTokenizer;

public class HighScores {

    private ScoreInfo[] scoreInfos;
    private int numScores;

    private static final int MAX_SCORES = 10;
    private static String SCORE_FN = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/networkingbasics/threaded/scores.txt";

    public HighScores() {
        scoreInfos = new ScoreInfo[MAX_SCORES];
        numScores = 0;
        loadScores();
    }

    synchronized public String toString() {
        String details = "HIGH$$ ";
        for (int i = 0; i < numScores; i++) {
            details += scoreInfos[i].getName() + " & " + scoreInfos[i].getScore() + " & ";
        }
        System.out.println("details : " + details);
        return details;
    }

    synchronized public void addScore(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, "&");
        try {
            String name = tokenizer.nextToken().trim();
            int score = Integer.parseInt(tokenizer.nextToken().trim());
            addScore(name, score);
            saveScores();
        } catch (Exception e) {
            System.out.println("Problem parsing new score : \n" + e);
        }
    }

    private void addScore(String name, int score) {
        int i = 0;

        while ((i < numScores) && (scoreInfos[i].getScore() >= score)) {
            i++;
        }

        if (i == MAX_SCORES) {
            System.out.println("Score too small to be added to full array");
            return;
        }

        if (numScores == MAX_SCORES) {
            numScores--;
        }

        for (int j = numScores - 1; j >= 1; j--) {
            scoreInfos[j + 1] = scoreInfos[j];
        }

        scoreInfos[i] = new ScoreInfo(name, score);
        numScores++;
    }

    private void loadScores() {
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader(SCORE_FN));
            while ((line = in.readLine()) != null) {
                addScore(line);
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void saveScores() {
        String line;
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(SCORE_FN)), true);
            for (int i = 0; i < numScores; i++) {
                line = scoreInfos[i].getName() + " & " + scoreInfos[i].getScore() + " & ";
                out.println(line);
            }
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

class ScoreInfo {

    private String name;
    private int score;

    public ScoreInfo(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}