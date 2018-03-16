package com.example.loaderstests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SoundsPanel extends JPanel {

    private static final String[] names = {"dog", "cat", "sheep", "chicken"};

    private static final int[] xCoords = {20, 210, 20, 210};
    private static final int[] yCoords = {25, 25, 170, 170};

    private static final String IMS_FILE = "imagesInfo.txt";

    private static final int PANEL_WIDTH = 350;
    private static final int PANEL_HEIGHT = 350;

    private int numImages;
    private BufferedImage[] images;
    private Rectangle[] hotSpots;

    private LoadersTests topLevel;

    public SoundsPanel(LoadersTests sts) {
        topLevel = sts;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        initImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //super.mousePressed(e);
                selectImage(e.getX(), e.getY());
            }
        });
    }

    private void initImages() {
        numImages = names.length;

        hotSpots = new Rectangle[numImages];
        images = new BufferedImage[numImages];

        ImagesLoader imagesLoader = new ImagesLoader(IMS_FILE);

        for (int i = 0; i < numImages; i++) {
            images[i] = imagesLoader.getImage(names[i]);
            hotSpots[i] = new Rectangle(xCoords[i], yCoords[i], images[i].getWidth(), images[i].getHeight());
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        for (int i = 0; i < numImages; i++) {
            graphics.drawImage(images[i], xCoords[i], yCoords[i], this);
        }
    }

    private void selectImage(int x, int y) {
        for (int i = 0; i < numImages; i++) {
            if (hotSpots[i].contains(x, y)) {
                topLevel.playClip(names[i], i);
                break;
            }
        }
    }
}
