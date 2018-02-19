package com.example.test;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;

public class UtilTimerClass {
    private static int DEFAULT_FPS = 80;

    public static void main(String[] args) {
        int fps = DEFAULT_FPS;
        if (args.length != 0) {
            fps = Integer.parseInt(args[0]);
        }
        int period = (int) 1000 / fps;
        System.out.println("fps : " + fps + "; period : " + period + " ms");
        PaintPanel pp = new PaintPanel(period);

        JFrame app = new JFrame("Utilities Timer Test");
        app.getContentPane().add(pp, BorderLayout.CENTER);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MyTimerTask task = new MyTimerTask(pp);
        Timer t = new Timer();
        t.scheduleAtFixedRate(task, 0, period);

        app.pack();
        app.setResizable(false);
        app.setVisible(true);
    }
}
