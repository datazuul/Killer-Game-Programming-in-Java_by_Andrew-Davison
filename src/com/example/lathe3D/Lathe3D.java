package com.example.lathe3D;

import javax.swing.*;
import java.awt.*;

public class Lathe3D extends JFrame {

    public Lathe3D() {
        super("Lathe3D");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapLathe3D wrapLathe3D = new WrapLathe3D();
        container.add(wrapLathe3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Lathe3D();
    }
}
