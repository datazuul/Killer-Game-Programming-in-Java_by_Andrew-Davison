package com.example.tour3D;

import javax.swing.*;
import java.awt.*;

public class Tour3D extends JFrame {

    public Tour3D(String args[]) {
        super("3D Tour");
        String tourFnm = null;

        if (args.length == 1) {
            tourFnm = args[0];
        } else {
            System.out.println("NOT OK");
            System.exit(0);
        }

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapTour3D wrapTour3D = new WrapTour3D(tourFnm, this);
        container.add(wrapTour3D, BorderLayout.CENTER);

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Tour3D(args);
    }
}
