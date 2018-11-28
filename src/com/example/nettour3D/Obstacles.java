package com.example.nettour3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.StringTokenizer;

public class Obstacles {

    private static final float RADIUS = 0.1f;
    private static final float HEIGHT = 1.0f;

    private static final int FLOOR_LENGTH = 20;

    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f red = new Color3f(0.9f, 0.1f, 0.2f);

    private boolean isObstacles[][];
    private BoundingSphere[][] obstaclesBSs;
    private Group obstaclesGroup;

    public Obstacles() {
        isObstacles = new boolean[FLOOR_LENGTH + 1][FLOOR_LENGTH + 1];
        obstaclesBSs = new BoundingSphere[FLOOR_LENGTH + 1][FLOOR_LENGTH + 1];
        for (int z = 0; z <= FLOOR_LENGTH; z++) {
            for (int x = 0; x <= FLOOR_LENGTH; x++) {
                isObstacles[z][x] = false;
                obstaclesBSs[z][x] = null;
            }
        }
        obstaclesGroup = new Group();
    }

    public void store(String line) {
        int x = 0;
        int z = 0;
        String coordinateString;
        StringTokenizer pointsST;
        StringTokenizer coordinatesST = new StringTokenizer(line);
        while (coordinatesST.hasMoreTokens()) {
            coordinateString = coordinatesST.nextToken();
            pointsST = new StringTokenizer(coordinateString, "(,)");
            try {
                x = Integer.parseInt(pointsST.nextToken());
                z = Integer.parseInt(pointsST.nextToken());
            } catch (NumberFormatException e) {
                System.out.println("Incorrect format for obstacle data in tours file");
                System.out.println(e);
                break;
            }
            markObstacle(x, z);
        }
    }

    private void markObstacle(int x, int z) {
        if ((x < -FLOOR_LENGTH / 2) || (x > FLOOR_LENGTH / 2)) {
            System.out.println("Obstacle x point out of bounds : " + x);
            x = 0;
        }
        if ((z < -FLOOR_LENGTH / 2) || (z > FLOOR_LENGTH / 2)) {
            System.out.println("Obstacle z point out of bounds : " + z);
            z = 0;
        }
        isObstacles[z + (FLOOR_LENGTH / 2)][x + (FLOOR_LENGTH / 2)] = true;
        obstaclesBSs[z + (FLOOR_LENGTH / 2)][x + (FLOOR_LENGTH / 2)] = new BoundingSphere(new Point3d(x, 0.0, z),
                RADIUS);
        obstaclesGroup.addChild(makeObstacles(x, z));
    }

    public void print() {
        for (int x = (-FLOOR_LENGTH / 2); x <= (FLOOR_LENGTH / 2); x++) {
            if (x == 0) {
                System.out.print("0");
            } else if (x % 5 == 0) {
                System.out.print("*");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println("");
        for (int x = (-FLOOR_LENGTH / 2); x <= (FLOOR_LENGTH / 2); x++) {
            System.out.print("-");
        }
        System.out.println("");

        for (int z = 0; z <= FLOOR_LENGTH; z++) {
            for (int x = 0; x <= FLOOR_LENGTH; x++) {
                if (isObstacles[z][x]) {
                    System.out.print("0");
                } else {
                    System.out.print(" ");
                }
            }
            if ((z - FLOOR_LENGTH / 2) % 5 == 0) {
                System.out.println("| " + (z - FLOOR_LENGTH / 2));
            } else {
                System.out.println("|");
            }
        }

        for (int x = (-FLOOR_LENGTH / 2); x <= (FLOOR_LENGTH / 2); x++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public boolean nearObstacle(Point3d point3d, double radius) {
        if ((point3d.x < -FLOOR_LENGTH / 2) || (point3d.x > FLOOR_LENGTH / 2) || (point3d.z < -FLOOR_LENGTH / 2) ||
                (point3d.z > FLOOR_LENGTH / 2)) {
            return true;
        }
        BoundingSphere boundingSphere = new BoundingSphere(point3d, radius);
        for (int z = 0; z <= FLOOR_LENGTH; z++) {
            for (int x = 0; x <= FLOOR_LENGTH; x++) {
                if (isObstacles[z][x]) {
                    if (obstaclesBSs[z][x].intersect(boundingSphere)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private TransformGroup makeObstacles(int x, int z) {
        Appearance appearance = new Appearance();
        Material material = new Material(black, black, red, specular, 100.0f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        Cylinder cylinder = new Cylinder(RADIUS, HEIGHT, Cylinder.GENERATE_NORMALS, appearance);

        TransformGroup transformGroup = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(x, HEIGHT / 2, z));
        transformGroup.setTransform(transform3D);
        transformGroup.addChild(cylinder);
        return transformGroup;
    }

    public Group getObstaclesGroup() {
        return obstaclesGroup;
    }
}
