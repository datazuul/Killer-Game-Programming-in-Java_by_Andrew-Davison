package com.example.maze3D;

import javax.swing.*;
import java.awt.*;

public class BirdsEye extends JPanel {

    private static final int PANEL_WIDTH = 256;
    private static final int PANEL_HEIGHT = 256;

    private static final int NUM_DIRS = 4;
    private static final int FORWARD = 0;
    private static final int LEFT = 1;
    private static final int BACK = 2;
    private static final int RIGHT = 3;

    private static final String BANG_MSG = "BANG!";

    private MazeManager mazeManager;

    private Image mazeImage;
    private Image userImage;
    private Image[] arrowImages;

    private int arrowWidth, arrowHeight;
    private int step;
    private int compass;

    private Point[] moves;
    private Point currentPosition;

    private boolean showBang;
    private Font messageFont;

    public BirdsEye(MazeManager mazeManager) {
        this.mazeManager = mazeManager;
        setBackground(Color.white);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        messageFont = new Font("SansSerif", Font.BOLD, 24);

        mazeImage = mazeManager.getMazeBImage();
        initMoves();
        loadArrows();
        initPosition();
        repaint();
    }

    private void initMoves() {
        moves = new Point[NUM_DIRS];
        step = mazeManager.getImageStep();
        moves[FORWARD] = new Point(0, step);
        moves[LEFT] = new Point(step, 0);
        moves[BACK] = new Point(0, -step);
        moves[RIGHT] = new Point(-step, 0);
    }

    private void loadArrows() {
        arrowImages = new Image[NUM_DIRS];

        ImageIcon imageIcon = new ImageIcon("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/arrowFwd.gif");
        arrowImages[FORWARD] = imageIcon.getImage();
        arrowWidth = imageIcon.getIconWidth();
        arrowHeight = imageIcon.getIconHeight();

        arrowImages[LEFT] = new ImageIcon("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/arrowLeft.gif").getImage();
        arrowImages[BACK] = new ImageIcon("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/arrowBack.gif").getImage();
        arrowImages[RIGHT] = new ImageIcon("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/arrowRight.gif").getImage();
    }

    private void initPosition() {
        currentPosition = mazeManager.getImageStartPosition();
        compass = FORWARD;
        userImage = arrowImages[FORWARD];
        showBang = false;
    }

    public void setMove(int dir) {
        int actualHead = (compass + dir) % NUM_DIRS;
        Point move = moves[actualHead];
        currentPosition.x += move.x;
        currentPosition.y += move.y;
        repaint();
    }

    public void setRotation(int dir) {
        compass = (compass + dir) % NUM_DIRS;
        userImage = arrowImages[compass];
        repaint();
    }

    public void bangAlert() {
        showBang = true;
        repaint();
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        graphics.drawImage(mazeImage, 0, 0, null);

        int xPosition = currentPosition.x + step / 2 - arrowWidth / 2;
        int yPosition = currentPosition.y + step / 2 - arrowHeight / 2;
        graphics.drawImage(userImage, xPosition, yPosition, null);

        if (showBang) {
            graphics.setColor(Color.red);
            graphics.setFont(messageFont);
            graphics.drawString(BANG_MSG, PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
            showBang = false;
        }
    }
}
