package com.example.animTour3D;

import javax.swing.*;
import java.awt.*;

public class AnimTour3D extends JFrame {

    public AnimTour3D() {
        super("Animated 3D Tour");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapAnimTour3D wrapAnimTour3D = new WrapAnimTour3D();
        container.add(wrapAnimTour3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AnimTour3D();
    }
}
