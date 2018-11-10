package com.example.trees3D;

import com.sun.j3d.utils.geometry.Text2D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import java.awt.*;

public class CheckerFloor {

    private static final int FLOOR_LEN = 20;

    private static final Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);
    private static final Color3f green = new Color3f(0.0f, 0.5f, 0.1f);
    private static final Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private BranchGroup floorBG;

    public CheckerFloor() {
        floorBG = new BranchGroup();

        boolean isBlue;
        for (int z = -FLOOR_LEN / 2; z <= (FLOOR_LEN / 2) - 1; z++) {
            isBlue = (z % 2 == 0) ? true : false;
            for (int x = -FLOOR_LEN / 2; x <= (FLOOR_LEN / 2) - 1; x++) {
                createTile(x, z, isBlue);
                isBlue = !isBlue;
            }
        }
        addOriginMarker();
        labelAxes();
    }

    private void createTile(int x, int z, boolean isBlue) {
        Point3f p1 = new Point3f(x, 0.0f, z + 1.0f);
        Point3f p2 = new Point3f(x + 1.0f, 0.0f, z + 1.0f);
        Point3f p3 = new Point3f(x + 1.0f, 0.0f, z);
        Point3f p4 = new Point3f(x, 0.0f, z);

        Color3f color3f = (isBlue) ? blue : green;
        floorBG.addChild(new ColouredTile(p1, p2, p3, p4, color3f));
    }

    private void addOriginMarker() {
        Point3f p1 = new Point3f(-0.25f, 0.01f, 0.25f);
        Point3f p2 = new Point3f(0.25f, 0.01f, 0.25f);
        Point3f p3 = new Point3f(0.25f, 0.01f, -0.25f);
        Point3f p4 = new Point3f(-0.25f, 0.01f, -0.25f);

        floorBG.addChild(new ColouredTile(p1, p2, p3, p4, medRed));
    }

    private void labelAxes() {
        Vector3d pt = new Vector3d();
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            pt.x = i;
            floorBG.addChild(makeText(pt, "" + i));
        }

        pt.x = 0;
        for (int i = -FLOOR_LEN / 2; i <= FLOOR_LEN / 2; i++) {
            pt.z = i;
            floorBG.addChild(makeText(pt, "" + i));
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
