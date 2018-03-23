package com.example.bugrunner;

import java.awt.image.BufferedImage;

public class ImagesPlayer {

    private String imName;
    private boolean isRepeating, ticksIgnored;
    private ImagesLoader imagesLoader;

    private int animPeriod;
    private long animTotalTime;
    private int showPeriod;
    private double seqDuration;
    private int numImages;
    private int imPosition;

    private ImagesPlayerWatcher watcher = null;

    public ImagesPlayer(String nm, int ap, double d, boolean isr, ImagesLoader il) {
        imName = nm;
        animPeriod = ap;
        seqDuration = d;
        isRepeating = isr;
        imagesLoader = il;

        animTotalTime = 0L;

        if (seqDuration < 0.5) {
            System.out.println("Warning. minimum sequence duration is 0.5 sec");
            seqDuration = 0.5;
        }

        if (!imagesLoader.isLoaded(imName)) {
            System.out.println(imName + " is not known by the ImagesLoader");
            numImages = 0;
            imPosition = -1;
            ticksIgnored = true;
        } else {
            numImages = imagesLoader.numImages(imName);
            imPosition = 0;
            ticksIgnored = false;
            showPeriod = (int) (1000 * seqDuration / numImages);
        }
    }

    public void updateTick() {
        if (!ticksIgnored) {
            animTotalTime = (animTotalTime + animPeriod) % (long) (1000 * seqDuration);
            imPosition = (int) (animTotalTime / showPeriod);
            if ((imPosition == numImages - 1) && (!isRepeating)) {
                ticksIgnored = true;
                if (watcher != null) {
                    watcher.sequenceEnded(imName);
                }
            }
        }
    }

    public BufferedImage getCurrentImage() {
        if (numImages != 0) {
            return imagesLoader.getImage(imName, imPosition);
        } else {
            return null;
        }
    }

    public int getCurrentPosition() {
        return imPosition;
    }

    public void setWatcher(ImagesPlayerWatcher w) {
        watcher = w;
    }

    public void stop() {
        ticksIgnored = true;
    }

    public boolean isStopped() {
        return ticksIgnored;
    }

    public boolean atSequenceEnd() {
        return ((imPosition == numImages - 1) && (!isRepeating));
    }

    public void restartAt(int imPosn) {
        if (numImages != 0) {
            if ((imPosn < 0) || (imPosn > numImages - 1)) {
                System.out.println("Out of range restart, starting at 0");
                imPosn = 0;
            }

            imPosition = imPosn;
            animTotalTime = (long) imPosition * showPeriod;
            ticksIgnored = false;
        }
    }

    public void resume() {
        if (numImages != 0) {
            ticksIgnored = false;
        }
    }
}
