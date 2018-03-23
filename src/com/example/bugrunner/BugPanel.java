package com.example.bugrunner;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BugPanel extends JPanel implements Runnable {

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 400;

    private static final int NO_DELAYS_PER_YIELD = 16;

    private static int MAX_FRAME_SKIPS = 5;

    private static final String IMS_INFO = "imsInfo.txt";
    private static final String SOUNDS_FILE = "clipsInfo.txt";

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean isPaused = false;

    private long period;

    private BugRunner bugTop;
    private ClipsLoader clipsLoader;

    private BallSprite ball;
    private BatSprite bat;

    private long gameStartTime;
    private int timeSpentInGame;

    private volatile boolean gameOver = false;
    private int score = 0;

    private Font msgsFont;
    private FontMetrics metrics;

    private Graphics dbg;
    private Image dbImage = null;

    private BufferedImage bgImage = null;

    public BugPanel(BugRunner br, long period) {
        bugTop = br;
        this.period = period;

        setDoubleBuffered(false);
        setBackground(Color.black);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //super.keyPressed(e);
                processKey(e);
            }
        });

        ImagesLoader imagesLoader = new ImagesLoader(IMS_INFO);
        bgImage = imagesLoader.getImage("bladerunner");

        clipsLoader = new ClipsLoader(SOUNDS_FILE);

        bat = new BatSprite(PANEL_WIDTH, PANEL_HEIGHT, imagesLoader, (int) (period / 1000000L));
        ball = new BallSprite(PANEL_WIDTH, PANEL_HEIGHT, imagesLoader, clipsLoader, this, bat);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //super.mousePressed(e);
                testPress(e.getX());
            }
        });

        msgsFont = new Font("SansSerif", Font.BOLD, 24);
        metrics = this.getFontMetrics(msgsFont);
    }

    private void processKey(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_ESCAPE) ||
                (keyCode == KeyEvent.VK_Q) ||
                (keyCode == KeyEvent.VK_END) ||
                ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
            running = false;
        }

        if (!isPaused && !gameOver) {
            if (keyCode == KeyEvent.VK_LEFT) {
                bat.moveLeft();
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                bat.moveRight();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                bat.stayStill();
            }
        }
    }

    public void gameOver() {
        int finalTime = (int) ((J3DTimer.getValue() - gameStartTime) / 1000000000L);
        score = finalTime;
        clipsLoader.play("gameOver", false);
        gameOver = true;
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

    public void pauseGame() {
        isPaused = true;
    }

    public void stopGame() {
        running = false;
    }

    private void testPress(int x) {
        if (!isPaused && !gameOver) {
            bat.mouseMove(x);
        }
    }

    @Override
    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        gameStartTime = J3DTimer.getValue();
        beforeTime = gameStartTime;

        running = true;
        while (running) {
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = J3DTimer.getValue();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000L);
                } catch (InterruptedException e) {

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
            ball.updateSprite();
            bat.updateSprite();
        }
    }

    private void gameRender() {
        if (dbImage == null) {
            dbImage = createImage(PANEL_WIDTH, PANEL_HEIGHT);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else {
                dbg = dbImage.getGraphics();
            }
        }

        if (bgImage == null) {
            dbg.setColor(Color.black);
            dbg.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        } else {
            dbg.drawImage(bgImage, 0, 0, this);
        }

        ball.drawSprite(dbg);
        bat.drawSprite(dbg);

        reportStats(dbg);

        if (gameOver) {
            gameOverMessage(dbg);
        }
    }

    private void reportStats(Graphics graphics) {
        if (!gameOver) {
            timeSpentInGame = (int) ((J3DTimer.getValue() - gameStartTime) / 1000000000L);
            graphics.setColor(Color.yellow);
            graphics.setFont(msgsFont);

            ball.drawBallStats(graphics, 15, 25);
            graphics.drawString("Time : " + timeSpentInGame + " secs", 15, 50);
            graphics.setColor(Color.black);
        }
    }

    private void gameOverMessage(Graphics graphics) {
        String msg = "Game over. Your score : " + score;
        int x = (PANEL_WIDTH - metrics.stringWidth(msg)) / 2;
        int y = (PANEL_HEIGHT - metrics.getHeight()) / 2;

        graphics.setColor(Color.red);
        graphics.setFont(msgsFont);
        graphics.drawString(msg, x, y);
    }

    private void paintScreen() {
        Graphics graphics;
        try {
            graphics = this.getGraphics();
            if ((graphics != null) && (dbImage != null)) {
                graphics.drawImage(dbImage, 0, 0, null);
            }
            Toolkit.getDefaultToolkit().sync();
            graphics.dispose();
        } catch (Exception e) {
            System.out.println("Graphics context error : " + e);
        }
    }
}
