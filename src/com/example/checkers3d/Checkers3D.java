package com.example.checkers3d;

import javax.swing.*;
import java.awt.*;

public class Checkers3D extends JFrame {

    public Checkers3D() {
        super("Checkers3D");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapCheckers3D wrapCheckers3D = new WrapCheckers3D();
        container.add(wrapCheckers3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Checkers3D();
    }
}
