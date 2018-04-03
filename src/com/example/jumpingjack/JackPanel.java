package com.example.jumpingjack;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class JackPanel extends JPanel implements Runnable, ImagesPlayerWatcher {

    private static final int PWIDTH = 500;
    private static final int PHEIGHT = 360;

    private static final int NO_DELAYS_PER_YIELD = 16;
    private static final int MAX_FRAME_SKIPS = 5;

    private static final String IMS_INFO = "imsInfo.txt";
    private static final String BRICKS_INFO = "bricksInfo.txt";
    private static final String SNDS__FILE = "clipsInfo.txt";

    private static final String[] exploNames = {"explo1", "explo2", "explo3"};

    private static final int MAX_HITS = 20;

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean isPaused = false;

    private long period;

    private JumpingJack jackTop;
    private ClipsLoader clipsLoader;

    private JumperSprite jack;
    private FireBallSprite fireBall;
    private RibbonsManager ribbonsManager;
    private BricksManager bricksManager;

    private long gameStartTime;
    private int timeSpentInGame;

    private volatile boolean gameOver = false;
    private int score = 0;

    private Font msgsFont;
    private FontMetrics metrics;

    private Graphics dbg;
    private Image dbImage = null;

    private boolean showHelp;
    private BufferedImage helpIm;

    private ImagesPlayer explosionPlayer = null;
    private boolean showExplosion = false;
    private int explWidth, explHeight;
    private int xExpl, yExpl;

    private int numHits = 0;

    public JackPanel(JumpingJack jj, long period) {
        jackTop = jj;
        this.period = period;

        setDoubleBuffered(false);
        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //super.keyPressed(e);
                processKey(e);
            }
        });

        ImagesLoader imsLoader = new ImagesLoader(IMS_INFO);
        clipsLoader = new ClipsLoader(SNDS__FILE);

        bricksManager = new BricksManager(PWIDTH, PHEIGHT, BRICKS_INFO, imsLoader);
        int brickMoveSize = bricksManager.getMoveSize();

        ribbonsManager = new RibbonsManager(PWIDTH, PHEIGHT, brickMoveSize, imsLoader);
        jack = new JumperSprite(PWIDTH, PHEIGHT, brickMoveSize, bricksManager, imsLoader, (int) (period / 1000000L));
        fireBall = new FireBallSprite(PWIDTH, PHEIGHT, imsLoader, this, jack);

        explosionPlayer = new ImagesPlayer("explosion", (int) (period / 1000000L), 0.5, false, imsLoader);
        BufferedImage explosionIm = imsLoader.getImage("explosion");
        explWidth = explosionIm.getWidth();
        explHeight = explosionIm.getHeight();
        explosionPlayer.setWatcher(this);

        helpIm = imsLoader.getImage("title");
        showHelp = true;
        isPaused = true;

        msgsFont = new Font("SansSerif", Font.BOLD, 24);
        metrics = this.getFontMetrics(msgsFont);
    }

    private void processKey(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END) ||
                ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
            running = false;
        }

        if (keyCode == KeyEvent.VK_H) {
            if (showHelp) {
                showHelp = false;
                isPaused = false;
            } else {
                showHelp = true;
                isPaused = true;
            }
        }

        if (!isPaused && !gameOver) {
            if (keyCode == KeyEvent.VK_LEFT) {
                jack.moveLeft();
                bricksManager.moveRight();
                ribbonsManager.moveRight();
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                jack.moveRight();
                bricksManager.moveLeft();
                ribbonsManager.moveLeft();
            } else if (keyCode == KeyEvent.VK_UP) {
                jack.jump();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                jack.stayStill();
                bricksManager.stayStill();
                ribbonsManager.stayStill();
            }
        }
    }

    public void showExplosion(int x, int y) {
        if (!showExplosion) {
            showExplosion = true;
            xExpl = x - explWidth / 2;
            yExpl = y - explHeight / 2;
            clipsLoader.play(exploNames[numHits % exploNames.length], false);
            numHits++;
        }
    }

    @Override
    public void sequenceEnded(String imageName) {
        showExplosion = false;
        explosionPlayer.restartAt(0);

        if (numHits >= MAX_HITS) {
            gameOver = true;
            score = (int) ((J3DTimer.getValue() - gameStartTime) / 1000000000L);
            clipsLoader.play("applause", false);
        }
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
        if (!showHelp) {
            isPaused = false;
        }
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void stopGame() {
        running = false;
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
            if (jack.willHitBrick()) {
                jack.stayStill();
                bricksManager.stayStill();
                ribbonsManager.stayStill();
            }
            ribbonsManager.update();
            bricksManager.update();
            jack.updateSprite();
            fireBall.updateSprite();

            if (showExplosion) {
                explosionPlayer.updateTick();
            }
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
        }

        dbg.setColor(Color.white);
        dbg.fillRect(0, 0, PWIDTH, PHEIGHT);

        ribbonsManager.display(dbg);
        bricksManager.display(dbg);
        jack.drawSprite(dbg);
        fireBall.drawSprite(dbg);

        if (showExplosion) {
            dbg.drawImage(explosionPlayer.getCurrentImage(), xExpl, yExpl, null);
        }

        reportStats(dbg);

        if (gameOver) {
            gameOverMessage(dbg);
        }

        if (showHelp) {
            dbg.drawImage(helpIm, (PWIDTH - helpIm.getWidth()) / 2, (PHEIGHT - helpIm.getHeight()) / 2, null);
        }
    }

    private void reportStats(Graphics graphics) {
        if (!gameOver) {
            timeSpentInGame = (int) ((J3DTimer.getValue() - gameStartTime) / 1000000000L);
            graphics.setColor(Color.red);
            graphics.setFont(msgsFont);
            graphics.drawString("Hits : " + numHits + "/" + MAX_HITS, 15, 25);
            graphics.drawString("Time : " + timeSpentInGame + " secs", 15, 50);
            graphics.setColor(Color.black);
        }
    }

    private void gameOverMessage(Graphics graphics) {
        String msg = "Game Over. Your Score : " + score;

        int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
        int y = (PHEIGHT - metrics.getHeight()) / 2;

        graphics.setColor(Color.black);
        graphics.setFont(msgsFont);
        graphics.drawString(msg, x, y);
    }

    private void paintScreen() {
        Graphics graphics;
        try {
            graphics = this.getGraphics();
            if ((graphics != null) && (dbImage != null)) {
                graphics.drawImage(dbImage, 0, 0, null);
                Toolkit.getDefaultToolkit().sync();
                graphics.dispose();
            }
        } catch (Exception e) {
            System.out.println("Graphics context error : " + e);
        }
    }
}
