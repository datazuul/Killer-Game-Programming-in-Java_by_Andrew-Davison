package com.example.soundplayer;

public class VolChanger extends Thread {

    private static int PERIOD = 500;
    private FadeMidi player;
    private int numChanges = 0;

    public VolChanger(FadeMidi p) {
        super("VolChanger");
        player = p;
    }

    public void startChanging(int duration) {
        numChanges = (int) duration / PERIOD;
        start();
    }

    public void run() {
        int volume = player.getMaxVolume();
        int stepVolume = (int) volume / numChanges;
        if (stepVolume == 0) {
            stepVolume = 1;
        }
        System.out.println("Max Volume : " + volume + ", step : " + stepVolume);

        int counter = 0;
        System.out.print("Fading");
        while (counter < numChanges) {
            try {
                volume -= stepVolume;
                if ((volume >= 0) && (player != null)) {
                    player.setVolume(volume);
                }
                Thread.sleep(PERIOD);
            } catch (InterruptedException e) {

            }
            System.out.print(".");
            counter++;
        }
        System.out.println();
    }
}
