package com.example.soundplayer;

public class PanChanger extends Thread {

    private static int CYCLE_PERIOD = 4000;
    private int[] panVals = {0, 127};

    private PanMidi player;
    private int duration;

    public PanChanger(PanMidi p) {
        super("PanChanger");
        player = p;
    }

    public void startChanging(int d) {
        duration = d;
        start();
    }

    public void run() {
        int pan = player.getMaxPan();
        System.out.println("Max Pan : " + pan);

        int panValsIdx = 0;
        int timeCount = 0;
        int delayPeriod = (int) (CYCLE_PERIOD / panVals.length);

        System.out.println("panning");
        while (timeCount < duration) {
            try {
                if (player != null) {
                    player.setPan(panVals[panValsIdx]);
                }
                Thread.sleep(delayPeriod);
            } catch (InterruptedException e) {

            }
            System.out.print(".");
            panValsIdx = (panValsIdx + 1) % panVals.length;
            timeCount += delayPeriod;
        }
        System.out.println();
    }
}
