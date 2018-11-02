package com.example.fpshooter3D;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

public class ImageCsSeries extends Shape3D {

    private static final int DELAY = 100;
    private static final int NUM_VERTS = 4;

    private ImageComponent2D[] ims;
    private Texture2D texture2D;

    public ImageCsSeries(Point3f center, float screenSize, ImageComponent2D[] imageComponent2DS) {
        ims = imageComponent2DS;
        createGeometry(center, screenSize);
        createAppearance();
    }

    private void createGeometry(Point3f point3f, float sz) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        Point3f point3f1 = new Point3f(point3f.x - sz / 2, point3f.y - sz / 2, point3f.z);
        Point3f point3f2 = new Point3f(point3f.x + sz / 2, point3f.y - sz / 2, point3f.z);
        Point3f point3f3 = new Point3f(point3f.x + sz / 2, point3f.y + sz / 2, point3f.z);
        Point3f point3f4 = new Point3f(point3f.x - sz / 2, point3f.y + sz / 2, point3f.z);

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

    private void createAppearance() {
        Appearance appearance = new Appearance();

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        appearance.setTransparencyAttributes(transparencyAttributes);

        texture2D = new Texture2D(Texture2D.BASE_LEVEL, Texture.RGBA, ims[0].getWidth(), ims[0].getHeight());
        texture2D.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
        texture2D.setImage(0, ims[0]);
        texture2D.setCapability(Texture.ALLOW_IMAGE_WRITE);
        appearance.setTexture(texture2D);

        setAppearance(appearance);
    }

    public void showSeries() {
        for (int i = 0; i < ims.length; i++) {
            texture2D.setImage(0, ims[i]);
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {

            }
        }
    }
}
