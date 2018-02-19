package com.example.test;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    private PaintPanel pp;

    public MyTimerTask(PaintPanel pp) {
        this.pp = pp;
    }

    @Override
    public void run() {
        sillyTask();
        pp.repaint();
    }

    private void sillyTask() {
        long tot = 0;
        for (long i = 0; i < 200000L; i++) {
            tot += i;
        }
    }
}
