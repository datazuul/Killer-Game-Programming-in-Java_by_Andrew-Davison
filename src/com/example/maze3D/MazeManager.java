package com.example.maze3D;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MazeManager {

    private static final int LENGTH = 40;
    private static final double USER_HEIGHT = 1.0;
    private static final int IMAGE_LENGTH = 240;
    private static final int IMAGE_STEP = IMAGE_LENGTH / LENGTH;
    private static final float RADIUS = 0.5f;
    private static final float HEIGHT = 3.0f;

    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f blue = new Color3f(0.0f, 0.0f, 1.0f);
    private static final Color3f medgreen = new Color3f(0.0f, 0.5f, 0.1f);

    private static final String BLOCK_TEXTURE = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/plate.jpg";
    private static final String CYLINDER_TEXTURE = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/cobbles.jpg";

    private Appearance blockAppearance, cylinderAppearance;

    private char[][] maze;
    private int xStartPosition, zStartPosition;

    private BranchGroup mazeBG;
    private BufferedImage mazeBImage;

    public MazeManager(String fn) {
        initialiseVars();
        readFile(fn);
        buildMazeReps();
    }

    private void initialiseVars() {
        maze = new char[LENGTH][LENGTH];
        for (int z = 0; z < LENGTH; z++) {
            for (int x = 0; x < LENGTH; x++) {
                maze[z][x] = ' ';
            }
        }
        xStartPosition = LENGTH / 2;
        zStartPosition = 0;

        mazeBG = new BranchGroup();
        mazeBImage = new BufferedImage(IMAGE_LENGTH, IMAGE_LENGTH, BufferedImage.TYPE_INT_ARGB);

        blockAppearance = makeAppearance(blue, BLOCK_TEXTURE);
        cylinderAppearance = makeAppearance(medgreen, CYLINDER_TEXTURE);
    }

    private Appearance makeAppearance(Color3f color3f, String textureFnm) {
        Appearance appearance = new Appearance();

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        System.out.println("Loading obstacle texture from " + textureFnm);
        TextureLoader textureLoader = new TextureLoader(textureFnm, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();
        appearance.setTexture(texture2D);

        Material material = new Material(color3f, black, color3f, specular, 20.0f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);
        return appearance;
    }

    private void readFile(String fn) {
        System.out.println("Reading maze plan from " + fn);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fn));
            String line;
            char charLine[];
            int numberOfRows = 0;
            while ((numberOfRows < LENGTH) && ((line = bufferedReader.readLine()) != null)) {
                charLine = line.toCharArray();
                int x = 0;
                while ((x < LENGTH) && (x < charLine.length)) {
                    maze[numberOfRows][x] = charLine[x];
                    x++;
                }
                numberOfRows++;
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading maze plan from " + fn);
            System.exit(0);
        }
    }

    private void buildMazeReps() {
        System.out.println("Building maze representations...please wait");

        char ch;
        Graphics graphics = (Graphics) mazeBImage.createGraphics();
        graphics.setColor(Color.white);

        for (int z = 0; z < LENGTH; z++) {
            for (int x = 0; x < LENGTH; x++) {
                ch = maze[z][x];
                if (ch == 's') {
                    xStartPosition = x;
                    zStartPosition = z;
                    maze[z][x] = ' ';
                } else if (ch == 'b') {
                    mazeBG.addChild(makeObstacle(ch, x, z, blockAppearance));
                    drawBlock(graphics, x, z);
                } else if (ch == 'c') {
                    mazeBG.addChild(makeObstacle(ch, x, z, cylinderAppearance));
                    drawCylinder(graphics, x, z);
                }
            }
        }
        graphics.dispose();
    }

    private TransformGroup makeObstacle(char ch, int x, int z, Appearance appearance) {
        Primitive obstacle;
        if (ch == 'b') {
            obstacle = new Box(RADIUS, HEIGHT / 2, RADIUS, Primitive.GENERATE_TEXTURE_COORDS |
                    Primitive.GENERATE_NORMALS, appearance);
        } else {
            obstacle = new Cylinder(RADIUS, HEIGHT, Primitive.GENERATE_TEXTURE_COORDS |
                    Primitive.GENERATE_NORMALS, appearance);
        }

        TransformGroup positionTG = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(x, HEIGHT / 2, z));
        positionTG.setTransform(transform3D);
        positionTG.addChild(obstacle);
        return positionTG;
    }

    private void drawBlock(Graphics graphics, int i, int j) {
        graphics.setColor(Color.blue);
        graphics.fillRect(i * IMAGE_STEP, j * IMAGE_STEP, IMAGE_STEP, IMAGE_STEP);
    }

    private void drawCylinder(Graphics graphics, int i, int j) {
        graphics.setColor(Color.green);
        graphics.fillOval(i * IMAGE_STEP, j * IMAGE_STEP, IMAGE_STEP, IMAGE_STEP);
    }

    public BranchGroup getMazeBG() {
        return mazeBG;
    }

    public Vector3d getMazeStartPosn() {
        return new Vector3d(xStartPosition, USER_HEIGHT, zStartPosition);
    }

    public BufferedImage getMazeBImage() {
        return mazeBImage;
    }

    public Point getImageStartPosition() {
        return new Point(xStartPosition * IMAGE_STEP, zStartPosition * IMAGE_STEP);
    }

    public int getImageStep() {
        return IMAGE_STEP;
    }

    public boolean canMoveTo(double xWorld, double zWorld) {
        int x = (int) Math.round(xWorld);
        int z = (int) Math.round(zWorld);

        if ((x < 0) || (x >= LENGTH) || (z < 0) || (z >= LENGTH)) {
            return true;
        }

        if ((maze[z][x] == 'b') || (maze[z][x] == 'c')) {
            return false;
        }
        return true;
    }
}
