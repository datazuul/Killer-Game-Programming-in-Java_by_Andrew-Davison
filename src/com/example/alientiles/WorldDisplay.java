package com.example.alientiles;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class WorldDisplay {

    private static final String WORLD_DIR = "World/";

    public static final int BLOCK = 0;
    public static final int PICKUP = 1;
    public static final int SPRITE = 2;

    private int numXTiles, numYTiles;
    private int tileWidth, tileHeight;
    private int evenRowX, evenRowY;
    private int oddRowX, oddRowY;
    private int xOffset, yOffset;

    private ImagesLoader imsLoader;
    private AlienTilesPanel atPanel;

    private BufferedImage flooeIm;
    private boolean obstacles[][];

    private WorldItems wItems;

    private int numPickups = 0;
    private int blocksCounter = 0;

    private PlayerSprite player;
    private AlienSprite aliens[];

    public WorldDisplay(ImagesLoader imsLd, AlienTilesPanel atp) {
        imsLoader = imsLd;
        atPanel = atp;

        xOffset = 0;
        yOffset = 0;
        loadFloorInfo("worldInfo.txt");

        wItems = new WorldItems(tileWidth, tileHeight, evenRowX, evenRowY, oddRowX, oddRowY);

        initObstacles();
        loadWorldObjects("worldObjs.txt");
    }

    private void loadFloorInfo(String wFNm) {
        String worldFNm = WORLD_DIR + wFNm;
        System.out.println("Reading file : " + worldFNm);
        try {
            InputStream in = this.getClass().getResourceAsStream(worldFNm);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String[] tokens;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                tokens = line.split("\\s+");
                if (tokens[0].equals("image")) {
                    flooeIm = imsLoader.getImage(tokens[1]);
                } else if (tokens[0].equals("numTiles")) {
                    numXTiles = getNumber(tokens[1]);
                    numYTiles = getNumber(tokens[2]);
                } else if (tokens[0].equals("dimTile")) {
                    tileWidth = getNumber(tokens[1]);
                    tileHeight = getNumber(tokens[2]);
                } else if (tokens[0].equals("evenRow")) {
                    evenRowX = getNumber(tokens[1]);
                    evenRowY = getNumber(tokens[2]);
                } else if (tokens[0].equals("oddRow")) {
                    oddRowX = getNumber(tokens[1]);
                    oddRowY = getNumber(tokens[2]);
                } else {
                    System.out.println("Do not recognize line : " + line);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading file : " + worldFNm);
            System.exit(1);
        }
    }

    private int getNumber(String token) {
        int num = 0;
        try {
            num = Integer.parseInt(token);
        } catch (NumberFormatException ex) {
            System.out.println("Incorrect format for " + token);
        }
        return num;
    }

    private void loadWorldObjects(String woFNm) {
        String objsFNm = WORLD_DIR + woFNm;
        System.out.println("Reading file : " + objsFNm);
        try {
            InputStream in = this.getClass().getResourceAsStream(objsFNm);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            char ch;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                ch = Character.toLowerCase(line.charAt(0));
                if (ch == 'n') {
                    getObstacles(line.substring(1), br);
                } else if (ch == 'b') {
                    getBlocks(line, br);
                } else if (ch == 'p') {
                    getPickup(line);
                } else {
                    System.out.println("Do not recognize line : " + line);
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error reading file : " + objsFNm);
            System.exit(1);
        }
    }

    private void getObstacles(String line, BufferedReader br) {
        boolean reachedEnd = getObstaclesLine(line);
        try {
            while (!reachedEnd) {
                line = br.readLine();
                if (line == null) {
                    System.out.println("Unexpected end of obstacles info");
                    System.exit(1);
                }
                reachedEnd = getObstaclesLine(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading obstacles info");
            System.exit(1);
        }
    }

    private boolean getObstaclesLine(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        String token;
        Point coord;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.equals("#")) {
                return true;
            }
            coord = getCoord(token);
            obstacles[coord.x][coord.y] = true;
        }
        return false;
    }

    private Point getCoord(String token) {
        int x = 0;
        int y = 0;
        String[] results = token.split("-");
        if (results.length != 2) {
            System.out.println("Incorrect coordinates in " + token);
        } else {
            try {
                x = Integer.parseInt(results[0]);
                y = Integer.parseInt(results[1]);
            } catch (NumberFormatException ex) {
                System.out.println("Incorrect format for coordinates in " + token);
            }
        }

        if (x >= numXTiles) {
            System.out.println("x coordinate too large in " + token);
            x = numXTiles - 1;
        }
        if (y >= numYTiles) {
            System.out.println("x coordinate too large in " + token);
            x = numYTiles - 1;
        }

        return new Point(x, y);
    }

    private void getBlocks(String line, BufferedReader br) {
        boolean reachedEnd = false;
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();
        String blockName = tokenizer.nextToken();
        BufferedImage blockIm = imsLoader.getImage(blockName);

        try {
            while (!reachedEnd) {
                line = br.readLine();
                if (line == null) {
                    System.out.println("Unexpected end of blocks info");
                    System.exit(1);
                }
                reachedEnd = getBlocksLine(line, blockName, blockIm);
            }
        } catch (IOException e) {
            System.out.println("Error reading blocks info");
            System.exit(1);
        }
    }

    private boolean getBlocksLine(String line, String blockName, BufferedImage im) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        String token;
        Point coord;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.equals("#")) {
                return true;
            }
            coord = getCoord(token);

            wItems.addItem(blockName + blocksCounter, BLOCK, coord.x, coord.y, im);
            obstacles[coord.x][coord.y] = true;
            blocksCounter++;
        }
        return false;
    }

    private void getPickup(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();

        String pickupName = tokenizer.nextToken();
        BufferedImage pickupIm = imsLoader.getImage(pickupName);

        Point coord = getCoord(tokenizer.nextToken());
        wItems.addItem(pickupName, PICKUP, coord.x, coord.y, pickupIm);
        numPickups++;
    }

    private void initObstacles() {
        obstacles = new boolean[numXTiles][numYTiles];
        for (int i = 0; i < numXTiles; i++) {
            for (int j = 0; j < numYTiles; j++) {
                obstacles[i][j] = false;
            }
        }
    }

    public boolean validTileLoc(int x, int y) {
        if ((x < 0) || (x >= numXTiles) || (y < 0) || (y >= numYTiles)) {
            return false;
        }
        if (obstacles[x][y]) {
            return false;
        }
        return true;
    }

    public void draw(Graphics graphics) {
        graphics.drawImage(flooeIm, xOffset, yOffset, null);
        wItems.positionSprites(player, aliens);
        wItems.draw(graphics, xOffset, yOffset);
        wItems.removeSprites();
    }

    public String overPickup(Point pt) {
        return wItems.findPickupName(pt);
    }

    public void removePickup(String name) {
        if (wItems.removePickup(name)) {
            numPickups--;
            if (numPickups == 0) {
                atPanel.gameOver();
            }
        } else {
            System.out.println("Can not delete unknown pickup : " + name);
        }
    }

    public String getPickupsStatus() {
        return "" + numPickups + " pickups left";
    }

    public boolean hasPickupsLeft() {
        return numPickups != 0;
    }

    public Point nearestPickup(Point pt) {
        return wItems.nearestPickup(pt);
    }

    public void addSprites(PlayerSprite ps, AlienSprite as[]) {
        player = ps;
        aliens = as;
    }

    public Point getPlayerLoc() {
        return player.getTileLoc();
    }

    public void hitByAlien() {
        player.hitByAlien();
    }

    public void playerHasMoved(Point newPt, int moveQuad) {
        for (int i = 0; i < aliens.length; i++) {
            aliens[i].playerHasMoved(newPt);
        }

        updateOffsets(moveQuad);
    }

    private void updateOffsets(int moveQuad) {
        if (moveQuad == TiledSprite.SW) {
            xOffset += tileWidth / 2;
            yOffset -= tileHeight / 2;
        } else if (moveQuad == TiledSprite.NW) {
            xOffset += tileWidth / 2;
            yOffset += tileHeight / 2;
        } else if (moveQuad == TiledSprite.NE) {
            xOffset -= tileWidth / 2;
            yOffset += tileHeight / 2;
        } else if (moveQuad == TiledSprite.SE) {
            xOffset -= tileWidth / 2;
            yOffset -= tileHeight / 2;
        } else if (moveQuad == TiledSprite.STILL) {

        } else {
            System.out.println("moveQuad error detected");
        }
    }
}
