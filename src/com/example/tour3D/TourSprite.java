package com.example.tour3D;

public class TourSprite extends Sprite3D {

    private static final double MOVERATE = 0.3;
    private static final double ROTATE_AMT = Math.PI / 16.0;

    public TourSprite(String fnm, Obstacles obstacles) {
        super(fnm, obstacles);
    }

    public boolean moveForward() {
        return moveBy(0.0, MOVERATE);
    }

    public boolean moveBackward() {
        return moveBy(0.0, -MOVERATE);
    }

    public boolean moveLeft() {
        return moveBy(-MOVERATE, 0.0);
    }

    public boolean moveRight() {
        return moveBy(MOVERATE, 0.0);
    }

    public void rotClock() {
        doRotateY(-ROTATE_AMT);
    }

    public void rotCounterClock() {
        doRotateY(ROTATE_AMT);
    }
}
