package com.example.flocking3D;

import javax.swing.*;
import java.awt.*;

public class Flocking3D extends JFrame {

    private static final int NUM_PREDATORS = 40;
    private static final int NUM_PREY = 160;
    private static final int NUM_OBSTACLES = 20;

    public Flocking3D(String[] args) {
        super("Flocking predator and prey boids");

        int numPreds = NUM_PREDATORS;
        int numPrey = NUM_PREY;
        int numObstacles = NUM_OBSTACLES;

        if (args.length >= 1) {
            try {
                numPreds = Integer.parseInt(args[0]);
                numPrey = numPreds;
            } catch (NumberFormatException e) {
                System.out.println("Illegal number of predators");
            }
        }

        if (args.length >= 2) {
            try {
                numPrey = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number of prey");
            }
        }

        if (args.length == 3) {
            try {
                numObstacles = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number of obstacles");
            }
        }

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapFlocking3D wrapFlocking3D = new WrapFlocking3D(numPreds, numPrey, numObstacles);
        container.add(wrapFlocking3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Flocking3D(args);
    }
}
