package com.example.nettour3D;

public class DistanceTourSprite extends Sprite3D {

    private static final double MOVE_RATE = 0.3;
    private static final double ROTATION_AMOUNT = Math.PI / 16.0;

    public DistanceTourSprite(String userName, String fileName, Obstacles obstacles, double xPosition, double zPosition) {
        super(userName, fileName, obstacles);
        setPosition(xPosition, zPosition);
    }

    public boolean moveForward() {
        return moveBy(0.0, MOVE_RATE);
    }

    public boolean moveBackward() {
        return moveBy(0.0, -MOVE_RATE);
    }

    public boolean moveLeft() {
        return moveBy(-MOVE_RATE, 0.0);
    }

    public boolean moveRight() {
        return moveBy(MOVE_RATE, 0.0);
    }

    public void rotationClock() {
        doRotateY(-ROTATION_AMOUNT);
    }

    public void rotationCounterClock() {
        doRotateY(ROTATION_AMOUNT);
    }
}
