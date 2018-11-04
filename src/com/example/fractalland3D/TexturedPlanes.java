package com.example.fractalland3D;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.TexCoord2f;
import java.util.ArrayList;

public class TexturedPlanes extends Shape3D {

    public TexturedPlanes(ArrayList coords, String fnm) {
        System.out.println(fnm + "; numPoints : " + coords.size());
        createGeometry(coords);
        createAppearance(fnm);

        PickTool.setCapabilities(this, PickTool.INTERSECT_COORD);
    }

    private void createGeometry(ArrayList coords) {
        int numPoints = coords.size();
        QuadArray plane = new QuadArray(numPoints, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 |
                GeometryArray.NORMALS);

        Point3d[] point3ds = new Point3d[numPoints];
        coords.toArray(point3ds);

        TexCoord2f[] texCoord2fs = new TexCoord2f[numPoints];
        for (int i = 0; i < numPoints; i = i + 4) {
            texCoord2fs[i] = new TexCoord2f(0.0f, 0.0f);
            texCoord2fs[i + 1] = new TexCoord2f(1.0f, 0.0f);
            texCoord2fs[i + 2] = new TexCoord2f(1.0f, 1.0f);
            texCoord2fs[i + 3] = new TexCoord2f(0.0f, 1.0f);
        }

        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        geometryInfo.setCoordinates(point3ds);
        geometryInfo.setTextureCoordinateParams(1, 2);
        geometryInfo.setTextureCoordinates(0, texCoord2fs);

        NormalGenerator normalGenerator = new NormalGenerator();
        normalGenerator.setCreaseAngle((float) Math.toRadians(150));
        normalGenerator.generateNormals(geometryInfo);

        Stripifier stripifier = new Stripifier();
        stripifier.stripify(geometryInfo);

        setGeometry(geometryInfo.getGeometryArray());
    }

    private void createAppearance(String fnm) {
        Appearance appearance = new Appearance();

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        TextureLoader textureLoader = new TextureLoader(fnm, TextureLoader.GENERATE_MIPMAP, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();
        texture2D.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
        appearance.setTexture(texture2D);

        Material material = new Material();
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        setAppearance(appearance);
    }
}
