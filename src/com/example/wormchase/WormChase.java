package com.example.wormchase;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;

//public class WormChase extends JFrame implements WindowListener {
//public class WormChase extends JApplet {
//public class WormChase extends JFrame {
public class WormChase extends JFrame implements Runnable {
    private static final int NUM_BUFFERS = 2;

    private static int DEFAULT_FPS = 100;

    private static long MAX_STATS_INTERVAL = 1000000000L;

    private static final int NO_DELAYS_PER_YIELD = 16;

    private static int MAX_FRAME_SKIPS = 5;

    private static int NUM_FPS = 10;

    //private WormPanel wp;
    //private JTextField jtfBox;
    //private JTextField jtfTime;

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
    private volatile boolean running = false;

    private long period;

    private Worm fred;
    private Obstacles obs;
    private int boxesUsed = 0;

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

    private GraphicsDevice gd;
    private Graphics gScr;
    private BufferStrategy bufferStrategy;

    public WormChase(long period) {
        super("The Worm Chase");
        //makeGUI(period);
        //makeGUI();

        //addWindowListener(this);
        //pack();
        //setResizable(false);
        //calcSizes();
        //setResizable(true);
        //setVisible(true);

        this.period = period;
        initFullScreen();
        readyForTermination();

        obs = new Obstacles(this);
        fred = new Worm(pWidth, pHeight, obs);

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
        quitArea = new Rectangle(pWidth - 100, pHeight - 45, 70, 15);

        fpsStore = new double[NUM_FPS];
        upsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++) {
            fpsStore[i] = 0;
            upsStore[i] = 0;
        }

        gameStart();

        /**Container c = getContentPane();
         c.setLayout(new BorderLayout());

         wp = new WormPanel(this, period);
         //wp = new WormPanel(this, period, pWidth, pHeight);
         c.add(wp, "Center");

         setUndecorated(true);
         setIgnoreRepaint(true);
         pack();
         setResizable(false);
         setVisible(true);

         /**addWindowListener(this);

         addComponentListener(new ComponentAdapter() {
         //@Override
         public void componentMoved(ComponentEvent e) {
         //super.componentMoved(e);
         setLocation(0, 0);
         }
         });

         setResizable(false);
         setVisible(true);**/
    }

    /**
     * public void init() {
     * String str = getParameter("fps");
     * int fps = (str != null) ? Integer.parseInt(str) : DEFAULT_FPS;
     * <p>
     * long period = (long) 1000 / fps;
     * System.out.println("fps : " + fps + "; period : " + period + " ms");
     * <p>
     * makeGUI(period);
     * wp.startGame();
     * }
     **/

    private void initFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();

        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);

        if (!gd.isFullScreenSupported()) {
            System.out.println("Full-screen exclusive mode not supported");
            System.exit(0);
        }
        gd.setFullScreenWindow(this);

        showCurrentMode();

        pWidth = getBounds().width;
        pHeight = getBounds().height;

        setBufferStrategy();
    }

    private void reportCapabilities() {
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        ImageCapabilities imageCapabilities = gc.getImageCapabilities();
        System.out.println("Image Capabilities isAccelerated : " + imageCapabilities.isAccelerated());
        System.out.println("Image Capabilities isTrueVolatile : " + imageCapabilities.isTrueVolatile());

        BufferCapabilities bufferCapabilities = gc.getBufferCapabilities();
        System.out.println("Buffer Capabilities isPageFlipping : " + bufferCapabilities.isPageFlipping());
        System.out.println("Buffer Capabilities Flip Contents : " + getFlipText(bufferCapabilities.getFlipContents()));
        System.out.println("Buffer Capabilities Full-screen Required : " + bufferCapabilities.isFullScreenRequired());
        System.out.println("Buffer Capabilities MultiBuffers : " + bufferCapabilities.isMultiBufferAvailable());
    }

    private String getFlipText(BufferCapabilities.FlipContents flip) {
        if (flip == null) {
            return "false";
        } else if (flip == BufferCapabilities.FlipContents.UNDEFINED) {
            return "Undefined";
        } else if (flip == BufferCapabilities.FlipContents.BACKGROUND) {
            return "Background";
        } else if (flip == BufferCapabilities.FlipContents.PRIOR) {
            return "Prior";
        } else {
            return "Copied";
        }
    }

    private void setBufferStrategy() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    createBufferStrategy(NUM_BUFFERS);
                }
            });
        } catch (Exception e) {
            System.out.println("Error while creating buffer strategy");
            System.exit(0);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {

        }
        bufferStrategy = getBufferStrategy();
    }

    private void readyForTermination() {
        addKeyListener(new KeyAdapter() {
            @Override
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
                finishOff();
            }
        });
    }

    private void gameStart() {
        if (animator == null || !running) {
            animator = new Thread(this);
            animator.start();
        }
    }

    private void testPress(int x, int y) {
        if (isOverPauseButton) {
            isPaused = !isPaused;
        } else if (isOverQuitButton) {
            running = false;
        } else {
            if (!isPaused && !gameOver) {
                if (fred.nearHead(x, y)) {
                    gameOver = true;
                    score = (40 - timeSpentInGame) + (40 - boxesUsed);
                } else {
                    if (!fred.touchedAt(x, y)) {
                        obs.add(x, y);
                    }
                }
            }
        }
    }

    private void testMove(int x, int y) {
        if (running) {
            isOverPauseButton = pauseArea.contains(x, y) ? true : false;
            isOverQuitButton = quitArea.contains(x, y) ? true : false;
        }
    }


    //private void makeGUI(long/*int*/ period) {
    /*private void makeGUI() {
        Container c = getContentPane();
        //c.setLayout(new BorderLayout());

        //wp = new WormPanel(this, period * 1000000L);
        //c.add(wp, "Center");

        JPanel ctrls = new JPanel();
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

        jtfBox = new JTextField("Boxes used : 0");
        jtfBox.setEditable(false);
        ctrls.add(jtfBox);

        jtfTime = new JTextField("Time Spent : 0 secs");
        jtfTime.setEditable(false);
        ctrls.add(jtfTime);

        c.add(ctrls, "South");
    }*/

    public void setBoxNumber(int no) {
        //jtfBox.setText("Boxes used : " + no);
        boxesUsed = no;
    }

    public void run() {
        long beforeTime, afterTime, timeDiff, sleepTime;
        long overSleepTime = 0L;
        int noDelays = 0;
        long excess = 0L;

        gameStartTime = J3DTimer.getValue();
        prevStatsTime = gameStartTime;
        beforeTime = gameStartTime;

        running = true;

        while (running) {
            gameUpdate();
            screenUpdate();

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

            framesSkipped += skips;
            storeStats();
        }

        finishOff();
    }

    private void gameUpdate() {
        if (!isPaused && !gameOver) {
            fred.move();
        }
    }

    private void screenUpdate() {
        try {
            gScr = bufferStrategy.getDrawGraphics();
            gameRender(gScr);
            gScr.dispose();

            if (!bufferStrategy.contentsLost()) {
                bufferStrategy.show();
            } else {
                System.out.println("Contents Lost");
            }

            Toolkit.getDefaultToolkit().sync();
        } catch (Exception e) {
            e.printStackTrace();
            running = false;
        }
    }

    private void gameRender(Graphics gScr) {
        gScr.setColor(Color.white);
        gScr.fillRect(0, 0, pWidth, pHeight);

        gScr.setColor(Color.blue);
        gScr.setFont(font);

        gScr.drawString("Average FPS/UPS : " + df.format(averageFPS) + ", " +
                df.format(averageUPS), 20, 25);
        gScr.drawString("Time Spent : " + timeSpentInGame + " secs", 10, pHeight - 15);
        gScr.drawString("Boxes Used : " + boxesUsed, 260, pHeight - 15);

        drawButtons(gScr);
        gScr.setColor(Color.black);

        obs.draw(gScr);
        fred.draw(gScr);

        if (gameOver) {
            gameOverMessage(gScr);
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
        String msg = "Game Over. Your Score : " + score;
        int x = (pWidth - metrics.stringWidth(msg)) / 2;
        int y = (pHeight - metrics.getHeight()) / 2;

        g.setColor(Color.red);
        g.setFont(font);
        g.drawString(msg, x, y);
    }

    private void storeStats() {
        frameCount++;
        statsInterval += period;

        if (statsInterval >= MAX_STATS_INTERVAL) {
            long timenow = J3DTimer.getValue();
            timeSpentInGame = (int) ((timenow - gameStartTime) / 1000000000L);

            long realElapsedTime = timenow - prevStatsTime;
            totalElapsedTime += realElapsedTime;

            double timingError = ((double) (realElapsedTime - statsInterval) / statsInterval) * 100;
            totalFramesSkipped += framesSkipped;

            double actualFPS = 0;
            double actualUPS = 0;
            if (totalElapsedTime > 0) {
                actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
                actualUPS = (((double) (frameCount + totalFramesSkipped) / totalElapsedTime) * 1000000000L);
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

            framesSkipped = 0;
            prevStatsTime = timenow;
            statsInterval = 0L;
        }
    }

    private void finishOff() {
        if (!finishedOff) {
            finishedOff = true;
            printStats();
            restoreScreen();
            System.exit(0);
        }
    }

    private void printStats() {
        System.out.println("Frame Count/Loss : " + frameCount + " /" + totalFramesSkipped);
        System.out.println("Average FPS : " + df.format(averageFPS));
        System.out.println("Average UPS : " + df.format(averageUPS));
        System.out.println("Time Spent : " + timeSpentInGame + " secs");
        System.out.println("Boxes used : " + boxesUsed);
    }

    private void restoreScreen() {
        Window w = gd.getFullScreenWindow();
        if (w != null) {
            w.dispose();
        }
        gd.setFullScreenWindow(null);
    }

    private void setDisplayMode(int width, int height, int bitDepth) {
        if (!gd.isDisplayChangeSupported()) {
            System.out.println("Display mode changing not supported");
            return;
        }

        if (!isDisplayModeAvailable(width, height, bitDepth)) {
            System.out.println("Display mode ( " + width + ", " +
                    height + ", " + bitDepth + " ) not available");
            return;
        }

        DisplayMode dm = new DisplayMode(width, height, bitDepth,
                DisplayMode.REFRESH_RATE_UNKNOWN);
        try {
            gd.setDisplayMode(dm);
            System.out.println("Display mode set to : ( " + width + ", " +
                    height + ", " + bitDepth + " )");
        } catch (IllegalArgumentException e) {
            System.out.println("Error setting Display mode ( " + width + ", " +
                    height + ", " + bitDepth + " )");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {

        }
    }

    private boolean isDisplayModeAvailable(int width, int height, int bitDepth) {
        DisplayMode[] modes = gd.getDisplayModes();
        showModes(modes);

        for (int i = 0; i < modes.length; i++) {
            if (width == modes[i].getWidth() && height == modes[i].getHeight() &&
                    bitDepth == modes[i].getBitDepth()) {
                return true;
            }
        }
        return false;
    }

    private void showModes(DisplayMode[] modes) {
        System.out.println("Modes");
        for (int i = 0; i < modes.length; i++) {
            System.out.println("( " + modes[i].getWidth() + ", " +
                    modes[i].getHeight() + ", " +
                    modes[i].getBitDepth() + ", " +
                    modes[i].getRefreshRate() + " ) ");

            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    private void showCurrentMode() {
        DisplayMode dm = gd.getDisplayMode();
        System.out.println("Current Display Mode : ( " +
                dm.getWidth() + ", " + dm.getHeight() + ", " +
                dm.getBitDepth() + ", " + dm.getRefreshRate() + " ) ");
    }

    /*public void setTimeSpent(int t) {
        jtfTime.setText("Time spent : " + t + " secs");
    }

    private void calcSizes() {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        Rectangle screenRect = gc.getBounds();
        System.out.println("Screen size : " + screenRect);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Insets desktopInsets = tk.getScreenInsets(gc);
        System.out.println("OS Insets : " + desktopInsets);

        Insets frameInsets = getInsets();
        System.out.println("JFrame Insets : " + frameInsets);

        Dimension tfdim = jtfBox.getPreferredSize();
        System.out.println("Box TF Size : " + tfdim);
        System.out.println("Time TF Size : " + jtfTime.getPreferredSize());

        pWidth = screenRect.width - (desktopInsets.left + desktopInsets.right)
                - (frameInsets.left + frameInsets.right);
        pHeight = screenRect.height - (desktopInsets.top + desktopInsets.bottom)
                - (frameInsets.top + frameInsets.bottom)
                - (tfdim.height);
        System.out.println("pWidth : " + pWidth + "; pHeight : " + pHeight);
    }*/

    /**
     * public void windowActivated(WindowEvent e) {
     * wp.resumeGame();
     * }
     * <p>
     * public void windowDeactivated(WindowEvent e) {
     * wp.pauseGame();
     * }
     * <p>
     * public void windowDeiconified(WindowEvent e) {
     * wp.resumeGame();
     * }
     * <p>
     * public void windowIconified(WindowEvent e) {
     * wp.pauseGame();
     * }
     * <p>
     * public void windowClosing(WindowEvent e) {
     * wp.stopGame();
     * }
     * <p>
     * public void windowClosed(WindowEvent e) {
     * <p>
     * }
     * <p>
     * public void windowOpened(WindowEvent e) {
     * <p>
     * }
     **/

    public static void main(String[] args) {
        int fps = DEFAULT_FPS;
        if (args.length != 0) {
            fps = Integer.parseInt(args[0]);
        }

        long period = (long) 1000 / fps;
        //int period = (int) 1000 / fps;
        System.out.println("fps : " + fps + "; period : " + period + " ms");
        new WormChase(period * 1000000L);
        //new WormChase(period);
    }

    /**public void start() {
     wp.resumeGame();
     }

     public void stop() {
     wp.pauseGame();
     }

     public void destroy() {
     wp.stopGame();
     }**/
}
