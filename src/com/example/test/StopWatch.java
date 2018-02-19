package com.example.test;

import sun.misc.Perf;

public class StopWatch {
    private Perf hiResTimer;
    private long freq;
    private long startTime;

    public StopWatch() {
        hiResTimer = Perf.getPerf();
        freq = hiResTimer.highResFrequency();
    }

    public void start() {
        startTime = hiResTimer.highResCounter();
    }

    public long stop() {
        return (hiResTimer.highResCounter() - startTime) * 1000000000L / freq;
    }

    public long getResolution() {
        long diff, count1, count2;

        count1 = hiResTimer.highResCounter();
        count2 = hiResTimer.highResCounter();
        while (count1 == count2) {
            count2 = hiResTimer.highResCounter();
        }
        diff = (count2 - count1);

        count1 = hiResTimer.highResCounter();
        count2 = hiResTimer.highResCounter();
        while (count1 == count2) {
            count2 = hiResTimer.highResCounter();
        }
        diff += (count2 - count1);

        count1 = hiResTimer.highResCounter();
        count2 = hiResTimer.highResCounter();
        while (count1 == count2) {
            count2 = hiResTimer.highResCounter();
        }
        diff += (count2 - count1);

        count1 = hiResTimer.highResCounter();
        count2 = hiResTimer.highResCounter();
        while (count1 == count2) {
            count2 = hiResTimer.highResCounter();
        }
        diff += (count2 - count1);

        return (diff * 1000000000L) / (4 * freq);
    }
}
