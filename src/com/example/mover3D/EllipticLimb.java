package com.example.mover3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Texture;

public class EllipticLimb extends Limb {

    public EllipticLimb(int lNo, String jn0, String jn1, int axis, double angle, double[] xs, double[] ys, String tex) {
        super(lNo, jn0, jn1, axis, angle, xs, ys, tex);
    }

    @Override
    protected void makeShape() {
        EllipseShape3D ellipseShape3D;
        if (texPath != null) {
            TextureLoader textureLoader = new TextureLoader("textures/" + texPath, null);
            Texture texture = textureLoader.getTexture();
            ellipseShape3D = new EllipseShape3D(xsIn, ysIn, texture);
        } else {
            ellipseShape3D = new EllipseShape3D(xsIn, ysIn, null);
        }
        zAxisTG.addChild(ellipseShape3D);
    }
}
