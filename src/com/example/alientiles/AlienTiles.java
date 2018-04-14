package com.example.alientiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AlienTiles extends JFrame implements WindowListener {

    private static int DEFAULT_FPS = 40;

    private AlienTilesPanel atp;
    private MidisLoader midisLoader;

    public AlienTiles(long period) {
        super("AlienTiles");

        midisLoader = new MidisLoader();
        midisLoader.load("mi", "Mission_Impossible.mid");
        midisLoader.play("mi", true);

        Container c = getContentPane();
        atp = new AlienTilesPanel(this, period);
        c.add(atp, "Center");

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
        atp.stopGame();
        midisLoader.close();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
        atp.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        atp.resumeGame();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        atp.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        atp.pauseGame();
    }

    public static void main(String[] args) {
        long period = (long) 1000 / DEFAULT_FPS;
        new AlienTiles(period * 1000000L);
    }
}
