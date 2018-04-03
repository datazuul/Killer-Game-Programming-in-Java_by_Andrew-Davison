package com.example.jumpingjack;

import java.awt.*;

public class FireBallSprite extends Sprite {

    private static final int STEP = -10;
    private static final int STEP_OFFSET = 2;

    private JackPanel jp;
    private JumperSprite jack;

    public FireBallSprite(int w, int h, ImagesLoader imsLd, JackPanel jp, JumperSprite j) {
        super(w, h / 2, w, h, imsLd, "fireball");
        this.jp = jp;
        jack = j;
        initPosition();
    }

    private void initPosition() {
        int h = getPHeight() / 2 + ((int) (getPHeight() * Math.random()) / 2);
        if (h + getHeight() > getPHeight()) {
            h -= getHeight();
        }

        setPosition(getPWidth(), h);
        setStep(STEP + getRandRange(STEP_OFFSET), 0);
    }

    private int getRandRange(int x) {
        return ((int) (2 * x * Math.random())) - x;
    }

    public void updateSprite() {
        hasHitJack();
        goneOffScreen();
        super.updateSprite();
    }

    private void hasHitJack() {
        Rectangle jackBox = jack.getMyRectangle();
        jackBox.grow(-jackBox.width / 3, 0);

        if (jackBox.intersects(getMyRectangle())) {
            jp.showExplosion(locx, locy + getHeight() / 2);
            initPosition();
        }
    }

    private void goneOffScreen() {
        if (((locx + getWidth()) <= 0) && (dx < 0)) {
            initPosition();
        }
    }
}
