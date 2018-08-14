package com.example.tour3D;

import javax.vecmath.Point3d;
import java.text.DecimalFormat;

public class AlienSprite extends TourSprite {

    private static final double CLOSE_DIST = 0.2;

    private TourSprite tourSprite;
    private double currAngle;
    private DecimalFormat decimalFormat;

    public AlienSprite(String fnm, Obstacles obstacles, TourSprite tourSprite) {
        super(fnm, obstacles);
        decimalFormat = new DecimalFormat("0.###");
        this.tourSprite = tourSprite;
        currAngle = 0.0;
    }

    public void update() {
        if (isActive()) {
            headTowardsTourist();
            if (closeTogether(getCurrLoc(), tourSprite.getCurrLoc())) {
                System.out.println("Alien and Tourist are close together");
            }
        }
    }

    private void headTowardsTourist() {
        double rotAngle = calcTurn(getCurrLoc(), tourSprite.getCurrLoc());
        double angleChg = rotAngle - currAngle;
        doRotateY(angleChg);
        currAngle = rotAngle;

        if (moveForward())
            ;
        else if (moveLeft())
            ;
        else if (moveRight())
            ;
        else if (moveBackward())
            ;
        else
            System.out.println("Alien stuck!");
    }

    private double calcTurn(Point3d alienLoc, Point3d touristLoc) {
        double zDiff = touristLoc.z - alienLoc.z;
        double xDiff = touristLoc.x - alienLoc.x;

        double turnAngle = 0.0;
        if (zDiff != 0.0) {
            double angle = Math.atan(xDiff / zDiff);
            if ((xDiff > 0) && (zDiff > 0)) {
                turnAngle = angle;
            } else if ((xDiff > 0) && (zDiff < 0)) {
                turnAngle = Math.PI + angle;
            } else if ((xDiff < 0) && (zDiff < 0)) {
                turnAngle = Math.PI + angle;
            } else if ((xDiff < 0) && (zDiff > 0)) {
                turnAngle = angle;
            }
        } else {
            if (xDiff > 0) {
                turnAngle = Math.PI / 2;
            } else if (xDiff < 0) {
                turnAngle = -Math.PI / 2;
            } else {
                turnAngle = 0.0;
            }
        }
        return turnAngle;
    }

    private boolean closeTogether(Point3d alienLoc, Point3d touristLoc) {
        double distApart = alienLoc.distance(touristLoc);
        if (distApart < CLOSE_DIST) {
            return true;
        }
        return false;
    }
}
