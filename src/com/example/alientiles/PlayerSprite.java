package com.example.alientiles;

import java.awt.*;

public class PlayerSprite extends TiledSprite {

    private static final int MAX_HITS = 3;

    private ClipsLoader clipsLoader;
    private AlienTilesPanel atPanel;

    private int hitCount = 0;

    public PlayerSprite(int x, int y, int w, int h, ClipsLoader clipsLd, ImagesLoader imsLd, WorldDisplay wd, AlienTilesPanel atp) {
        super(x, y, w, h, imsLd, "still", wd);
        clipsLoader = clipsLd;
        atPanel = atp;
    }

    public boolean tryPickup() {
        String pickUpName;
        if ((pickUpName = world.overPickup(getTileLoc())) == null) {
            clipsLoader.play("noPickup", false);
            return false;
        } else {
            clipsLoader.play("gotPickup", false);
            world.removePickup(pickUpName);
            return true;
        }
    }

    public void hitByAlien() {
        clipsLoader.play("hit", false);
        hitCount++;
        if (hitCount == MAX_HITS) {
            atPanel.gameOver();
        }
    }

    public String getHitStatus() {
        int livesLeft = MAX_HITS - hitCount;
        if (livesLeft <= 0) {
            return "You're DEAD";
        } else if (livesLeft == 1) {
            return "1 life left";
        } else {
            return "" + livesLeft + " lives left";
        }
    }

    public void move(int quad) {
        Point newPt = tryMove(quad);
        if (newPt == null) {
            clipsLoader.play("slap", false);
            standStill();
        } else {
            setTileLoc(newPt);
            if (quad == NE) {
                setImage("ne");
            } else if (quad == SE) {
                setImage("se");
            } else if (quad == SW) {
                setImage("sw");
            } else {
                setImage("nw");
            }
            world.playerHasMoved(newPt, quad);
        }
    }

    public void standStill() {
        setImage("still");
    }
}
