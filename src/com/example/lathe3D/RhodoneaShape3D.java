package com.example.lathe3D;

import javax.media.j3d.Texture;
import javax.vecmath.Color3f;

public class RhodoneaShape3D extends LatheShape3D {

    public RhodoneaShape3D(double[] xsIn, double[] ysIn, Color3f darkCol, Color3f lightCol) {
        super(xsIn, ysIn, darkCol, lightCol);
    }

    public RhodoneaShape3D(double[] xsIn, double[] ysIn, Texture texture) {
        super(xsIn, ysIn, texture);
    }

    protected double xCoord(double radius, double angle) {
        double r = radius * Math.cos(4 * angle);
        return r * Math.cos(angle);
    }

    protected double zCoord(double radius, double angle) {
        double r = radius * Math.cos(4 * angle);
        return r * Math.sin(angle);
    }
}
