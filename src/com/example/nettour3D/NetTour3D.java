package com.example.nettour3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class NetTour3D extends JFrame {

    private String userName = null;
    private String tourFileName = null;
    private double xPosition = 0.0;
    private double zPosition = 0.0;

    private WrapNetTour3D wrapNetTour3D;

    public NetTour3D(String args[]) {
        super("3D NetTour");
        processArguments(args);
        setTitle(getTitle() + " for " + userName);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        wrapNetTour3D = new WrapNetTour3D(userName, tourFileName, xPosition, zPosition);
        container.add(wrapNetTour3D, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                wrapNetTour3D.closeLink();
            }
        });

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void processArguments(String[] args) {
        if (args.length == 4) {
            userName = args[0];
            tourFileName = args[3];

            try {
                xPosition = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("xPosition value must be double");
            }

            try {
                zPosition = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("zPosition value must be double");
            }
        } else if (args.length == 2) {
            userName = args[0];
            tourFileName = args[1];
            System.out.println("xPosition and zPosition set to 0.0");
        } else {
            System.out.println("OK");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new NetTour3D(args);
    }
}
