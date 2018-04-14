package com.example.alientiles;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class AlienTilesPanel extends JPanel implements Runnable {

    private static final int PWIDTH = 800;
    private static final int PHEIGHT = 400;

    private static final int NO_DELAYS_PER_YIELD = 16;
    private static final int MAX_FRAME_SKIPS = 5;

    private static final String IMS_INFO = "imsInfo.txt";
    private static final String SNDS_FILE = "clipsInfo.txt";

    private static final Color lightBlue = new Color(0.17f, 0.87f, 1.0f);

    private Thread animator;
    private volatile boolean running = false;
    private volatile boolean isPaused = false;

    private long period;

    private AlienTiles alienTop;
    private ClipsLoader clipsLoader;

    private WorldDisplay world;
    private PlayerSprite player;
    private AlienSprite aliens[];

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

    public AlienTilesPanel(AlienTiles at, long period) {
        alienTop = at;
        this.period = period;

        setDoubleBuffered(false);
        setBackground(Color.black);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                processKey(e);
            }
        });

        ImagesLoader imsLoader = new ImagesLoader(IMS_INFO);
        clipsLoader = new ClipsLoader(SNDS_FILE);

        createWorld(imsLoader);

        helpIm = imsLoader.getImage("title");
        showHelp = true;
        isPaused = true;

        msgsFont = new Font("SansSerif", Font.BOLD, 24);
        metrics = this.getFontMetrics(msgsFont);
    }

    private void createWorld(ImagesLoader imsLoader) {
        world = new WorldDisplay(imsLoader, this);
        player = new PlayerSprite(7, 12, PWIDTH, PHEIGHT, clipsLoader, imsLoader, world, this);
        aliens = new AlienSprite[4];

        aliens[0] = new AlienAStarSprite(10, 11, PWIDTH, PHEIGHT, imsLoader, world);
        aliens[1] = new AlienQuadSprite(6, 21, PWIDTH, PHEIGHT, imsLoader, world);
        aliens[2] = new AlienQuadSprite(14, 20, PWIDTH, PHEIGHT, imsLoader, world);
        aliens[3] = new AlienAStarSprite(34, 34, PWIDTH, PHEIGHT, imsLoader, world);

        world.addSprites(player, aliens);
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
            if (keyCode == KeyEvent.VK_NUMPAD7) {
                player.move(TiledSprite.NW);
            } else if (keyCode == KeyEvent.VK_NUMPAD9) {
                player.move(TiledSprite.NE);
            } else if (keyCode == KeyEvent.VK_NUMPAD3) {
                player.move(TiledSprite.SE);
            } else if (keyCode == KeyEvent.VK_NUMPAD1) {
                player.move(TiledSprite.SW);
            } else if (keyCode == KeyEvent.VK_NUMPAD5) {
                player.standStill();
            } else if (keyCode == KeyEvent.VK_NUMPAD2) {
                player.tryPickup();
            }
        }
    }

    public void gameOver() {
        if (!gameOver) {
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
                    overSleepTime = (J3DTimer.getValue() - afterTime) - sleepTime;
                }
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
            for (int i = 0; i < aliens.length; i++) {
                aliens[i].update();
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

        dbg.setColor(lightBlue);
        dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
        world.draw(dbg);

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
            graphics.drawString("Time : " + timeSpentInGame + " secs", 15, 25);
            graphics.drawString(player.getHitStatus(), 15, 50);
            graphics.drawString(world.getPickupsStatus(), 15, 75);
            graphics.setColor(Color.black);
        }
    }

    private void gameOverMessage(Graphics graphics) {
        String msg = "Game Over. Your score : " + score;

        int x = (PWIDTH - metrics.stringWidth(msg)) / 2;
        int y = (PHEIGHT - metrics.getHeight()) / 2;
        graphics.setColor(Color.red);
        graphics.setFont(msgsFont);
        graphics.drawString(msg, x, y);
        graphics.setColor(Color.black);
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
