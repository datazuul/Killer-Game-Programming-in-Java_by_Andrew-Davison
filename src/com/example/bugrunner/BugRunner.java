package com.example.bugrunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class BugRunner extends JFrame implements WindowListener {

    private static int DEFAULT_FPS = 40;

    private BugPanel bp;
    private MidisLoader midisLoader;

    public BugRunner(long period) {
        super("BugRunner");

        midisLoader = new MidisLoader();
        midisLoader.load("br", "blade_runner.mid");
        midisLoader.play("br", true);

        Container c = getContentPane();
        bp = new BugPanel(this, period);
        c.add(bp, "Center");

        addWindowListener(this);
        pack();
        setResizable(false);
        setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        bp.stopGame();
        midisLoader.close();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
        bp.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        bp.resumeGame();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        bp.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        bp.pauseGame();
    }

    public static void main(String[] args) {
        long period = (long) 1000 / DEFAULT_FPS;
        new BugRunner(period * 1000000L);
    }
}
