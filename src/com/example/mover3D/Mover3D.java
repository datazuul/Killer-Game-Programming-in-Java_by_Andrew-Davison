package com.example.mover3D;

import javax.swing.*;
import java.awt.*;

public class Mover3D extends JFrame {

    public Mover3D() {
        super("3D Moveable Figure");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        WrapMover3D wrapMover3D = new WrapMover3D();
        container.add(wrapMover3D, BorderLayout.CENTER);

        Figure figure = wrapMover3D.getFigure();
        CommandsPanel commandsPanel = new CommandsPanel(figure);
        container.add(commandsPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Mover3D();
    }
}
