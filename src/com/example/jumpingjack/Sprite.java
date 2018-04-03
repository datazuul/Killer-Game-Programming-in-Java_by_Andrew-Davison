package com.example.jumpingjack;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {

    private static final int XSTEP = 5;
    private static final int YSTEP = 5;

    private static final int SIZE = 12;

    private ImagesLoader imagesLoader;
    private String imageName;
    private BufferedImage image;
    private int width, height;

    private ImagesPlayer player;
    private boolean isLooping;

    private int pWidth, pHeight;
    private boolean isActive = true;

    protected int locx, locy;
    protected int dx, dy;

    public Sprite(int x, int y, int w, int h, ImagesLoader imsLd, String name) {
        locx = x;
        locy = y;
        pWidth = w;
        pHeight = h;
        dx = XSTEP;
        dy = YSTEP;

        imagesLoader = imsLd;
        setImage(name);
    }

    public void setImage(String name) {
        imageName = name;
        image = imagesLoader.getImage(imageName);
        if (image == null) {
            System.out.println("No sprite image for " + imageName);
            width = SIZE;
            height = SIZE;
        } else {
            width = image.getWidth();
            height = image.getHeight();
        }

        player = null;
        isLooping = false;
    }

    public void loopImage(int animPeriod, double seqDuration) {
        if (imagesLoader.numImages(imageName) > 1) {
            player = null;
            player = new ImagesPlayer(imageName, animPeriod, seqDuration, true, imagesLoader);
            isLooping = true;
        } else {
            System.out.println(imageName + " is not a sequence of images");
        }
    }

    public void stopLooping() {
        if (isLooping) {
            player.stop();
            isLooping = false;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPWidth() {
        return pWidth;
    }

    public int getPHeight() {
        return pHeight;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setPosition(int x, int y) {
        locx = x;
        locy = y;
    }

    public void translate(int xDist, int yDist) {
        locx += xDist;
        locy += yDist;
    }

    public int getXPosn() {
        return locx;
    }

    public int getYPosn() {
        return locy;
    }

    public void setStep(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getXStep() {
        return dx;
    }

    public int getYStep() {
        return dy;
    }

    public Rectangle getMyRectangle() {
        return new Rectangle(locx, locy, width, height);
    }

    public void updateSprite() {
        if (isActive()) {
            locx += dx;
            locy += dy;
            if (isLooping) {
                player.updateTick();
            }
        }
    }

    public void drawSprite(Graphics graphics) {
        if (isActive()) {
            if (image == null) {
                graphics.setColor(Color.yellow);
                graphics.fillOval(locx, locy, SIZE, SIZE);
                graphics.setColor(Color.black);
            } else {
                if (isLooping) {
                    image = player.getCurrentImage();
                }
                graphics.drawImage(image, locx, locy, null);
            }
        }
    }
}
