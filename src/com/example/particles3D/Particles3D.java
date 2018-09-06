package com.example.particles3D;

import javax.swing.*;
import java.awt.*;

public class Particles3D extends JFrame {

    private static final int NUM_PARTICLES = 3000;
    private static final int FOUNTAIN_CHOICE = 1;

    public Particles3D(String args[]) {
        super("Particles3D");

        int numParticles = NUM_PARTICLES;
        int fountainChoice = FOUNTAIN_CHOICE;

        if (args.length > 0) {
            try {
                numParticles = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal number of particles");
            }

            if (numParticles < 0) {
                System.out.println("Number of particles must be positive");
                numParticles = NUM_PARTICLES;
            }
        }

        if (args.length > 1) {
            try {
                fountainChoice = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal fountain choice");
            }

            if ((fountainChoice < 1) || (fountainChoice > 3)) {
                System.out.println("Fountain choices are 1-3");
                fountainChoice = FOUNTAIN_CHOICE;
            }
        }

        System.out.println("numParticles : " + numParticles + "; fountainChoice : " + fountainChoice);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapParticles3D wrapParticles3D = new WrapParticles3D(numParticles, fountainChoice);
        container.add(wrapParticles3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Particles3D(args);
    }
}
