package com.example.wormchase;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {

    private static final int PWIDTH = 500;
    private static final int PHEIGHT = 400;
    private static final int NO_DELAYS_PER_YIELD = 16;

    private static int MAX_FRAME_SKIPS = 5;

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean gameOver = false;
    private volatile boolean isPaused = false;
    private Graphics dbg;
    private Image dbImage = null;

    public GamePanel() {
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
        setFocusable(true);
        requestFocus();
        readyForTermination();

        addMouseListener(new MouseAdapter() {
            //@Override
            public void mousePressed(MouseEvent e) {
                //super.mousePressed(e);
                testPress(e.getX(), e.getY());
            }
        });
    }

    public void addNotify() {
        super.addNotify();
        startGame();
    }

    private void startGame() {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    public void resumeGame() {
        isPaused = false;
    }

    public synchronized void stopGame() {
        running = false;
        notify();
    }

    public void pauseGame() {
        isPaused = true;
    }

    //@Override
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        long excess = 0L;
        int noDelays = 0;

        beforeTime = J3DTimer.getValue();

        running = true;
        while (running) {
            try {
                if (isPaused) {
                    synchronized (this) {
                        while (isPaused && running) {
                            wait();
                        }
                    }
                }
            } catch (InterruptedException e) {

            }
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = J3DTimer.getValue();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) {

                try {
                    Thread.sleep(sleepTime / 1000000L);
                } catch (InterruptedException ex) {

                }
                overSleepTime = (J3DTimer.getValue() - afterTime) - sleepTime;
            } else {
                excess -= sleepTime;
                overSleepTime = 0L;
                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield();
                    noDelays = 0;
                }
            }
            beforeTime = J3DTimer.getValue();
            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate();
                skips++;
            }
        }
        System.exit(0);
    }

    private void gameUpdate() {
        if (!isPaused && !gameOver) {

        }
    }

    private void gameRender() {
        if (dbImage == null) {
            dbImage = createImage(PWIDTH, PHEIGHT);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else {
                dbg = dbImage.getGraphics();
            }
            dbg.setColor(Color.white);
            dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
            if (gameOver) {
                gameOverMessage(dbg);
            }
        }
    }

    private void gameOverMessage(Graphics g) {
        g.drawString(msg, x, y);
    }

    public void paintComponen(Graphics g) {
        super.paintComponent(g);
        if (dbImage != null) {
            g.drawImage(dbImage, 0, 0, null);
        }
    }

    private void readyForTermination() {
        addKeyListener(new KeyAdapter() {
            //@Override
            public void keyPressed(KeyEvent e) {
                //super.keyPressed(e);
                int keyCode = e.getKeyCode();
                if ((keyCode == KeyEvent.VK_ESCAPE) ||
                        (keyCode == KeyEvent.VK_Q) ||
                        (keyCode == KeyEvent.VK_END) ||
                        ((keyCode == KeyEvent.VK_C) &&
                                e.isControlDown())) {
                    running = false;
                }
            }
        });
    }

    private void testPress(int x, int y) {
        if (!isPaused && !gameOver) {

        }
    }

    private void paintScreen() {
        Graphics g;
        try {
            g = this.getGraphics();
            if ((g != null) && (dbImage != null)) {
                g.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        } catch (Exception e) {
            System.out.println("Graphics context error : " + e);
        }
    }
}