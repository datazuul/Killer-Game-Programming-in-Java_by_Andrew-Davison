package com.example.terra3D;

import javax.swing.*;
import java.awt.*;

public class Terra3D extends JFrame {

    public Terra3D(String args[]) {
        super("Terra3D");
        String fn = null;

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapTerra3D wrapTerra3D = new WrapTerra3D(fn);
        container.add(wrapTerra3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Terra3D(args);
    }
}
