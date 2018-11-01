package com.example.shooter3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

public class ImagesSeries extends Shape3D {

    private static final int DELAY = 100;
    private static final int NUM_VERTS = 4;

    private ImageComponent2D imageComponent2Ds[];
    private Texture2D texture2D;

    public ImagesSeries(float screenSize, String fnm, int num) {
        loadImages(fnm, num);
        createGeometry(screenSize);
        createAppearance();
    }

    private void loadImages(String fnm, int num) {
        String filename;
        TextureLoader loader;

        imageComponent2Ds = new ImageComponent2D[num];

        System.out.println("Loading " + num + " GIFS called " + fnm);
        for (int i = 0; i < num; i++) {
            filename = new String(fnm + i + ".gif");
            loader = new TextureLoader(filename, null);
            imageComponent2Ds[i] = loader.getImage();
            if (imageComponent2Ds[i] == null) {
                System.out.println("Image Loading Failed for " + filename);
            }
        }
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

    private void createAppearance() {
        Appearance appearance = new Appearance();

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        appearance.setTransparencyAttributes(transparencyAttributes);

        texture2D = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGBA, imageComponent2Ds[0].getWidth(),
                imageComponent2Ds[0].getHeight());
        texture2D.setImage(0, imageComponent2Ds[0]);
        texture2D.setCapability(Texture.ALLOW_IMAGE_WRITE);
        appearance.setTexture(texture2D);

        setAppearance(appearance);
    }

    public void showSeries() {
        for (int i = 0; i < imageComponent2Ds.length; i++) {
            texture2D.setImage(0, imageComponent2Ds[i]);
            try {
                Thread.sleep(DELAY);
            } catch (Exception e) {

            }
        }
    }
}
