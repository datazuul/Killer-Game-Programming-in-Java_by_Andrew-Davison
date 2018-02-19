package com.example.test;

import com.sun.j3d.utils.timer.J3DTimer;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class PaintPanel extends JPanel {
    private static final int PWIDTH = 200;
    private static final int PHEIGHT = 75;

    private static long MAX_STATS_INTERVAL = 1000L;
    private static int NUM_FPS = 10;

    private long statsInterval = 0L;
    private long prevStatsTime;
    private long totalElapsedTime = 0L;
    private long frameCount = 0;
    private long statsCount = 0;

    private double fpsStore[];
    private double averageFPS = 0.0;

    private DecimalFormat df = new DecimalFormat("0.##");
    private DecimalFormat timedf = new DecimalFormat("0.####");

    private int period;

    public PaintPanel(int p) {
        period = p;

        setBackground(Color.white);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        fpsStore = new double[NUM_FPS];
        for (int i = 0; i < NUM_FPS; i++) {
            fpsStore[i] = 0.0;
        }
        prevStatsTime = J3DTimer.getValue();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0, 0, PWIDTH, PHEIGHT);

        g.setColor(Color.black);
        g.drawString("Average FPS : " + df.format(averageFPS), 10, 25);

        reportStats();
    }

    private void reportStats() {
        frameCount++;
        statsInterval += period;

        if (statsInterval >= MAX_STATS_INTERVAL) {
            long timeNow = J3DTimer.getValue();
            long realElapsedTime = timeNow - prevStatsTime;
            totalElapsedTime += realElapsedTime;
            long sInterval = (long) statsInterval * 1000000L;
            double timingError = ((double) (realElapsedTime - sInterval)) / sInterval * 100;
            double actualFPS = 0;
            if (totalElapsedTime > 0) {
                actualFPS = (((double) frameCount / totalElapsedTime) * 1000000000L);
            }
            fpsStore[(int) statsCount % NUM_FPS] = actualFPS;
            statsCount = statsCount + 1;
            double totalFPS = 0.0;
            for (int i = 0; i < NUM_FPS; i++) {
                totalFPS += fpsStore[i];
            }
            if (statsCount < NUM_FPS) {
                averageFPS = totalFPS / statsCount;
            } else {
                averageFPS = totalFPS / NUM_FPS;
            }
            System.out.println(
                    timedf.format((double) statsInterval / 1000) + " " +
                            timedf.format((double) realElapsedTime / 1000000000L) + "s " +
                            df.format(timingError) + "% " +
                            frameCount + "c " +
                            df.format(actualFPS) + " " +
                            df.format(averageFPS) + " afps"
            );
            prevStatsTime = timeNow;
            statsInterval = 0L;
        }
    }
}
