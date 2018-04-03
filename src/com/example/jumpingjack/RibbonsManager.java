package com.example.jumpingjack;

import java.awt.*;

public class RibbonsManager {

    private String ribImages[] = {"mountains", "houses", "trees"};
    private double moveFactors[] = {0.1, 0.5, 1.0};

    private Ribbon[] ribbons;
    private int numRibbons;
    private int moveSize;

    public RibbonsManager(int w, int h, int brickMvSz, ImagesLoader imsLd) {
        moveSize = brickMvSz;
        numRibbons = ribImages.length;
        ribbons = new Ribbon[numRibbons];

        for (int i = 0; i < numRibbons; i++) {
            ribbons[i] = new Ribbon(w, h, imsLd.getImage(ribImages[i]), (int) (moveFactors[i] * moveSize));
        }
    }

    public void moveRight() {
        for (int i = 0; i < numRibbons; i++) {
            ribbons[i].moveRight();
        }
    }

    public void moveLeft() {
        for (int i = 0; i < numRibbons; i++) {
            ribbons[i].moveLeft();
        }
    }

    public void stayStill() {
        for (int i = 0; i < numRibbons; i++) {
            ribbons[i].stayStill();
        }
    }

    public void update() {
        for (int i = 0; i < numRibbons; i++) {
            ribbons[i].update();
        }
    }

    public void display(Graphics graphics) {
        for (int i = 0; i < numRibbons; i++) {
            ribbons[i].display(graphics);
        }
    }
}
