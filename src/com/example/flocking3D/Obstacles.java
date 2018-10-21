package com.example.flocking3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class Obstacles {

    private static final int FLOOR_LEN = 20;
    private static final float MAX_HEIGHT = 8.0f;
    private static final float RADIUS = 0.3f;

    private ArrayList obsList;
    private BranchGroup obsBG;

    public Obstacles(int numObs) {
        System.out.println("Number of Obstacles : " + numObs);

        obsList = new ArrayList();
        obsBG = new BranchGroup();

        Appearance blueApp = makeBlueApp();

        float x, z, height;
        Point3d lower, upper;
        BoundingBox boundingBox;
        for (int i = 0; i < numObs; i++) {
            x = randomFloorPosn();
            z = randomFloorPosn();
            height = (float) (Math.random() * MAX_HEIGHT);
            lower = new Point3d(x - RADIUS, 0.0f, z - RADIUS);
            upper = new Point3d(x + RADIUS, height, z + RADIUS);

            boundingBox = new BoundingBox(lower, upper);
            obsList.add(boundingBox);

            obsBG.addChild(makeSceneOb(height, x, z, blueApp));
        }
    }

    private float randomFloorPosn() {
        return (float) ((Math.random() * FLOOR_LEN) - (FLOOR_LEN / 2));
    }

    private Appearance makeBlueApp() {
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f blue = new Color3f(0.3f, 0.3f, 0.8f);
        Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

        Material blueMat = new Material(blue, black, blue, specular, 80.0f);
        blueMat.setLightingEnable(true);

        Appearance blueApp = new Appearance();
        blueApp.setMaterial(blueMat);
        return blueApp;
    }

    private TransformGroup makeSceneOb(float height, float x, float z, Appearance blueApp) {
        Cylinder cylinder = new Cylinder(RADIUS, height, blueApp);

        TransformGroup transformGroup = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(x, height / 2, z));
        transformGroup.setTransform(transform3D);
        transformGroup.addChild(cylinder);
        return transformGroup;
    }

    public BranchGroup getObsBG() {
        return obsBG;
    }

    public boolean isOverlapping(BoundingSphere boundingSphere) {
        BoundingBox boundingBox;
        for (int i = 0; i < obsList.size(); i++) {
            boundingBox = (BoundingBox) obsList.get(i);
            if (boundingBox.intersect(boundingSphere)) {
                return true;
            }
        }
        return false;
    }
}