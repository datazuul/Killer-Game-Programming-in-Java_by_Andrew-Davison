package com.example.nettour3D;

import com.sun.j3d.utils.geometry.Text2D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.ArrayList;

public class CheckerFloor {

    private static final int FLOOR_LENGTH = 20;

    private static final Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);
    private static final Color3f green = new Color3f(0.0f, 0.5f, 0.1f);
    private static final Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private BranchGroup floorBG;

    public CheckerFloor() {
        ArrayList blueCoordinates = new ArrayList();
        ArrayList greenCoordinates = new ArrayList();
        floorBG = new BranchGroup();

        boolean isBlue;
        for (int z = -FLOOR_LENGTH / 2; z <= (FLOOR_LENGTH / 2) - 1; z++) {
            isBlue = (z % 2 == 0) ? true : false;
            for (int x = -FLOOR_LENGTH / 2; x <= (FLOOR_LENGTH / 2) - 1; x++) {
                if (isBlue) {
                    createCoordinates(x, z, blueCoordinates);
                } else {
                    createCoordinates(x, z, greenCoordinates);
                }
                isBlue = !isBlue;
            }
        }
        floorBG.addChild(new ColouredTiles(blueCoordinates, blue));
        floorBG.addChild(new ColouredTiles(greenCoordinates, green));

        addOriginMarker();
        labelAxes();
    }

    private void createCoordinates(int x, int z, ArrayList arrayList) {
        Point3f point3f1 = new Point3f(x, 0.0f, z + 1.0f);
        Point3f point3f2 = new Point3f(x + 1.0f, 0.0f, z + 1.0f);
        Point3f point3f3 = new Point3f(x + 1.0f, 0.0f, z);
        Point3f point3f4 = new Point3f(x, 0.0f, z);
        arrayList.add(point3f1);
        arrayList.add(point3f2);
        arrayList.add(point3f3);
        arrayList.add(point3f4);
    }

    private void addOriginMarker() {
        Point3f point3f1 = new Point3f(-0.25f, 0.01f, 0.25f);
        Point3f point3f2 = new Point3f(0.25f, 0.01f, 0.25f);
        Point3f point3f3 = new Point3f(0.25f, 0.01f, -0.25f);
        Point3f point3f4 = new Point3f(-0.25f, 0.01f, -0.25f);

        ArrayList arrayList = new ArrayList();
        arrayList.add(point3f1);
        arrayList.add(point3f2);
        arrayList.add(point3f3);
        arrayList.add(point3f4);

        floorBG.addChild(new ColouredTiles(arrayList, medRed));
    }

    private void labelAxes() {
        Vector3d vector3d = new Vector3d();
        for (int i = -FLOOR_LENGTH / 2; i <= FLOOR_LENGTH / 2; i++) {
            vector3d.x = i;
            floorBG.addChild(makeText(vector3d, "" + i));
        }

        vector3d.x = 0;
        for (int i = -FLOOR_LENGTH / 2; i <= FLOOR_LENGTH / 2; i++) {
            vector3d.z = i;
            floorBG.addChild(makeText(vector3d, "" + i));
        }
    }

    private TransformGroup makeText(Vector3d vector3d, String text) {
        Text2D text2D = new Text2D(text, white, "SansSerif", 36, Font.BOLD);

        TransformGroup transformGroup = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(vector3d);
        transformGroup.setTransform(transform3D);
        transformGroup.addChild(text2D);
        return transformGroup;
    }

    public BranchGroup getFloorBG() {
        return floorBG;
    }
}
