package com.example.fractalland3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class ColouredPlane extends Shape3D {

    private static final int NUM_VERTS = 4;

    public ColouredPlane(Point3d point3d1, Point3d point3d2, Point3d point3d3, Point3d point3d4, Vector3f normVector3f,
                         Color3f color3f) {
        createGeometry(point3d1, point3d2, point3d3, point3d4, normVector3f);
        createAppearance(color3f);
    }

    private void createGeometry(Point3d point3d1, Point3d point3d2, Point3d point3d3, Point3d point3d4,
                                Vector3f normVector3f) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.NORMALS);

        plane.setCoordinate(0, point3d1);
        plane.setCoordinate(1, point3d2);
        plane.setCoordinate(2, point3d3);
        plane.setCoordinate(3, point3d4);

        Vector3f[] norms = new Vector3f[NUM_VERTS];
        for (int i = 0; i < NUM_VERTS; i++) {
            norms[i] = normVector3f;
        }
        plane.setNormals(0, norms);
        setGeometry(plane);
    }

    private void createAppearance(Color3f color3f) {
        Appearance appearance = new Appearance();

        Material material = new Material();
        material.setDiffuseColor(color3f);
        material.setLightingEnable(true);

        appearance.setMaterial(material);
        setAppearance(appearance);
    }
}
