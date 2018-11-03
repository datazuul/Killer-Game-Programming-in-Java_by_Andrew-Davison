package com.example.maze3D;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class TexturedFloor {

    private static final int FLOOR_LENGTH = 80;
    private static final int STEP = 4;

    private static final String FLOOR_IMAGE = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/stone.jpg";

    private BranchGroup floorBG;

    public TexturedFloor() {
        ArrayList coords = new ArrayList();
        floorBG = new BranchGroup();

        for (int z = FLOOR_LENGTH / 2; z >= (-FLOOR_LENGTH / 2) + STEP; z -= STEP) {
            for (int x = -FLOOR_LENGTH / 2; x <= (FLOOR_LENGTH / 2) - STEP; x += STEP) {
                createCoords(x, z, coords);
            }
        }

        Vector3f upNormal = new Vector3f(0.0f, 1.0f, 0.0f);
        floorBG.addChild(new TexturedPlane(coords, FLOOR_IMAGE, upNormal));
    }

    private void createCoords(int x, int z, ArrayList coords) {
        Point3f point3f1 = new Point3f(x, 0.0f, z);
        Point3f point3f2 = new Point3f(x + STEP, 0.0f, z);
        Point3f point3f3 = new Point3f(x + STEP, 0.0f, z - STEP);
        Point3f point3f4 = new Point3f(x, 0.0f, z - STEP);
        coords.add(point3f1);
        coords.add(point3f2);
        coords.add(point3f3);
        coords.add(point3f4);
    }

    public BranchGroup getFloorBG() {
        return floorBG;
    }
}
