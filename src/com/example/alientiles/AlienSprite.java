package com.example.alientiles;

import java.awt.*;

public class AlienSprite extends TiledSprite {

    private static final int UPDATE_FREQ = 30;

    private int updateCounter = 0;

    public AlienSprite(int x, int y, int w, int h, ImagesLoader imsLd, WorldDisplay wd) {
        super(x, y, w, h, imsLd, "baddieStill", wd);
    }

    public void playerHasMoved(Point playerLoc) {

    }

    public void update() {
        updateCounter = (updateCounter + 1) % UPDATE_FREQ;
        if (updateCounter == 0) {
            if (!hitPlayer()) {
                move();
            }
        }
    }

    private boolean hitPlayer() {
        Point playerLoc = world.getPlayerLoc();
        if (playerLoc.equals(getTileLoc())) {
            world.hitByAlien();
            return true;
        }
        return false;
    }

    protected void move() {
        int quad = getRandDirection();
        Point newPt;
        while ((newPt = tryMove(quad)) == null) {
            quad = getRandDirection();
        }
        setMove(newPt, quad);
    }

    protected void setMove(Point newPt, int quad) {
        if (world.validTileLoc(newPt.x, newPt.y)) {
            setTileLoc(newPt);
            if ((quad == NE) || (quad == SE)) {
                setImage("baddieRight");
            } else if ((quad == SW) || (quad == NW)) {
                setImage("baddieLeft");
            } else {
                System.out.println("Unknown alien quadrant : " + quad);
            }
        } else {
            System.out.println("Can not move alien to ( " + newPt.x + ", " + newPt.y + ")");
        }
    }
}
