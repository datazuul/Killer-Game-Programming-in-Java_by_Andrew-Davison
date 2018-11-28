package com.example.nettour3D;

import java.io.PrintWriter;

public class TourSprite extends Sprite3D {

    private static final double MOVE_RATE = 0.3;
    private static final double ROTATE_AMOUNT = Math.PI / 16.0;

    PrintWriter printWriter;

    public TourSprite(String userName, String fileName, Obstacles obstacles, double xPosition, double zPosition,
                      PrintWriter printWriter) {
        super(userName, fileName, obstacles);
        setPosition(xPosition, zPosition);
        this.printWriter = printWriter;
        printWriter.println("create " + userName + " " + xPosition + " " + zPosition);
    }

    public boolean moveForward() {
        printWriter.println("forward");
        return moveBy(0.0, MOVE_RATE);
    }

    public boolean moveBackward() {
        printWriter.println("back");
        return moveBy(0.0, -MOVE_RATE);
    }

    public boolean moveLeft() {
        printWriter.println("left");
        return moveBy(-MOVE_RATE, 0.0);
    }

    public boolean moveRight() {
        printWriter.println("right");
        return moveBy(MOVE_RATE, 0.0);
    }

    public void rotationClock() {
        printWriter.println("rotationClock");
        doRotateY(-ROTATE_AMOUNT);
    }

    public void rotationCounterClock() {
        printWriter.println("rotationCounterClock");
        doRotateY(ROTATE_AMOUNT);
    }
}
