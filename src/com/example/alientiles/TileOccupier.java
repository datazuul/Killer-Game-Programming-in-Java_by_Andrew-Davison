package com.example.alientiles;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileOccupier {

    private String name;
    private int type;
    private BufferedImage image;
    private int xTile, yTile;
    private int xDraw, yDraw;

    private TiledSprite sprite = null;

    public TileOccupier(String nm, int ty, int x, int y, BufferedImage im, int xRowStart, int yRowStart, int xTileWidth, int yTileHeight) {
        name = nm;
        type = ty;
        xTile = x;
        yTile = y;
        image = im;
        caclPosition(xRowStart, yRowStart, xTileWidth, yTileHeight);
    }

    private void caclPosition(int xRowStart, int yRowStart, int xTileWidth, int yTileHeight) {
        int xImOffset = xTileWidth / 2 - image.getWidth() / 2;
        int yImOffset = yTileHeight - image.getHeight() - yTileHeight / 5;

        xDraw = xRowStart + (xTile * xTileWidth) + xImOffset;
        if (yTile % 2 == 0) {
            yDraw = yRowStart + (yTile / 2 * yTileHeight) + yImOffset;
        } else {
            yDraw = yRowStart + ((yTile - 1) / 2 * yTileHeight) + yImOffset;
        }
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Point getTileLoc() {
        return new Point(xTile, yTile);
    }

    public void addSpriteRef(TiledSprite s) {
        if (type == WorldDisplay.SPRITE) {
            sprite = s;
        }
    }

    public void draw(Graphics graphics, int xOffset, int yOffset) {
        if (type == WorldDisplay.SPRITE) {
            sprite.setPosition(xDraw + xOffset, yDraw + yOffset);
            sprite.drawSprite(graphics);
        } else {
            graphics.drawImage(image, xDraw + xOffset, yDraw + yOffset, null);
        }
    }
}
