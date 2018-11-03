package com.example.maze3D;

import javax.swing.*;
import java.awt.*;

public class Maze3D extends JFrame {

    public Maze3D(String args[]) {
        super("3D Maze");

        String fnm = null;
        if (args.length == 1) {
            fnm = args[0];
        } else if (args.length == 0) {
            fnm = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/maze.txt";
        } else {
            System.out.println("Usage : java Maze3D <fileName>");
            System.exit(0);
        }

        MazeManager mazeManager = new MazeManager(fnm);
        BirdsEye birdsEye = new BirdsEye(mazeManager);
        SecondViewPanel secondViewPanel = new SecondViewPanel(mazeManager);
        WrapMaze3D wrapMaze3D = new WrapMaze3D(mazeManager, birdsEye, secondViewPanel.getCamera2TG());

        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        container.add(wrapMaze3D);
        container.add(Box.createRigidArea(new Dimension(8, 0)));

        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(secondViewPanel);
        verticalBox.add(Box.createRigidArea(new Dimension(0, 8)));
        verticalBox.add(birdsEye);
        container.add(verticalBox);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Maze3D(args);
    }
}
