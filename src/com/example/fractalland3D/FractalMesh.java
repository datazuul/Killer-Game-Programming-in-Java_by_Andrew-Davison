package com.example.fractalland3D;

import javax.vecmath.Point3d;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

public class FractalMesh {

    private static final int WORLD_LEN = 64;

    private static final double MIN_HEIGHT = -2.0;
    private static final double MAX_HEIGHT = 8.0;

    private Point3d mesh[][];
    private DecimalFormat decimalFormat;

    private double flatness;
    private Random random;

    public FractalMesh(double flatness) {
        this.flatness = flatness;
        mesh = new Point3d[WORLD_LEN + 1][WORLD_LEN + 1];
        decimalFormat = new DecimalFormat("0.##");
        random = new Random(1L);
        makeMesh();
    }

    private void makeMesh() {
        System.out.println("Building the landscape...please wait");
        mesh[0][0] = new Point3d(-WORLD_LEN / 2, randomHeight(), -WORLD_LEN / 2);
        mesh[0][WORLD_LEN] = new Point3d(WORLD_LEN / 2, randomHeight(), -WORLD_LEN / 2);
        mesh[WORLD_LEN][0] = new Point3d(-WORLD_LEN / 2, randomHeight(), WORLD_LEN / 2);
        mesh[WORLD_LEN][WORLD_LEN] = new Point3d(WORLD_LEN / 2, randomHeight(), WORLD_LEN / 2);

        divideMesh((MAX_HEIGHT - MIN_HEIGHT) / flatness, WORLD_LEN / 2);
    }

    private double randomHeight() {
        return (Math.random() * (MAX_HEIGHT - MIN_HEIGHT) + MIN_HEIGHT);
    }

    private void divideMesh(double dHeight, int stepSize) {
        int xPt, zPt;
        if (stepSize >= 1) {
            zPt = stepSize;
            while (zPt < WORLD_LEN + 1) {
                xPt = stepSize;
                while (xPt < WORLD_LEN + 1) {
                    mesh[zPt][xPt] = getDiamond(zPt, xPt, dHeight, stepSize);
                    xPt += (stepSize * 2);
                }
                zPt += (stepSize * 2);
            }

            zPt = stepSize;
            while (zPt < WORLD_LEN + 1) {
                mesh[zPt][0] = getSquare(zPt, 0, dHeight, stepSize);
                xPt = stepSize;
                while (xPt < WORLD_LEN + 1) {
                    getSquares(zPt, xPt, dHeight, stepSize);
                    xPt += (stepSize * 2);
                }
                zPt += (stepSize * 2);
            }

            xPt = stepSize;
            while (xPt < WORLD_LEN + 1) {
                mesh[WORLD_LEN][xPt] = getSquare(WORLD_LEN, xPt, dHeight, stepSize);
                xPt += (stepSize * 2);
            }
            divideMesh(dHeight / flatness, stepSize / 2);
        }
    }

    private void getSquares(int z, int x, double dHeight, int stepSize) {
        mesh[cCoord(z - stepSize)][x] = getSquare(cCoord(z - stepSize), x, dHeight, stepSize);
        mesh[z][cCoord(x + stepSize)] = getSquare(z, cCoord(x + stepSize), dHeight, stepSize);
    }

    private Point3d getDiamond(int z, int x, double dHeight, int stepSize) {
        Point3d leftBack = mesh[cCoord(z - stepSize)][cCoord(x - stepSize)];
        Point3d rightBack = mesh[cCoord(z - stepSize)][cCoord(x + stepSize)];
        Point3d leftFront = mesh[cCoord(z + stepSize)][cCoord(x - stepSize)];
        Point3d rightFront = mesh[cCoord(z + stepSize)][cCoord(x + stepSize)];

        double height = calcHeight(leftBack, rightBack, leftFront, rightFront, dHeight);
        double xWorld = x - (WORLD_LEN / 2);
        double zWorld = z - (WORLD_LEN / 2);

        return new Point3d(xWorld, height, zWorld);
    }

    private Point3d getSquare(int z, int x, double dHeight, int stepSize) {
        Point3d back = mesh[cCoord(z - stepSize)][x];
        Point3d front = mesh[cCoord(z + stepSize)][x];
        Point3d left = mesh[z][cCoord(x - stepSize)];
        Point3d right = mesh[z][cCoord(x + stepSize)];

        double height = calcHeight(back, front, left, right, dHeight);
        double xWorld = x - (WORLD_LEN / 2);
        double zWorld = z - (WORLD_LEN / 2);

        return new Point3d(xWorld, height, zWorld);
    }

    private int cCoord(int coordIdx) {
        if (coordIdx < 0) {
            return WORLD_LEN + coordIdx;
        } else if (coordIdx > WORLD_LEN) {
            return coordIdx - WORLD_LEN;
        } else {
            return coordIdx;
        }
    }

    private double calcHeight(Point3d back, Point3d front, Point3d left, Point3d right, double dHeight) {
        double height = (back.y + front.y + left.y + right.y) / 4.0f + randomRange(dHeight);

        if (height < MIN_HEIGHT) {
            height = MIN_HEIGHT;
        } else if (height > MAX_HEIGHT) {
            height = height % MAX_HEIGHT;
        }
        return height;
    }

    private double randomRange(double h) {
        return ((Math.random() * 2 * h) - h);
    }

    public Point3d[] getVertices() {
        int numVerts = WORLD_LEN * WORLD_LEN * 4;
        Point3d vertices[] = new Point3d[numVerts];

        int vPos = 0;
        for (int z = 0; z < WORLD_LEN; z++) {
            for (int x = 0; x < WORLD_LEN; x++) {
                vertices[vPos++] = mesh[z + 1][x];
                vertices[vPos++] = mesh[z + 1][x + 1];
                vertices[vPos++] = mesh[z][x + 1];
                vertices[vPos++] = mesh[z][x];
            }
        }
        return vertices;
    }

    public void printMesh(int axis) {
        File file = new File("mesh.txt");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(file), true);

            if (axis == 0) {
                printWriter.println("----------World X Coords----------");
            } else if (axis == 1) {
                printWriter.println("----------World Y Coords----------");
            } else {
                printWriter.println("----------World Z Coords----------");
            }

            for (int z = 0; z < WORLD_LEN + 1; z++) {
                for (int x = 0; x < WORLD_LEN + 1; x++) {
                    if (axis == 0) {
                        printWriter.print(decimalFormat.format(mesh[z][x].x) + " ");
                    } else if (axis == 1) {
                        printWriter.print(decimalFormat.format(mesh[z][x].y) + " ");
                    } else {
                        printWriter.print(decimalFormat.format(mesh[z][x].z));
                    }
                }
                printWriter.println();
            }
            printWriter.println("------------------------------------------------------------");
            System.out.println("Mesh written to mesh.txt");
        } catch (Exception e) {
            System.out.println("Could not write mesh to mesh.txt");
        }
    }
}
