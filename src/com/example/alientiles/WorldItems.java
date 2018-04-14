package com.example.alientiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class WorldItems {

    private int tileWidth, tileHeight;
    private int evenRowX, evenRowY;
    private int oddRowX, oddRowY;
    private ArrayList items;

    public WorldItems(int w, int h, int erX, int erY, int orX, int orY) {
        tileWidth = w;
        tileHeight = h;
        evenRowX = erX;
        evenRowY = erY;
        items = new ArrayList();
    }

    public void addItem(String name, int type, int x, int y, BufferedImage im) {
        TileOccupier toc;
        if (y % 2 == 0) {
            toc = new TileOccupier(name, type, x, y, im, evenRowX, evenRowY, tileWidth, tileHeight);
        } else {
            toc = new TileOccupier(name, type, x, y, im, oddRowX, oddRowY, tileWidth, tileHeight);
        }

        rowInsert(toc, x, y);
    }

    private void rowInsert(TileOccupier toc, int x, int y) {
        TileOccupier item;
        Point itemPt;
        int i = 0;
        while (i < items.size()) {
            item = (TileOccupier) items.get(i);
            itemPt = item.getTileLoc();
            if (y < itemPt.y) {
                break;
            } else if ((y == itemPt.y) && (x < itemPt.x)) {
                break;
            }
            i++;
        }
        items.add(i, toc);
    }

    public void draw(Graphics graphics, int xOffset, int yOffset) {
        TileOccupier item;
        for (int i = 0; i < items.size(); i++) {
            item = (TileOccupier) items.get(i);
            item.draw(graphics, xOffset, yOffset);
        }
    }

    public void positionSprites(PlayerSprite ps, AlienSprite[] aliens) {
        posnSprite("bob", ps);
        for (int i = 0; i < aliens.length; i++) {
            posnSprite("alien " + i, aliens[i]);
        }
    }

    private void posnSprite(String name, TiledSprite tSprite) {
        Point sPt = tSprite.getTileLoc();

        TileOccupier toc;
        if (sPt.y % 2 == 0) {
            toc = new TileOccupier(name, WorldDisplay.SPRITE, sPt.x, sPt.y, tSprite.getImage(), evenRowX, evenRowY,
                    tileWidth, tileHeight);
        } else {
            toc = new TileOccupier(name, WorldDisplay.SPRITE, sPt.x, sPt.y, tSprite.getImage(), oddRowX, oddRowY,
                    tileWidth, tileHeight);
        }

        toc.addSpriteRef(tSprite);
        rowInsert(toc, sPt.x, sPt.y);
    }

    public void removeSprites() {
        TileOccupier item;
        int i = 0;
        while (i < items.size()) {
            item = (TileOccupier) items.get(i);
            if (item.getType() == WorldDisplay.SPRITE) {
                items.remove(i);
            } else {
                i++;
            }
        }
    }

    public String findPickupName(Point pt) {
        TileOccupier item;
        for (int i = 0; i < items.size(); i++) {
            item = (TileOccupier) items.get(i);
            if ((item.getType() == WorldDisplay.PICKUP) && (pt.equals(item.getTileLoc()))) {
                return item.getName();
            }
        }
        return null;
    }

    public Point nearestPickup(Point pt) {
        double minDist = 1000000;
        Point minPoint = null;
        double dist;
        TileOccupier item;
        for (int i = 0; i < items.size(); i++) {
            item = (TileOccupier) items.get(i);
            if (item.getType() == WorldDisplay.PICKUP) {
                dist = pt.distanceSq(item.getTileLoc());
                if (dist < minDist) {
                    minDist = dist;
                    minPoint = item.getTileLoc();
                }
            }
        }
        return minPoint;
    }

    public boolean removePickup(String name) {
        TileOccupier item;
        for (int i = 0; i < items.size(); i++) {
            item = (TileOccupier) items.get(i);
            if ((item.getType() == WorldDisplay.PICKUP) && (name.equals(item.getName()))) {
                items.remove(i);
                return true;
            }
        }
        return false;
    }
}
