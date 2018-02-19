package com.example.test;

import com.sun.j3d.utils.timer.J3DTimer;

import java.text.DecimalFormat;

public class SleepAcc {
    private static DecimalFormat df;

    public static void main(String[] args) {
        df = new DecimalFormat("0.##");

        sleepTest(1000);
        sleepTest(500);
        sleepTest(200);
        sleepTest(100);
        sleepTest(50);
        sleepTest(20);
        sleepTest(10);
        sleepTest(5);
        sleepTest(1);
    }

    private static void sleepTest(int delay) {
        long timeStart = J3DTimer.getValue();

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {

        }

        double timeDiff = ((double) (J3DTimer.getValue() - timeStart)) / (1000000L);
        double err = ((delay - timeDiff) / timeDiff) * 100;

        System.out.println("Slept : " + delay + " ms J3D : " +
                df.format(timeDiff) + " ms err : " + df.format(err) + " %");
    }
}
