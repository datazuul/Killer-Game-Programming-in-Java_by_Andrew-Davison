package com.example.mover3D;

import javax.media.j3d.Texture;
import javax.vecmath.Color3f;

public class EllipseShape3D extends LatheShape3D {

    public EllipseShape3D(double[] xsIn, double[] ysIn, Texture texture) {
        super(xsIn, ysIn, texture);
    }

    public EllipseShape3D(double[] xsIn, double[] ysIn, Color3f darkCol, Color3f lightCol) {
        super(xsIn, ysIn, darkCol, lightCol);
    }

    @Override
    protected double zCoord(double radius, double angle) {
        return 0.5 * radius * Math.sin(angle);
    }
}
