package com.example.fpshooter3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

public class TexturedPlane extends Shape3D {

    private static final int NUM_VERTS = 4;

    public TexturedPlane(Point3f point3f1, Point3f point3f2, Point3f point3f3, Point3f point3f4, String fnm) {
        createGeometry(point3f1, point3f2, point3f3, point3f4);
        createAppearance(fnm);
    }

    private void createGeometry(Point3f point3f1, Point3f point3f2, Point3f point3f3, Point3f point3f4) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

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
        System.out.println("Loading texture from " + fnm);
        TextureLoader textureLoader = new TextureLoader(fnm, null);
        ImageComponent2D imageComponent2D = textureLoader.getImage();
        if (imageComponent2D == null) {
            System.out.println("Load failed for texture : " + fnm);
        } else {
            Appearance appearance = new Appearance();

            TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
            transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
            appearance.setTransparencyAttributes(transparencyAttributes);

            Texture2D texture2D = new Texture2D(Texture2D.BASE_LEVEL, Texture.RGBA, imageComponent2D.getWidth(),
                    imageComponent2D.getHeight());
            texture2D.setImage(0, imageComponent2D);
            appearance.setTexture(texture2D);

            setAppearance(appearance);
        }
    }
}
