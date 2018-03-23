package com.example.bugrunner;

public interface SoundsWatcher {

    public static final int STOPPED = 0;
    public static final int REPLAYED = 1;

    void atSequenceEnd(String filename, int status);

}
