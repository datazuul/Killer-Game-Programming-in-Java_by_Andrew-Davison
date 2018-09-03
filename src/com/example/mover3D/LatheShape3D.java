package com.example.mover3D;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.TexCoord2f;
import java.text.DecimalFormat;

public class LatheShape3D extends Shape3D {

    private static final double RADS_DEGREE = Math.PI / 180.0;
    private static final double ANGLE_INCR = 15.0;
    private static final int NUM_SLICES = (int) (360.0 / ANGLE_INCR);

    private static final Color3f pink = new Color3f(1.0f, 0.75f, 0.8f);
    private static final Color3f darkPink = new Color3f(0.25f, 0.18f, 0.2f);
    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

    private double height;

    public LatheShape3D(double xsIn[], double ysIn[], Texture texture) {
        LatheCurve latheCurve = new LatheCurve(xsIn, ysIn);
        buildShape(latheCurve.getXs(), latheCurve.getYs(), latheCurve.getHeight(), texture);
    }

    public LatheShape3D(double xsIn[], double ysIn[], Color3f darkCol, Color3f lightCol) {
        LatheCurve latheCurve = new LatheCurve(xsIn, ysIn);
        buildShape(latheCurve.getXs(), latheCurve.getYs(), latheCurve.getHeight(), darkCol, lightCol);
    }

    private void buildShape(double[] xs, double[] ys, double h) {
        height = h;
        createGeometry(xs, ys, false);
        createAppearance(darkPink, pink);
    }

    private void buildShape(double[] xs, double[] ys, double h, Color3f darkCol, Color3f lightCol) {
        height = h;
        createGeometry(xs, ys, false);

        if ((darkCol == null) || (lightCol == null)) {
            System.out.println("One of the colours is null; using defaults");
            createAppearance(darkPink, pink);
        } else {
            createAppearance(darkCol, lightCol);
        }
    }

    private void buildShape(double[] xs, double[] ys, double h, Texture texture) {
        height = h;
        if (texture == null) {
            System.out.println("The texture is null; using default colours");
            createGeometry(xs, ys, false);
            createAppearance(darkPink, pink);
        } else {
            createGeometry(xs, ys, true);
            createAppearance(texture);
        }
    }

    private void createGeometry(double[] xs, double[] ys, boolean usingTexture) {
        double verts[] = surfaceRevolve(xs, ys);

        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
        geometryInfo.setCoordinates(verts);

        if (usingTexture) {
            geometryInfo.setTextureCoordinateParams(1, 2);
            TexCoord2f[] texCoord2fs = initTexCoords(verts);
            correctTexCoords(texCoord2fs);
            geometryInfo.setTextureCoordinates(0, texCoord2fs);
        }

        NormalGenerator normalGenerator = new NormalGenerator();
        normalGenerator.generateNormals(geometryInfo);

        setGeometry(geometryInfo.getGeometryArray());
    }

    private void printVerts(double[] verts) {
        DecimalFormat decimalFormat = new DecimalFormat("0.###");

        int numPerLine = 6;
        int count = 0;
        System.out.println("No. vertices: " + verts.length + "\n");

        for (int i = 0; i < verts.length; i = i + 3) {
            if (count == numPerLine) {
                System.out.println();
                count = 0;
            }
            System.out.println("( " + decimalFormat.format(verts[i]) + ", " + decimalFormat.format(verts[i + 1]) +
                    ", " + decimalFormat.format(verts[i + 2]) + ") ");
            count += 3;
        }
        System.out.println("\n");
    }

    private TexCoord2f[] initTexCoords(double[] verts) {
        int numVerts = verts.length;
        TexCoord2f[] texCoord2fs = new TexCoord2f[numVerts / 3];

        double x, y, z;
        float sVal, tVal;
        double angle, frac;

        int idx = 0;
        for (int i = 0; i < numVerts / 3; i++) {
            x = verts[idx];
            y = verts[idx + 1];
            z = verts[idx + 2];

            angle = Math.atan2(x, z);
            frac = angle / Math.PI;
            sVal = (float) (0.5 + frac / 2);
            tVal = (float) (y / height);

            texCoord2fs[i] = new TexCoord2f(sVal, tVal);
            idx += 3;
        }
        return texCoord2fs;
    }

    private void printTexCoords(TexCoord2f[] texCoord2fs) {
        System.out.println("No. tex coords : " + texCoord2fs.length + "\n");

        for (int i = 0; i < texCoord2fs.length; i = i + 2) {
            System.out.println(texCoord2fs[i] + " " + texCoord2fs[i + 1]);
        }
        System.out.println("\n");
    }

    private void correctTexCoords(TexCoord2f[] texCoord2fs) {
        for (int i = 0; i < texCoord2fs.length; i = i + 4) {
            if ((texCoord2fs[i].x < texCoord2fs[i + 3].x) && (texCoord2fs[i + 1].x < texCoord2fs[i + 2].x)) {
                texCoord2fs[i].x = (1.0f + texCoord2fs[i + 3].x) / 2;
                texCoord2fs[i + 1].x = (1.0f + texCoord2fs[i + 2].x) / 2;
            }
        }
    }

    private void createAppearance(Color3f darkCol, Color3f lightCol) {
        Appearance appearance = new Appearance();

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polygonAttributes);

        Material material = new Material(darkCol, black, lightCol, black, 1.0f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        setAppearance(appearance);
    }

    private void createAppearance(Texture texture) {
        Appearance appearance = new Appearance();

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polygonAttributes);

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        Material material = new Material();
        material.setSpecularColor(black);
        material.setLightingEnable(true);
        appearance.setMaterial(material);
        appearance.setTexture(texture);

        setAppearance(appearance);
    }

    private double[] surfaceRevolve(double xs[], double ys[]) {
        checkCoords(xs);

        double[] coords = new double[(NUM_SLICES) * (xs.length - 1) * 4 * 3];

        int index = 0;
        for (int i = 0; i < xs.length - 1; i++) {
            for (int slice = 0; slice < NUM_SLICES; slice++) {
                addCorner(coords, xs[i], ys[i], slice, index);
                index += 3;

                addCorner(coords, xs[i + 1], ys[i + 1], slice, index);
                index += 3;

                addCorner(coords, xs[i + 1], ys[i + 1], slice + 1, index);
                index += 3;

                addCorner(coords, xs[i], ys[i], slice + 1, index);
                index += 3;
            }
        }
        return coords;
    }

    private void checkCoords(double xs[]) {
        for (int i = 0; i < xs.length; i++) {
            if (xs[i] < 0) {
                System.out.println("Warning: setting xs[ " + i + "] from -ve to 0");
                xs[i] = 0;
            }
        }
    }

    private void addCorner(double[] coords, double xOrig, double yOrig, int slice, int index) {
        double angle = RADS_DEGREE * (slice * ANGLE_INCR);

        if (slice == NUM_SLICES) {
            coords[index] = xOrig;
        } else {
            coords[index] = xCoord(xOrig, angle);
        }

        coords[index + 1] = yOrig;

        if (slice == NUM_SLICES) {
            coords[index + 2] = 0;
        } else {
            coords[index + 2] = zCoord(xOrig, angle);
        }
    }

    protected double xCoord(double radius, double angle) {
        return radius * Math.cos(angle);
    }

    protected double zCoord(double radius, double angle) {
        return radius * Math.sin(angle);
    }
}
