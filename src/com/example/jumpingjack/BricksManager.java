package com.example.jumpingjack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BricksManager {

    private static final String IMAGE_DIR = "Images/";
    private static final int MAX_BRICKS_LINES = 15;

    private static final double MOVE_FACTOR = 0.25;

    private int pWidth, pHeight;
    private int width, height;

    private int imWidth, imHeight;
    private int numCols, numRows;

    private int moveSize;

    private boolean isMovingRight;
    private boolean isMovingLeft;

    private int xMapHead;

    private ArrayList bricksList;
    private ArrayList[] columnBricks;

    private ImagesLoader imagesLoader;
    private ArrayList brickImages = null;

    public BricksManager(int w, int h, String fnm, ImagesLoader il) {
        pWidth = w;
        pHeight = h;
        imagesLoader = il;

        bricksList = new ArrayList();
        loadBricksFile(fnm);
        initBricksInfo();
        createColumns();

        moveSize = (int) (imWidth * MOVE_FACTOR);
        if (moveSize == 0) {
            System.out.println("moveSize cannot be 0, setting it to 1");
            moveSize = 1;
        }

        isMovingRight = false;
        isMovingLeft = false;
        xMapHead = 0;
    }

    private void loadBricksFile(String fnm) {
        String imsFNm = IMAGE_DIR + fnm;
        System.out.println("Reading bricks file : " + imsFNm);

        int numStripImages = -1;
        int numBricksLines = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(imsFNm));
            String line;
            char ch;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                ch = Character.toLowerCase(line.charAt(0));
                if (ch == 's') {
                    numStripImages = getStripImages(line);
                } else {
                    if (numBricksLines > MAX_BRICKS_LINES) {
                        System.out.println("Max reached, skipping bricks line : " + line);
                    } else if (numStripImages == -1) {
                        System.out.println("No strip image, skipping bricks line : " + line);
                    } else {
                        storeBricks(line, numBricksLines, numStripImages);
                        numBricksLines++;
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error loadBricksFile file : " + imsFNm);
            //System.exit(1);
            e.printStackTrace();
        }
    }

    private int getStripImages(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() != 3) {
            System.out.println("Wrong no. of arguments for " + line);
            return -1;
        } else {
            tokenizer.nextToken();
            System.out.print("Bricks strip : ");

            String fnm = tokenizer.nextToken();
            int number = -1;
            try {
                number = Integer.parseInt(tokenizer.nextToken());
                imagesLoader.loadStripImages(fnm, number);
                brickImages = imagesLoader.getImages(getPrefix(fnm));
            } catch (Exception e) {
                System.out.println("Number is incorrect for " + line);
            }
            return number;
        }
    }

    private String getPrefix(String fnm) {
        int posn;
        if ((posn = fnm.lastIndexOf(".")) == -1) {
            System.out.println("No prefix found for filename : " + fnm);
            return fnm;
        } else {
            return fnm.substring(0, posn);
        }
    }

    private void storeBricks(String line, int lineNo, int numImages) {
        int imageID;
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            if (ch == ' ') {
                continue;
            }
            if (Character.isDigit(ch)) {
                imageID = ch - '0';
                if (imageID >= numImages) {
                    System.out.println("Image ID " + imageID + " out of range");
                } else {
                    bricksList.add(new Brick(imageID, x, lineNo));
                }
            } else {
                System.out.println("Brick char " + ch + " is not a digit");
            }
        }
    }

    private void initBricksInfo() {
        if (brickImages == null) {
            System.out.println("No bricks images were loaded");
            System.exit(1);
        }
        if (bricksList.size() == 0) {
            System.out.println("No bricks map were loaded");
            System.exit(1);
        }

        BufferedImage image = (BufferedImage) brickImages.get(0);
        imWidth = image.getWidth();
        imHeight = image.getHeight();

        findNumBricks();
        calcMapDimensions();
        checkForGaps();

        addBrickDetails();
    }

    private void findNumBricks() {
        Brick brick;
        numCols = 0;
        numRows = 0;
        for (int i = 0; i < bricksList.size(); i++) {
            brick = (Brick) bricksList.get(i);
            if (numCols < brick.getMapX()) {
                numCols = brick.getMapX();
            }
            if (numRows < brick.getMapY()) {
                numRows = brick.getMapY();
            }
        }
        numCols++;
        numRows++;
    }

    private void calcMapDimensions() {
        width = imWidth * numCols;
        height = imHeight * numRows;

        if (width < pWidth) {
            System.out.println("Bricks map is less wide than the panel");
            System.exit(0);
        }
    }

    private void checkForGaps() {
        boolean[] hasBrick = new boolean[numCols];
        for (int j = 0; j < numCols; j++) {
            hasBrick[j] = false;
        }

        Brick brick;
        for (int i = 0; i < bricksList.size(); i++) {
            brick = (Brick) bricksList.get(i);
            if (brick.getMapY() == numRows - 1) {
                hasBrick[brick.getMapX()] = true;
            }
        }

        for (int j = 0; j < numCols; j++) {
            if (!hasBrick[j]) {
                System.out.println("Gap found in bricks map bottom line at position " + j);
                System.exit(0);
            }
        }
    }

    private void addBrickDetails() {
        Brick brick;
        BufferedImage image;
        for (int i = 0; i < bricksList.size(); i++) {
            brick = (Brick) bricksList.get(i);
            image = (BufferedImage) brickImages.get(brick.getImageID());
            brick.setImage(image);
            brick.setLocY(pHeight, numRows);
        }
    }

    private void createColumns() {
        columnBricks = new ArrayList[numCols];
        for (int i = 0; i < numCols; i++) {
            columnBricks[i] = new ArrayList();
        }

        Brick brick;
        for (int j = 0; j < bricksList.size(); j++) {
            brick = (Brick) bricksList.get(j);
            columnBricks[brick.getMapX()].add(brick);
        }
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
            xMapHead = (xMapHead + moveSize) % width;
        } else if (isMovingLeft) {
            xMapHead = (xMapHead - moveSize) % width;
        }
    }

    public void display(Graphics graphics) {
        int bCoord = (int) (xMapHead / imWidth) * imWidth;
        int offset;
        if (bCoord >= 0) {
            offset = xMapHead - bCoord;
        } else {
            offset = bCoord - xMapHead;
        }

        if ((bCoord >= 0) && (bCoord < pWidth)) {
            drawBricks(graphics, 0 - (imWidth - offset), xMapHead, width - bCoord - imWidth);
            drawBricks(graphics, xMapHead, pWidth, 0);
        } else if (bCoord >= pWidth) {
            drawBricks(graphics, 0 - (imWidth - offset), pWidth, width - bCoord - imWidth);
        } else if ((bCoord < 0) && (bCoord >= pWidth - width + imWidth)) {
            drawBricks(graphics, 0 - offset, pWidth, -bCoord);
        } else if (bCoord < pWidth - width + imWidth) {
            drawBricks(graphics, 0 - offset, width + xMapHead, -bCoord);
            drawBricks(graphics, width + xMapHead, pWidth, 0);
        }

    }

    private void drawBricks(Graphics graphics, int xStart, int xEnd, int xBrick) {
        int xMap = xBrick / imWidth;
        ArrayList column;
        Brick brick;
        for (int x = xStart; x < xEnd; x += imWidth) {
            column = columnBricks[xMap];
            for (int i = 0; i < column.size(); i++) {
                brick = (Brick) column.get(i);
                brick.display(graphics, x);
            }
            xMap++;
        }
    }

    public int getBrickHeight() {
        return imHeight;
    }

    public int findFloor(int xSprite) {
        int xMap = (int) (xSprite / imWidth);
        int locY = pHeight;
        ArrayList column = columnBricks[xMap];

        Brick brick;
        for (int i = 0; i < column.size(); i++) {
            brick = (Brick) column.get(i);
            if (brick.getLocY() < locY) {
                locY = brick.getLocY();
            }
        }
        return locY;
    }

    public int getMoveSize() {
        return moveSize;
    }

    public boolean insideBrick(int xWorld, int yWorld) {
        Point mapCoord = worldToMap(xWorld, yWorld);
        ArrayList column = columnBricks[mapCoord.x];

        Brick brick;
        for (int i = 0; i < column.size(); i++) {
            brick = (Brick) column.get(i);
            if (mapCoord.y == brick.getMapY()) {
                return true;
            }
        }
        return false;
    }

    private Point worldToMap(int xWorld, int yWorld) {
        xWorld = xWorld % width;
        if (xWorld < 0) {
            xWorld += width;
        }
        int mapX = (int) (xWorld / imWidth);

        yWorld = yWorld - (pHeight - height);
        int mapY = (int) (yWorld / imHeight);

        if (yWorld < 0) {
            mapY = mapY - 1;
        }

        return new Point(mapX, mapY);
    }

    public int checkBrickBase(int xWorld, int yWorld, int step) {
        if (insideBrick(xWorld, yWorld)) {
            int yMapWorld = yWorld - (pHeight - height);
            int mapY = (int) (yMapWorld / imHeight);
            int topOffse = yMapWorld - (mapY * imHeight);
            int smallStep = step - (imHeight - topOffse);
            return smallStep;
        }
        return step;
    }

    public int checkBrickTop(int xWorld, int yWorld, int step) {
        if (insideBrick(xWorld, yWorld)) {
            int yMapWorld = yWorld - (pHeight - height);
            int mapY = (int) (yMapWorld / imHeight);
            int topOffset = yMapWorld - (mapY * imHeight);
            int smallStep = step - topOffset;
            return smallStep;
        }
        return step;
    }
}
