package com.example.terra3D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;

public class TexturedPlane extends Shape3D {

    private static final int NUM_VERTS = 4;

    public TexturedPlane(Point3d point3d1, Point3d point3d2, Point3d point3d3, Point3d point3d4, Texture2D texture2D) {
        createGeometry(point3d1, point3d2, point3d3, point3d4);

        Appearance appearance = new Appearance();
        appearance.setTexture(texture2D);
        setAppearance(appearance);
    }

    private void createGeometry(Point3d p1, Point3d p2, Point3d p3, Point3d p4) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        plane.setCoordinate(0, p1);
        plane.setCoordinate(1, p2);
        plane.setCoordinate(2, p3);
        plane.setCoordinate(3, p4);

        TexCoord2f texCoord2f = new TexCoord2f();
        texCoord2f.set(0.0f, 0.0f);
        plane.setTextureCoordinate(0, 0, texCoord2f);
        texCoord2f.set(1.0f, 0.0f);
        plane.setTextureCoordinate(0, 1, texCoord2f);
        texCoord2f.set(1.0f, 1.0f);
        plane.setTextureCoordinate(0, 2, texCoord2f);
        texCoord2f.set(0.0f, 1.0f);
        plane.setTextureCoordinate(0, 3, texCoord2f);

        setGeometry(plane);
    }
}
