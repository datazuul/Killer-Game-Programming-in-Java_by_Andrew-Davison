package com.example.terra3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

public class GroundShape extends OrientedShape3D {

    private static final int NUM_VERTS = 4;

    public GroundShape(float screenSize, String fnm) {
        setAlignmentAxis(0.0f, 1.0f, 0.0f);

        createGeometry(screenSize);
        createAppearance(fnm);
    }

    private void createGeometry(float screenSize) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        Point3f point3f1 = new Point3f(-screenSize / 2, 0.0f, 0.0f);
        Point3f point3f2 = new Point3f(screenSize / 2, 0.0f, 0.0f);
        Point3f point3f3 = new Point3f(screenSize / 2, screenSize, 0.0f);
        Point3f point3f4 = new Point3f(-screenSize / 2, screenSize, 0.0f);

        plane.setCoordinate(0, point3f1);
        plane.setCoordinate(1, point3f2);
        plane.setCoordinate(2, point3f3);
        plane.setCoordinate(3, point3f4);

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

    private void createAppearance(String fnm) {
        Appearance appearance = new Appearance();

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        appearance.setTransparencyAttributes(transparencyAttributes);

        TextureLoader textureLoader = new TextureLoader(fnm, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();
        if (texture2D == null) {
            System.out.println("Image loading failed for " + fnm);
        } else {
            texture2D.setMinFilter(Texture2D.BASE_LEVEL_LINEAR);
            texture2D.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
            appearance.setTexture(texture2D);
        }

        setAppearance(appearance);
    }
}
