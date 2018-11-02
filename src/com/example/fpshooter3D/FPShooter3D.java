package com.example.fpshooter3D;

import javax.swing.*;
import java.awt.*;

public class FPShooter3D extends JFrame {

    public FPShooter3D() {
        super("FPShooter3D");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapFPShooter3D wrapFPShooter3D = new WrapFPShooter3D();
        container.add(wrapFPShooter3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new FPShooter3D();
    }
}
