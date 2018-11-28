package com.example.nettour3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.util.ArrayList;

public class ColouredTiles extends Shape3D {

    private QuadArray quadArray;

    public ColouredTiles(ArrayList arrayList, Color3f color3f) {
        quadArray = new QuadArray(arrayList.size(), GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        createGeometry(arrayList, color3f);
        createAppearance();
    }

    private void createGeometry(ArrayList arrayList, Color3f color3f) {
        int numberOfPoints = arrayList.size();

        Point3f[] point3fs = new Point3f[numberOfPoints];
        arrayList.toArray(point3fs);
        quadArray.setCoordinates(0, point3fs);

        Color3f[] color3fs = new Color3f[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            color3fs[i] = color3f;
        }
        quadArray.setColors(0, color3fs);

        setGeometry(quadArray);
    }

    private void createAppearance() {
        Appearance appearance = new Appearance();

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polygonAttributes);

        setAppearance(appearance);
    }
}
