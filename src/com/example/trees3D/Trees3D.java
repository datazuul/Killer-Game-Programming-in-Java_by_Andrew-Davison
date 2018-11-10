package com.example.trees3D;

import javax.swing.*;
import java.awt.*;

public class Trees3D extends JFrame {

    public Trees3D() {
        super("Trees3D");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapTrees3D wrapTrees3D = new WrapTrees3D();
        container.add(wrapTrees3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Trees3D();
    }
}
