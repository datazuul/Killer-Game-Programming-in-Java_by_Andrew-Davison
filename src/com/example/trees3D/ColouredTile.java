package com.example.trees3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class ColouredTile extends Shape3D {

    private static final int NUM_VERTS = 4;
    private QuadArray plane;

    public ColouredTile(Point3f point3f1, Point3f point3f2, Point3f point3f3, Point3f point3f4, Color3f color3f) {
        plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        createGeometry(point3f1, point3f2, point3f3, point3f4, color3f);
        createAppearance();
    }

    private void createGeometry(Point3f point3f1, Point3f point3f2, Point3f point3f3, Point3f point3f4, Color3f color3f) {
        plane.setCoordinate(0, point3f1);
        plane.setCoordinate(1, point3f2);
        plane.setCoordinate(2, point3f3);
        plane.setCoordinate(3, point3f4);

        Color3f[] color3fs = new Color3f[NUM_VERTS];
        for (int i = 0; i < NUM_VERTS; i++) {
            color3fs[i] = color3f;
        }

        plane.setColors(0, color3fs);

        setGeometry(plane);
    }

    private void createAppearance() {
        Appearance appearance = new Appearance();

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);

        appearance.setPolygonAttributes(polygonAttributes);

        setAppearance(appearance);
    }
}
