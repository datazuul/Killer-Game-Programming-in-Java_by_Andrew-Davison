package com.example.shooter3D;

import javax.swing.*;
import java.awt.*;

public class Shooter3D extends JFrame {

    public Shooter3D() {
        super("Shooter3D");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapShooter3D wrapShooter3D = new WrapShooter3D();
        container.add(wrapShooter3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Shooter3D();
    }
}
