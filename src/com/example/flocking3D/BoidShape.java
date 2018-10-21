package com.example.flocking3D;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.IndexedTriangleArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class BoidShape extends Shape3D {

    private static final int NUM_VERTS = 4;
    private static final int NUM_INDICES = 12;
    private static final Color3f purple = new Color3f(0.5f, 0.2f, 0.8f);

    public BoidShape(Color3f color3f) {
        IndexedTriangleArray indexedTriangleArray = new IndexedTriangleArray(NUM_VERTS, GeometryArray.COORDINATES |
                GeometryArray.COLOR_3, NUM_INDICES);

        Point3f[] point3fs = new Point3f[NUM_VERTS];
        point3fs[0] = new Point3f(0.0f, 0.0f, 0.25f);
        point3fs[1] = new Point3f(0.2f, 0.0f, -0.25f);
        point3fs[2] = new Point3f(-0.2f, 0.0f, -0.25f);
        point3fs[3] = new Point3f(0.0f, 0.25f, -0.2f);

        int[] indices = {2, 0, 3, 2, 1, 0, 0, 1, 3, 1, 2, 3};

        indexedTriangleArray.setCoordinates(0, point3fs);
        indexedTriangleArray.setCoordinateIndices(0, indices);

        Color3f[] color3fs = new Color3f[NUM_VERTS];
        color3fs[0] = purple;
        for (int i = 1; i < NUM_VERTS; i++) {
            color3fs[i] = color3f;
        }

        indexedTriangleArray.setColors(0, color3fs);
        indexedTriangleArray.setColorIndices(0, indices);

        setGeometry(indexedTriangleArray);
    }
}
