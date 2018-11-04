package com.example.fractalland3D;

import javax.swing.*;
import java.awt.*;

public class FractalLand3D extends JFrame {

    private static final double DEF_FLAT = 2.3;
    private static final double MIN_FLAT = 1.6;
    private static final double MAX_FLAT = 2.5;

    public FractalLand3D(String[] args) {
        super("3D Fractal Landscape");

        double flatness = processArgs(args);
        System.out.println("Flatness : " + flatness);

        WrapFractalLand3D wrapFractalLand3D = new WrapFractalLand3D(flatness);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(wrapFractalLand3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    private double processArgs(String[] args) {
        double flatness = DEF_FLAT;
        if (args.length == 1) {
            flatness = getFlatness(args[0]);
        } else if (args.length > 1) {
            System.out.println("Use this");
            System.exit(0);
        }
        return flatness;
    }

    private double getFlatness(String arg) {
        double flatness;
        try {
            flatness = Double.parseDouble(arg);
            if ((flatness < MIN_FLAT) || (flatness > MAX_FLAT)) {
                System.out.println("Flatness must be between " + MIN_FLAT + " and " + MAX_FLAT);
                flatness = DEF_FLAT;
            }
        } catch (NumberFormatException e) {
            System.out.println("Incorrect format for Flatness double");
            flatness = DEF_FLAT;
        }
        return flatness;
    }

    public static void main(String[] args) {
        new FractalLand3D(args);
    }
}
