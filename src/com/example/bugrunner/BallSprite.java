package com.example.bugrunner;

import java.awt.*;

public class BallSprite extends Sprite {

    private static final int STEP = 8;
    private static final int STEP_OFFSET = 2;
    private static final String[] ballNames = {"rock1", "orangeRock", "computer", "ball"};
    private static final int MAX_BALLS_RETURNED = 16;

    private int nameIndex;
    private ClipsLoader clipsLoader;

    private BugPanel bp;
    private BatSprite bat;

    private int numRebounds;

    public BallSprite(int w, int h, ImagesLoader imsLd, ClipsLoader cl, BugPanel bp, BatSprite b) {
        super(w / 2, 0, w, h, imsLd, ballNames[0]);
        clipsLoader = cl;
        this.bp = bp;
        bat = b;

        nameIndex = 0;
        numRebounds = MAX_BALLS_RETURNED / 2;
        initPosition();
    }

    private void initPosition() {
        setImage(ballNames[nameIndex]);
        nameIndex = (nameIndex + 1) % ballNames.length;

        setPosition((int) (getPWidth() * Math.random()), 0);

        int step = STEP + getRandRange(STEP_OFFSET);
        int xStep = ((Math.random() < 0.5) ? -step : step);
        setStep(xStep, STEP + getRandRange(STEP_OFFSET));
    }

    private int getRandRange(int x) {
        return ((int) (2 * x * Math.random())) - x;
    }

    public void updateSprite() {
        hasHitBat();
        goneOffScreen();
        hasHitWall();

        super.updateSprite();
    }

    private void hasHitBat() {
        Rectangle rectangle = getMyRectangle();
        if (rectangle.intersects(bat.getMyRectangle())) {
            clipsLoader.play("hitBat", false);
            Rectangle interRect = rectangle.intersection(bat.getMyRectangle());
            dy = -dy;
            locy -= interRect.height;
        }
    }

    private void goneOffScreen() {
        if (((locy + getHeight()) <= 0) && (dy < 0)) {
            numRebounds++;
            if (numRebounds == MAX_BALLS_RETURNED) {
                bp.gameOver();
            } else {
                initPosition();
            }
        } else if ((locy >= getPHeight()) && (dy > 0)) {
            numRebounds--;
            if (numRebounds == 0) {
                bp.gameOver();
            } else {
                initPosition();
            }
        }
    }

    private void hasHitWall() {
        if ((locx <= 0) && (dx < 0)) {
            clipsLoader.play("hitLeft", false);
            dx = -dx;
        } else if ((locx + getWidth() >= getPWidth()) && (dx > 0)) {
            clipsLoader.play("hitRight", false);
            dx = -dx;
        }
    }

    public void drawBallStats(Graphics graphics, int x, int y) {
        graphics.drawString("Returns : " + numRebounds + "/" + MAX_BALLS_RETURNED, x, y);
    }
}
