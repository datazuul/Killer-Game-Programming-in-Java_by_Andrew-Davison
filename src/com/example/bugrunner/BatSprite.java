package com.example.bugrunner;

public class BatSprite extends Sprite {

    private static double DURATION = 0.5;
    private static final int FLOOR_DIST = 41;
    private static final int XSTEP = 10;

    private int period;

    public BatSprite(int w, int h, ImagesLoader imsLd, int p) {
        super(w / 2, h - FLOOR_DIST, w, h, imsLd, "leftBugs2");
        period = p;
        setStep(0, 0);
    }

    public void moveLeft() {
        setStep(-XSTEP, 0);
        setImage("leftBugs2");
        loopImage(period, DURATION);
    }

    public void moveRight() {
        setStep(XSTEP, 0);
        setImage("rightBugs2");
        loopImage(period, DURATION);
    }

    public void stayStill() {
        setStep(0, 0);
        stopLooping();
    }

    public void updateSprite() {
        if ((locx + getWidth() <= 0) && (dx < 0)) {
            locx = getPWidth() - 1;
        } else if ((locx >= getPWidth() - 1) && (dx > 0)) {
            locx = 1 - getWidth();
        }

        super.updateSprite();
    }

    public void mouseMove(int xCoord) {
        if (xCoord < locx) {
            moveLeft();
        } else if (xCoord > (locx + getWidth())) {
            moveRight();
        } else {
            stayStill();
        }
    }
}
