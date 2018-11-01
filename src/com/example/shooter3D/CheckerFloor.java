package com.example.shooter3D;

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

    private static final int FLOOR_LEN = 20;

    private static final Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);
    private static final Color3f green = new Color3f(0.0f, 0.5f, 0.1f);
    private static final Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private BranchGroup floorBG;

    public CheckerFloor() {
        ArrayList blueCoords = new ArrayList();
        ArrayList greenCoords = new ArrayList();
        floorBG = new BranchGroup();

        boolean isBlue;
        for (int z = -FLOOR_LEN / 2; z <= (FLOOR_LEN / 2) - 1; z++) {
            isBlue = (z % 2 == 0) ? true : false;
            for (int x = -FLOOR_LEN / 2; x <= (FLOOR_LEN / 2) - 1; x++) {
                if (isBlue) {
                    createCoords(x, z, blueCoords);
                } else {
                    createCoords(x, z, greenCoords);
                }
                isBlue = !isBlue;
            }
        }
        floorBG.addChild(new ColouredTiles(blueCoords, blue));
        floorBG.addChild(new ColouredTiles(greenCoords, green));

        addOriginMarker();
        labelAxes();
    }

    private void createCoords(int x, int z, ArrayList coords) {
        Point3f point3f1 = new Point3f(x, 0.0f, z + 1.0f);
        Point3f point3f2 = new Point3f(x + 1.0f, 0.0f, z + 1.0f);
        Point3f point3f3 = new Point3f(x, 0.0f, z);
        Point3f point3f4 = new Point3f(x, 0.0f, z);

        coords.add(point3f1);
        coords.add(point3f2);
        coords.add(point3f3);
        coords.add(point3f4);
    }

    private void addOriginMarker() {
        Point3f point3f1 = new Point3f(-0.25f, 0.01f, 0.25f);
        Point3f point3f2 = new Point3f(0.25f, 0.01f, 0.25f);
        Point3f point3f3 = new Point3f(0.25f, 0.01f, -0.25f);
        Point3f point3f4 = new Point3f(-0.25f, 0.01f, -0.25f);

        ArrayList oCoords = new ArrayList();
        oCoords.add(point3f1);
        oCoords.add(point3f2);
        oCoords.add(point3f3);
        oCoords.add(point3f4);

        floorBG.addChild(new ColouredTiles(oCoords, medRed));
    }

    private void labelAxes() {
        Vector3d pt = new Vector3d();
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            pt.z = i;
            floorBG.addChild(makeText(pt, "" + i));
        }
    }

    private TransformGroup makeText(Vector3d vector3d, String text) {
        Text2D message = new Text2D(text, white, "SansSerif", 36, Font.BOLD);
        message.setPickable(false);

        TransformGroup transformGroup = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(vector3d);
        transformGroup.setTransform(transform3D);
        transformGroup.addChild(message);
        return transformGroup;
    }

    public BranchGroup getFloorBG() {
        return floorBG;
    }
}
