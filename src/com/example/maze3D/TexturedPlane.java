package com.example.maze3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class TexturedPlane extends Shape3D {

    private QuadArray plane;
    private int numPoints;

    public TexturedPlane(ArrayList coords, String fnm, Vector3f normalVector3f) {
        numPoints = coords.size();
        plane = new QuadArray(numPoints, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 |
                GeometryArray.NORMALS);
        createGeometry(coords, normalVector3f);
        createAppearance(fnm);
    }

    private void createGeometry(ArrayList coords, Vector3f normalVector3f) {
        Point3f[] point3fs = new Point3f[numPoints];
        coords.toArray(point3fs);
        plane.setCoordinates(0, point3fs);

        TexCoord2f[] texCoord2fs = new TexCoord2f[numPoints];
        for (int i = 0; i < numPoints; i = i + 4) {
            texCoord2fs[i] = new TexCoord2f(0.0f, 0.0f);
            texCoord2fs[i + 1] = new TexCoord2f(1.0f, 0.0f);
            texCoord2fs[i + 2] = new TexCoord2f(1.0f, 1.0f);
            texCoord2fs[i + 3] = new TexCoord2f(0.0f, 1.0f);
        }
        plane.setTextureCoordinates(0, 0, texCoord2fs);

        for (int i = 0; i < numPoints; i++) {
            plane.setNormal(i, normalVector3f);
        }
        setGeometry(plane);
    }

    private void createAppearance(String fnm) {
        Appearance appearance = new Appearance();

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        System.out.println("Loading texture for plane from " + fnm);
        TextureLoader textureLoader = new TextureLoader(fnm, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();
        appearance.setTexture(texture2D);

        Material material = new Material();
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        setAppearance(appearance);
    }
}
