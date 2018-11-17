package com.example.networkingbasics.sequential;

import java.io.*;
import java.util.StringTokenizer;

public class HighScores {

    private ScoreInfo[] scores;
    private int numScores;

    private static final int MAX_SCORES = 10;
    private static String SCORE_FN = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/networkingbasics/sequential/scores.txt";

    public HighScores() {
        scores = new ScoreInfo[MAX_SCORES];
        numScores = 0;
        loadScores();
    }

    public String toString() {
        String details = "HIGH$$";
        for (int i = 0; i < numScores; i++) {
            details += scores[i].getName() + " & " + scores[i].getScore() + " & ";
        }
        System.out.println("details : " + details);
        return details;
    }

    public void addScore(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line, " & ");
        try {
            String name = tokenizer.nextToken().trim();
            int score = Integer.parseInt(tokenizer.nextToken().trim());
            addScore(name, score);
            saveScores();
        } catch (Exception e) {
            System.out.println("Problem parsing new score : \n" + e);
        }
    }

    public void addScore(String name, int score) {
        int i = 0;

        while ((i < numScores) && (scores[i].getScore() >= score)) {
            i++;
        }

        if (i == MAX_SCORES) {
            System.out.println("Score too small to be added to full array");
            return;
        }

        if (numScores == MAX_SCORES) {
            numScores--;
        }

        for (int j = numScores - 1; j >= i; j--) {
            scores[j + 1] = scores[j];
        }

        scores[i] = new ScoreInfo(name, score);

        numScores++;
    }

    private void loadScores() {
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(SCORE_FN));
            while ((line = bufferedReader.readLine()) != null) {
                addScore(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void saveScores() {
        String line;
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(SCORE_FN)), true);
            for (int i = 0; i < numScores; i++) {
                line = scores[i].getName() + " & " + scores[i].getScore() + " & ";
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