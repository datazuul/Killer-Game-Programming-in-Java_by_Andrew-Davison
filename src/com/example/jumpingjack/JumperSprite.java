package com.example.jumpingjack;

public class JumperSprite extends Sprite {

    private static double DURATION = 0.5;

    private static final int NOT_JUMPING = 0;
    private static final int RISING = 1;
    private static final int FALLING = 2;

    private static final int MAX_UP_STEPS = 8;

    private int period;
    private boolean isFacingRight, isStill;

    private int vertMoveMode;
    private int vertStep;
    private int upCount;

    private BricksManager bricksManager;
    private int moveSize;

    private int xWorld, yWorld;

    public JumperSprite(int w, int h, int brickMvSz, BricksManager bm, ImagesLoader imsLd, int p) {
        super(w / 2, h / 2, w, h, imsLd, "runningRight");
        moveSize = brickMvSz;
        bricksManager = bm;
        period = p;
        setStep(0, 0);

        isFacingRight = true;
        isStill = true;

        locy = bricksManager.findFloor(locx + getWidth() / 2) - getHeight();
        xWorld = locx;
        yWorld = locy;

        vertMoveMode = NOT_JUMPING;
        vertStep = bricksManager.getBrickHeight() / 2;

        upCount = 0;
    }

    public void moveLeft() {
        setImage("runningLeft");
        loopImage(period, DURATION);
        isFacingRight = false;
        isStill = false;
    }

    public void moveRight() {
        setImage("runningRight");
        loopImage(period, DURATION);
        isFacingRight = true;
        isStill = false;
    }

    public void stayStill() {
        stopLooping();
        isStill = true;
    }

    public void jump() {
        if (vertMoveMode == NOT_JUMPING) {
            vertMoveMode = RISING;
            upCount = 0;
            if (isStill) {
                if (isFacingRight) {
                    setImage("jumpRight");
                } else {
                    setImage("jumpLeft");
                }
            }
        }
    }

    public boolean willHitBrick() {
        if (isStill) {
            return false;
        }

        int xTest;
        if (isFacingRight) {
            xTest = xWorld + moveSize;
        } else {
            xTest = xWorld - moveSize;
        }

        int xMid = xTest + getWidth() / 2;
        int yMid = yWorld + (int) (getHeight() * 0.8);

        return bricksManager.insideBrick(xMid, yMid);
    }

    public void updateSprite() {
        if (!isStill) {
            if (isFacingRight) {
                xWorld += moveSize;
            } else {
                xWorld -= moveSize;
            }
            if (vertMoveMode == NOT_JUMPING) {
                checkIfFalling();
            }
        }

        if (vertMoveMode == RISING) {
            updateRising();
        } else if (vertMoveMode == FALLING) {
            updateFalling();
        }

        super.updateSprite();
    }

    private void checkIfFalling() {
        int yTrans = bricksManager.checkBrickTop(xWorld + (getWidth() / 2), yWorld + getHeight() + vertStep, vertStep);
        if (yTrans != 0) {
            vertMoveMode = FALLING;
        }
    }

    private void updateRising() {
        if (upCount == MAX_UP_STEPS) {
            vertMoveMode = FALLING;
            upCount = 0;
        } else {
            int yTrans = bricksManager.checkBrickBase(xWorld + (getWidth() / 2), yWorld - vertStep, vertStep);

            if (yTrans == 0) {
                vertMoveMode = FALLING;
                upCount = 0;
            } else {
                translate(0, -yTrans);
                yWorld -= yTrans;
                upCount++;
            }
        }
    }

    private void updateFalling() {
        int yTrans = bricksManager.checkBrickTop(xWorld + (getWidth() / 2), yWorld + getHeight() + vertStep, vertStep);

        if (yTrans == 0) {
            finishJumping();
        } else {
            translate(0, yTrans);
            yWorld += yTrans;
        }
    }

    private void finishJumping() {
        vertMoveMode = NOT_JUMPING;
        upCount = 0;

        if (isStill) {
            if (isFacingRight) {
                setImage("runningRight");
            } else {
                setImage("runningLeft");
            }
        }
    }
}
