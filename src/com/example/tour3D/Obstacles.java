package com.example.tour3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.StringTokenizer;

public class Obstacles {

    private static final float RADIUS = 0.1f;
    private static final float HEIGHT = 1.0f;

    private static final int FLOOR_LEN = 20;

    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f red = new Color3f(0.9f, 0.1f, 0.2f);

    private boolean obs[][];
    private BoundingSphere obsBounds[][];
    private Group obsGroup;

    public Obstacles() {
        obs = new boolean[FLOOR_LEN + 1][FLOOR_LEN + 1];
        obsBounds = new BoundingSphere[FLOOR_LEN + 1][FLOOR_LEN + 1];
        for (int z = 0; z <= FLOOR_LEN; z++) {
            for (int x = 0; x <= FLOOR_LEN; x++) {
                obs[z][x] = false;
                obsBounds[z][x] = null;
            }
        }
        obsGroup = new Group();
    }

    public void store(String line) {
        int x = 0;
        int z = 0;
        String coordStr;
        StringTokenizer points;
        StringTokenizer coords = new StringTokenizer(line);
        while (coords.hasMoreTokens()) {
            coordStr = coords.nextToken();
            points = new StringTokenizer(coordStr, "(,)");
            try {
                x = Integer.parseInt(points.nextToken());
                z = Integer.parseInt(points.nextToken());
            } catch (NumberFormatException ex) {
                System.out.println("Incorrect format for obstacle data in tours file");
                break;
            }
            markObstacle(x, z);
        }
    }

    private void markObstacle(int x, int z) {
        if ((x < -FLOOR_LEN / 2) || (x > FLOOR_LEN / 2)) {
            System.out.println("Obstacle x point out of bounds : " + x);
            x = 0;
        }
        if ((z < -FLOOR_LEN / 2) || (z > FLOOR_LEN / 2)) {
            System.out.println("Obstacle z point out of bounds : " + z);
            z = 0;
        }
        obs[z + (FLOOR_LEN / 2)][x + (FLOOR_LEN / 2)] = true;
        obsBounds[z + (FLOOR_LEN / 2)][x + (FLOOR_LEN / 2)] = new BoundingSphere(new Point3d(x, 0.0, z), RADIUS);
        obsGroup.addChild(makeObs(x, z));
    }

    public void print() {
        for (int x = (-FLOOR_LEN / 2); x <= (FLOOR_LEN / 2); x++) {
            if (x == 0) {
                System.out.print("0");
            } else if (x % 5 == 0) {
                System.out.print("*");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println("");
        for (int x = (-FLOOR_LEN / 2); x <= (FLOOR_LEN / 2); x++) {
            System.out.print("-");
        }
        System.out.println("");

        for (int z = 0; z <= FLOOR_LEN; z++) {
            for (int x = 0; x <= FLOOR_LEN; x++) {
                if (obs[z][x]) {
                    System.out.print("0");
                } else {
                    System.out.print(" ");
                }
            }
            if ((z - FLOOR_LEN / 2) % 5 == 0) {
                System.out.println("| " + (z - FLOOR_LEN / 2));
            } else {
                System.out.println("|");
            }
        }

        for (int x = (-FLOOR_LEN / 2); x <= (FLOOR_LEN / 2); x++) {
            System.out.print("-");
        }
        System.out.println("");
    }

    public boolean nearObstacle(Point3d pos, double radius) {
        if ((pos.x < -FLOOR_LEN / 2) || (pos.x > FLOOR_LEN / 2) || (pos.z < -FLOOR_LEN / 2) || (pos.z > FLOOR_LEN / 2)) {
            return true;
        }

        BoundingSphere boundingSphere = new BoundingSphere(pos, radius);
        for (int z = 0; z <= FLOOR_LEN; z++) {
            for (int x = 0; x <= FLOOR_LEN; x++) {
                if (obs[z][x]) {
                    if (obsBounds[z][x].intersect(boundingSphere)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private TransformGroup makeObs(int x, int z) {
        Appearance obsApp = new Appearance();
        Material material = new Material(black, black, red, specular, 100.0f);
        material.setLightingEnable(true);
        obsApp.setMaterial(material);
        Cylinder cylinder = new Cylinder(RADIUS, HEIGHT, Cylinder.GENERATE_NORMALS, obsApp);

        TransformGroup posnTG = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(x, HEIGHT / 2, z));
        posnTG.setTransform(transform3D);
        posnTG.addChild(cylinder);
        return posnTG;
    }

    public Group getObsGroup() {
        return obsGroup;
    }
}
