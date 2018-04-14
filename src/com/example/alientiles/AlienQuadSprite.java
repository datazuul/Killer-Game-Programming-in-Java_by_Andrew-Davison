package com.example.alientiles;

import java.awt.*;

public class AlienQuadSprite extends AlienSprite {

    private int currentQuad;

    public AlienQuadSprite(int x, int y, int w, int h, ImagesLoader imsLd, WorldDisplay wd) {
        super(x, y, w, h, imsLd, wd);
        currentQuad = getRandDirection();
    }

    public void playerHasMoved(Point playerLoc) {
        if (world.hasPickupsLeft()) {
            Point nearPickup = world.nearestPickup(playerLoc);
            currentQuad = calcQuadrant(nearPickup);
        }
    }

    private int calcQuadrant(Point pickupPt) {
        if ((pickupPt.x > xTile) && (pickupPt.y > yTile)) {
            return SE;
        } else if ((pickupPt.x > xTile) && (pickupPt.y < yTile)) {
            return NE;
        } else if ((pickupPt.x < xTile) && (pickupPt.y > yTile)) {
            return SW;
        } else {
            return NW;
        }
    }

    protected void move() {
        int quad = currentQuad;
        Point newPt;
        while ((newPt = tryMove(quad)) == null) {
            quad = getRandDirection();
        }
        setMove(newPt, quad);
    }
}
