package com.example.trees3D;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

public class ImageCsSeries extends OrientedShape3D {

    private static final int NUM_VERTS = 4;

    private ImageComponent2D[] ims;
    private int imIndex, numImages;
    private Texture2D texture2D;

    public ImageCsSeries(float zCoord, float screenSize, ImageComponent2D[] ims) {
        this.ims = ims;
        imIndex = 0;
        numImages = ims.length;

        setAlignmentMode(OrientedShape3D.ROTATE_ABOUT_POINT);
        setRotationPoint(0.0f, 0.0f, zCoord);

        createGeometry(screenSize);
        createAppearance();
    }

    private void createGeometry(float sz) {
        QuadArray plane = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);

        Point3f p1 = new Point3f(-sz / 2, -sz / 2, 0.0f);
        Point3f p2 = new Point3f(sz / 2, -sz / 2, 0.0f);
        Point3f p3 = new Point3f(sz / 2, sz / 2, 0.0f);
        Point3f p4 = new Point3f(-sz / 2, sz / 2, 0.0f);

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

    public void showImage(int i) {
        if (i < 0) {
            texture2D.setImage(0, ims[0]);
            imIndex = 0;
        } else if (i >= numImages) {
            texture2D.setImage(0, ims[numImages - 1]);
            imIndex = numImages - 1;
        } else {
            texture2D.setImage(0, ims[i]);
            imIndex = i;
        }
    }

    public void showNext() {
        if (imIndex < numImages - 1) {
            imIndex++;
            texture2D.setImage(0, ims[imIndex]);
        }
    }

    public void showPrev() {
        if (imIndex > 0) {
            imIndex--;
            texture2D.setImage(0, ims[imIndex]);
        }
    }
}
