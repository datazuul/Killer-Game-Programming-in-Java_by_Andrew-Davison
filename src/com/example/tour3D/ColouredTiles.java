package com.example.tour3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.util.ArrayList;

public class ColouredTiles extends Shape3D {

    private QuadArray plane;

    public ColouredTiles(ArrayList coords, Color3f color3f) {
        plane = new QuadArray(coords.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3);

        createGeometry(coords, color3f);
        createAppearance();
    }

    private void createGeometry(ArrayList coords, Color3f color3f) {
        int numpoints = coords.size();

        Point3f[] points = new Point3f[numpoints];
        coords.toArray(points);
        plane.setCoordinates(0, points);

        Color3f cols[] = new Color3f[numpoints];
        for (int i = 0; i < numpoints; i++) {
            cols[i] = color3f;
        }
        plane.setColors(0, cols);

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
