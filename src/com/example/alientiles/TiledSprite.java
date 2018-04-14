package com.example.alientiles;

import java.awt.*;

public class TiledSprite extends Sprite {

    public static final int NE = 0;
    public static final int SE = 1;
    public static final int SW = 2;
    public static final int NW = 3;
    public static final int STILL = 4;
    public static final int NUM_DIRS = 4;

    protected int xTile, yTile;
    protected WorldDisplay world;

    public TiledSprite(int x, int y, int w, int h, ImagesLoader imsLd, String name, WorldDisplay wd) {
        super(0, 0, w, h, imsLd, name);
        setStep(0, 0);
        world = wd;

        if (!world.validTileLoc(x, y)) {
            System.out.println("Alien tile location ( " + x + " , " + y + " ) not valid; using (0, 0)");
            x = 0;
            y = 0;
        }

        xTile = x;
        yTile = y;
    }

    public void setTileLoc(Point pt) {
        xTile = pt.x;
        yTile = pt.y;
    }

    public Point getTileLoc() {
        return new Point(xTile, yTile);
    }

    public Point tryMove(int quad) {
        Point nextPt;
        if (quad == NE) {
            nextPt = (yTile % 2 == 0) ? new Point(xTile, yTile - 1) : new Point(xTile + 1, yTile - 1);
        } else if (quad == SE) {
            nextPt = (yTile % 2 == 0) ? new Point(xTile, yTile + 1) : new Point(xTile + 1, yTile + 1);
        } else if (quad == SW) {
            nextPt = (yTile % 2 == 0) ? new Point(xTile - 1, yTile + 1) : new Point(xTile, yTile + 1);
        } else if (quad == NW) {
            nextPt = (yTile % 2 == 0) ? new Point(xTile - 1, yTile - 1) : new Point(xTile, yTile - 1);
        } else {
            return null;
        }

        if (world.validTileLoc(nextPt.y, nextPt.y)) {
            return nextPt;
        } else {
            return null;
        }
    }

    public int getRandDirection() {
        return (int) (NUM_DIRS * Math.random());
    }

    public int whichQuadrant(Point p) {
        if ((xTile == p.x) && (yTile == p.y)) {
            return STILL;
        }

        if (yTile % 2 == 0) {
            if ((xTile == p.x) && (yTile - 1 == p.y)) {
                return NE;
            }
            if ((xTile == p.x) && (yTile + 1 == p.y)) {
                return SE;
            }
            if ((xTile - 1 == p.x) && (yTile + 1 == p.y)) {
                return SW;
            }
            if ((xTile - 1 == p.x) && (yTile - 1 == p.y)) {
                return NW;
            }
        } else {
            if ((xTile + 1 == p.x) && (yTile - 1 == p.y)) {
                return NE;
            }
            if ((xTile + 1 == p.x) && (yTile + 1 == p.y)) {
                return SE;
            }
            if ((xTile == p.x) && (yTile + 1 == p.y)) {
                return SW;
            }
            if ((xTile == p.x) && (yTile - 1 == p.y)) {
                return NW;
            }
        }
        return -1;
    }
}
