package com.example.jumpingjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class JumpingJack extends JFrame implements WindowListener {

    private static int DEFAULT_FPS = 30;

    private JackPanel jp;
    private MidisLoader midisLoader;

    public JumpingJack(long period) {
        super("JumpingJack");

        midisLoader = new MidisLoader();
        midisLoader.load("jjf", "jumping_jack_flash.mid");
        midisLoader.play("jjf", true);

        Container container = getContentPane();
        jp = new JackPanel(this, period);
        container.add(jp, "Center");

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
        jp.stopGame();
        midisLoader.close();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {
        jp.pauseGame();
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        jp.resumeGame();
    }

    @Override
    public void windowActivated(WindowEvent e) {
        jp.resumeGame();
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        jp.pauseGame();
    }

    public static void main(String[] args) {
        long period = (long) 1000 / DEFAULT_FPS;
        new JumpingJack(period * 1000000L);
    }
}
