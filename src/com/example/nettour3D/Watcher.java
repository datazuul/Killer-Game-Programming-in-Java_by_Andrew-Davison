package com.example.nettour3D;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Watcher extends Thread {

    private HashMap visitors;
    private WrapNetTour3D wrapNetTour3D;
    private BufferedReader bufferedReader;
    private Obstacles obstacles;

    public Watcher(WrapNetTour3D wrapNetTour3D, BufferedReader bufferedReader, Obstacles obstacles) {
        this.wrapNetTour3D = wrapNetTour3D;
        this.bufferedReader = bufferedReader;
        this.obstacles = obstacles;
        visitors = new HashMap();
    }

    public void run() {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("create")) {
                    createVisitor(line.trim());
                } else if (line.startsWith("wantDetails")) {
                    sendDetails(line.trim());
                } else if (line.startsWith("detailsFor")) {
                    receiveDetails(line.trim());
                } else {
                    doCommand(line.trim());
                }
            }
        } catch (Exception e) {
            System.out.println("Link to Server lost");
            System.exit(0);
        }
    }

    private void createVisitor(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        String userName = tokenizer.nextToken();
        double xPosition = Double.parseDouble(tokenizer.nextToken());
        double zPosition = Double.parseDouble(tokenizer.nextToken());
        System.out.println("Create : " + userName + "(" + xPosition + ", " + zPosition + ")");

        if (visitors.containsKey(userName)) {
            System.out.println("Duplicate name -- ignoring it");
        } else {
            System.out.println("Creating Sprite");
            DistanceTourSprite distanceTourSprite = wrapNetTour3D.addVisitor(userName, xPosition, zPosition, 0);
            visitors.put(userName, distanceTourSprite);
        }
    }

    private void sendDetails(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        String clientAddress = tokenizer.nextToken();
        String stringPort = tokenizer.nextToken();

        wrapNetTour3D.sendDetails(clientAddress, stringPort);
    }

    private void receiveDetails(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        String userName = tokenizer.nextToken();
        double xPosition = Double.parseDouble(tokenizer.nextToken());
        double zPosition = Double.parseDouble(tokenizer.nextToken());
        double rotationRadians = Double.parseDouble(tokenizer.nextToken());

        if (visitors.containsKey(userName)) {
            System.out.println("Duplicate name -- ignoring it");
        } else {
            System.out.println("Making sprite for " + userName);
            DistanceTourSprite distanceTourSprite = wrapNetTour3D.addVisitor(userName, xPosition, zPosition, rotationRadians);
            visitors.put(userName, distanceTourSprite);
        }
    }

    private void doCommand(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        String userName = tokenizer.nextToken();
        String command = tokenizer.nextToken();

        DistanceTourSprite distanceTourSprite = (DistanceTourSprite) visitors.get(userName);
        if (distanceTourSprite == null) {
            System.out.println(userName + " is not here");
        } else {
            if (command.equals("forward")) {
                distanceTourSprite.moveForward();
            } else if (command.equals("back")) {
                distanceTourSprite.moveBackward();
            } else if (command.equals("left")) {
                distanceTourSprite.moveLeft();
            } else if (command.equals("right")) {
                distanceTourSprite.moveRight();
            } else if (command.equals("rotCClock")) {
                distanceTourSprite.rotationCounterClock();
            } else if (command.equals("rotClock")) {
                distanceTourSprite.rotationClock();
            } else if (command.equals("bye")) {
                System.out.println("Removing info on " + userName);
                distanceTourSprite.detach();
                visitors.remove(userName);
            } else {
                System.out.println("Do not recognise the command");
            }
        }
    }
}
