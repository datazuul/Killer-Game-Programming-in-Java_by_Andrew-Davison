/*
package com.example.wormchase;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class WormPanel extends JPanel implements Runnable {
    //private static final int PWIDTH = 500;
    //private static final int PHEIGHT = 400;

    private static long MAX_STATS_INTERVAL = 1000000000L;
    //private static long MAX_STATS_INTERVAL = 1000L;
    private static final int NO_DELAYS_PER_YIELD = 16;

    private static int MAX_FRAME_SKIPS = 5;
    private static int NUM_FPS = 10;

    private int pWidth, pHeight;

    private long statsInterval = 0L;
    private long prevStatsTime;
    private long totalElapsedTime = 0L;
    private long gameStartTime;
    private int timeSpentInGame = 0;

    private long frameCount = 0;
    private double fpsStore[];
    private long statsCount = 0;
    private double averageFPS = 0;

    private long framesSkipped = 0L;
    private long totalFramesSkipped = 0L;
    private double upsStore[];
    private double averageUPS = 0;

    private DecimalFormat df = new DecimalFormat("0.##");
    private DecimalFormat timedf = new DecimalFormat("0.####");

    private Thread animator;
    //private boolean running = false;
    private volatile boolean running = false;
    //private boolean isPaused = false;
    //private volatile boolean isPaused = false;

    private long period;

    private WormChase wcTop;
    private Worm fred;
    private Obstacles obs;
    private int boxesUsed = 0;

    //private boolean gameOver = false;
    private volatile boolean gameOver = false;
    private int score = 0;
    private Font font;
    private FontMetrics metrics;
    private boolean finishedOff = false;

    private volatile boolean isOverQuitButton = false;
    private Rectangle quitArea;

    private volatile boolean isOverPauseButton = false;
    private Rectangle pauseArea;
    private volatile boolean isPaused = false;

    private Graphics dbg;
    private Image dbImage = null;

    public WormPanel(WormChase wc, long period */
/**
 * int w, int h
 * private void startGame() {
 * if (animator == null || !running) {
 * animator = new Thread(this);
 * animator.start();
 * }
 * }
 * <p>
 * public void resumeGame() {
 * isPaused = false;
 * }
 * <p>
 * public void pauseGame() {
 * isPaused = true;
 * }
 * <p>
 * public void stopGame() {
 * running = false;
 * //finishOff();
 * }
 * if (!isPaused && !gameOver) {
 * if (fred.nearHead(x, y)) {
 * gameOver = true;
 * score = (40 - timeSpentInGame) + (40 - obs.getNumObstacles());
 * } else {
 * if (!fred.touchedAt(x, y)) {
 * obs.add(x, y);
 * }
 * }
 * }
 **//*
) {
        wcTop = wc;
        this.period = period;
        //pWidth = w;
        //pHeight = h;

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension scrDim = tk.getScreenSize();
        pWidth = scrDim.width;
        pHeight = scrDim.height;

        setBackground(Color.white);
        //setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
        //setPreferredSize(new Dimension(pWidth, pHeight));
        setPreferredSize(scrDim);

        setFocusable(true);
        requestFocus();
        readyForTermination();

        //obs = new Obstacles(wcTop);
        obs = new Obstacles(this);
        fred = new Worm(pWidth, pHeight, obs);
        //gameOver = false;

        addMouseListener(new MouseAdapter() {
            //@Override
            public void mousePressed(MouseEvent e) {
                //super.mousePressed(e);
                testPress(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            //@Override
            public void mouseMoved(MouseEvent e) {
                //super.mouseMoved(e);
                testMove(e.getX(), e.getY());
            }
        });

        font = new Font("SansSerif", Font.BOLD, 24);
        metrics = this.getFontMetrics(font);

        pauseArea = new Rectangle(pWidth - 100, pHeight - 45, 70, 15);
        quitArea = new Rectangle(pWidth - 100, pHeight - 20, 70, 15);

        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++) {
            fpsStore[i] = 0;
            upsStore[i] = 0;
        }
    }

    private void readyForTermination() {
        addKeyListener(new KeyAdapter() {
            //@Override
            public void keyPressed(KeyEvent e) {
                //super.keyPressed(e);
                int keyCode = e.getKeyCode();
                if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
                        (keyCode == KeyEvent.VK_END) ||
                        ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
                    running = false;
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                running = false;
                System.out.println("Shutdown hook executed");
                finishOff();
            }
        });
    }

    public void addNotify() {
        super.addNotify();
        //startGame();

        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    */
/**
 * private void startGame() {
 * if (animator == null || !running) {
 * animator = new Thread(this);
 * animator.start();
 * }
 * }
 * <p>
 * public void resumeGame() {
 * isPaused = false;
 * }
 * <p>
 * public void pauseGame() {
 * isPaused = true;
 * }
 * <p>
 * public void stopGame() {
 * running = false;
 * //finishOff();
 * }
 **//*


    private void testPress(int x, int y) {
        if (isOverPauseButton) {
            isPaused = !isPaused;
        } else if (isOverQuitButton) {
            running = false;
        } else {
            if (!isPaused && !gameOver) {
                if (fred.nearHead(x, y)) {
                    gameOver = true;
                    //score = (40 - timeSpentInGame) + (40 - obs.getNumObstacles());
                    score = (40 - timeSpentInGame) + (40 - boxesUsed);
                } else {
                    if (!fred.touchedAt(x, y)) {
                        obs.add(x, y);
                    }
                }
            }
        }
        */
/**if (!isPaused && !gameOver) {
 if (fred.nearHead(x, y)) {
 gameOver = true;
 score = (40 - timeSpentInGame) + (40 - obs.getNumObstacles());
 } else {
 if (!fred.touchedAt(x, y)) {
 obs.add(x, y);
 }
 }
 }**//*

    }

    private void testMove(int x, int y) {
        if (running) {
            isOverPauseButton = pauseArea.contains(x, y) ? true : false;
            isOverQuitButton = quitArea.contains(x, y) ? true : false;
        }
    }

    public void setBoxNumber(int no) {
        boxesUsed = no;
    }

    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        //int overSleepTime = 0;
        int noDelays = 0;
        long excess = 0L;
        //int excess = 0;

        gameStartTime = J3DTimer.getValue();
        //gameStartTime = System.currentTimeMillis();
        prevStatsTime = gameStartTime;
        beforeTime = gameStartTime;

        running = true;
        while (running) {
            gameUpdate();
            gameRender();
            paintScreen();

            afterTime = J3DTimer.getValue();
            //afterTime = System.currentTimeMillis();
            timeDiff = afterTime - beforeTime;
            sleepTime = (period - timeDiff) - overSleepTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime / 1000000L);
                    //Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {

                }
                overSleepTime = (J3DTimer.getValue() - afterTime) - sleepTime;
                //overSleepTime = (int) ((System.currentTimeMillis() - afterTime) - sleepTime);
            } else {
                excess -= sleepTime;
                overSleepTime = 0L;
                //overSleepTime = 0;

                if (++noDelays >= NO_DELAYS_PER_YIELD) {
                    Thread.yield();
                    noDelays = 0;
                }
            }

            beforeTime = J3DTimer.getValue();
            //beforeTime = System.currentTimeMillis();

            int skips = 0;
            while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
                excess -= period;
                gameUpdate();
                skips++;
            }

            framesSkipped += skips;
            storeStats();
        }
        //printStats();
        //System.exit(0);
        finishOff();
    }

    private void gameUpdate() {
        if (!isPaused && !gameOver) {
            fred.move();
        }
    }

    private void gameRender() {
        if (dbImage == null) {
            dbImage = createImage(pWidth, pHeight);
            if (dbImage == null) {
                System.out.println("dbImage is null");
                return;
            } else {
                dbg = dbImage.getGraphics();
            }

            dbg.setColor(Color.white);
            dbg.fillRect(0, 0, pWidth, pHeight);

            dbg.setColor(Color.blue);
            dbg.setFont(font);

            dbg.drawString("Average FPS/UPS : " + df.format(averageFPS) + ", " +
                    df.format(averageUPS), 20, 25);
            dbg.drawString("Time Spent : " + timeSpentInGame + " secs", 10, pHeight - 15);
            dbg.drawString("Boxes Used : " + boxesUsed, 260, pHeight - 15);

            drawButtons(dbg);
            dbg.setColor(Color.black);

            obs.draw(dbg);
            fred.draw(dbg);

            if (gameOver) {
                gameOverMessage(dbg);
            }
        }
    }

    private void drawButtons(Graphics g) {
        g.setColor(Color.black);

        if (isOverPauseButton) {
            g.setColor(Color.green);
        }

        g.drawOval(pauseArea.x, pauseArea.y, pauseArea.width, pauseArea.height);
        if (isPaused) {
            g.drawString("Paused", pauseArea.x, pauseArea.y + 10);
        } else {
            g.drawString("Pause", pauseArea.x + 5, pauseArea.y + 10);
        }

        if (isOverPauseButton) {
            g.setColor(Color.black);
        }

        if (isOverQuitButton) {
            g.setColor(Color.green);
        }

        g.drawOval(quitArea.x, quitArea.y, quitArea.width, quitArea.height);
        g.drawString("Quit", quitArea.x + 15, quitArea.y + 10);

        if (isOverQuitButton) {
            g.setColor(Color.black);
        }
    }

    private void gameOverMessage(Graphics g) {
        String msg = "Game over. Your Score : " + score;
        int x = (pWidth - metrics.stringWidth(msg)) / 2;
        int y = (pHeight - metrics.getHeight()) / 2;

        g.setColor(Color.red);
        g.setFont(font);
        g.drawString(msg, x, y);
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

    private void storeStats() {
        frameCount++;
        statsInterval += period;

        if (statsInterval >= MAX_STATS_INTERVAL) {
            long timeNow = J3DTimer.getValue();
            //long timeNow = System.currentTimeMillis();
            timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000000000L);
            //timeSpentInGame = (int) ((timeNow - gameStartTime) / 1000L);
            //wcTop.setTimeSpent(timeSpentInGame);

            long realElapsedTime = timeNow - prevStatsTime;
            totalElapsedTime += realElapsedTime;

            double timingError = (double) ((realElapsedTime - statsInterval) / statsInterval) * 100;
            totalFramesSkipped += framesSkipped;

            double actualFPS = 0;
            double actualUPS = 0;
            if (totalElapsedTime > 0) {
                actualFPS = (((double) (frameCount / totalElapsedTime) * 1000000000L));
                actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
                //actualFPS = (((double) (frameCount / totalElapsedTime) * 1000L));
                //actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000L);
            }

            fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
            upsStore[(int) statsCount % NUM_FPS] = actualUPS;
            statsCount = statsCount + 1;

            double totalFPS = 0;
            double totalUPS = 0;
            for (int i = 0; i < NUM_FPS; i++) {
                totalFPS += fpsStore[i];
                totalUPS += upsStore[i];
            }

            if (statsCount < NUM_FPS) {
                averageFPS = totalFPS / statsCount;
                averageUPS = totalUPS / statsCount;
            } else {
                averageFPS = totalFPS / NUM_FPS;
                averageUPS = totalUPS / NUM_FPS;
            }

            System.out.println(
                    timedf.format((double) statsInterval / 1000000000L) + " " +
                            timedf.format((double) realElapsedTime / 1000000000L) + "s " +
                            df.format(timingError) + "% " + frameCount + "c " +
                            framesSkipped + "/" + totalFramesSkipped + " skip; " +
                            df.format(actualFPS) + " " + df.format(averageFPS) + " afps; " +
                            df.format(actualUPS) + " " + df.format(averageUPS) + " aups"
            );

            framesSkipped = 0;
            prevStatsTime = timeNow;
            statsInterval = 0L;
        }
    }

    private void finishOff() {
        if (!finishedOff) {
            finishedOff = true;
            printStats();
            System.exit(0);
        }
    }

    private void printStats() {
        System.out.println("Frame Count/Loss : " + frameCount + " / " + totalFramesSkipped);
        System.out.println("Average FPS : " + df.format(averageFPS));
        System.out.println("Average UPS : " + df.format(averageUPS));
        System.out.println("Time Spent : " + timeSpentInGame + " secs");
        //System.out.println("Boxes used : " + obs.getNumObstacles());
        System.out.println("Boxes used : " + boxesUsed);
    }
}
*/
