package com.example.jumpingjack;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Ribbon {

    private BufferedImage image;
    private int width;
    private int pWidth, pHeight;

    private int moveSize;
    private boolean isMovingRight;
    private boolean isMovingLeft;

    private int xImHead;

    public Ribbon(int w, int h, BufferedImage im, int moveSz) {
        pWidth = w;
        pHeight = h;

        this.image = im;
        width = im.getWidth();
        if (width < pWidth) {
            System.out.println("Ribbon width < panel width");
        }

        moveSize = moveSz;
        isMovingRight = false;
        isMovingLeft = false;
        xImHead = 0;
    }

    public void moveRight() {
        isMovingRight = true;
        isMovingLeft = false;
    }

    public void moveLeft() {
        isMovingRight = false;
        isMovingLeft = true;
    }

    public void stayStill() {
        isMovingRight = false;
        isMovingLeft = false;
    }

    public void update() {
        if (isMovingRight) {
            xImHead = (xImHead + moveSize) % width;
        } else if (isMovingLeft) {
            xImHead = (xImHead - moveSize) % width;
        }
    }

    public void display(Graphics graphics) {
        if (xImHead == 0) {
            draw(graphics, image, 0, pWidth, 0, pWidth);
        } else if ((xImHead > 0) && (xImHead < pWidth)) {
            draw(graphics, image, 0, xImHead, width - xImHead, width);
            draw(graphics, image, xImHead, pWidth, 0, pWidth - xImHead);
        } else if (xImHead >= pWidth) {
            draw(graphics, image, 0, pWidth, width - xImHead, width - xImHead + pWidth);
        } else if ((xImHead < 0) && (xImHead >= pWidth - width)) {
            draw(graphics, image, 0, pWidth, -xImHead, pWidth - xImHead);
        } else if (xImHead < pWidth - width) {
            draw(graphics, image, 0, width + xImHead, -xImHead, width);
            draw(graphics, image, width + xImHead, pWidth, 0, pWidth - width - xImHead);
        }
    }

    private void draw(Graphics graphics, BufferedImage image, int scrX1, int scrX2, int imX1, int imX2) {
        graphics.drawImage(image, scrX1, 0, scrX2, pHeight, imX1, 0, imX2, pHeight, null);
    }
}
