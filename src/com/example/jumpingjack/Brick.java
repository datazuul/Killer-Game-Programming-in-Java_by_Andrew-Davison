package com.example.jumpingjack;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Brick {

    private int mapX, mapY;
    private int imageID;

    private BufferedImage image;
    private int height;
    private int locY;

    public Brick(int id, int x, int y) {
        mapX = x;
        mapY = y;
        imageID = id;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImage(BufferedImage image) {
        image = image;
        height = image.getHeight();
    }

    public void setLocY(int pHeight, int maxYBricks) {
        locY = pHeight - ((maxYBricks - mapY) * height);
    }

    public int getLocY() {
        return locY;
    }

    public void display(Graphics graphics, int xScr) {
        graphics.drawImage(image, xScr, locY, null);
    }
}
