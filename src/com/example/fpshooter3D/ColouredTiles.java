package com.example.fpshooter3D;

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
        int numPoints = coords.size();

        Point3f[] point3fs = new Point3f[numPoints];
        coords.toArray(point3fs);
        plane.setCoordinates(0, point3fs);

        Color3f[] color3fs = new Color3f[numPoints];
        for (int i = 0; i < numPoints; i++) {
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
