package com.example.mover3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import java.util.HashMap;

public class Limb {

    private static final double OVERLAP = 0.1;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private int limbNo;
    private String startJoint, endJoint;
    private int orientAxis;
    private double orientAngle = 0;
    private double limbLen;
    private boolean visibleLimb = false;

    protected TransformGroup xAxisTG, yAxisTG, zAxisTG;
    protected double xsIn[], ysIn[];
    protected String texPath;

    public Limb(int lNo, String jn0, String jn1, int axis, double angle, double[] xs, double[] ys, String tex) {
        this(lNo, jn0, jn1, axis, angle, 0);

        visibleLimb = true;
        xsIn = xs;
        ysIn = ys;
        texPath = tex;

        limbLen = ysIn[ysIn.length - 1];
    }

    public Limb(int lNo, String jn0, String jn1, int axis, double angle, double len) {
        limbNo = lNo;
        startJoint = jn0;
        endJoint = jn1;
        orientAxis = axis;
        orientAngle = angle;
        limbLen = len;
        visibleLimb = false;
    }

    public void printLimb() {
        System.out.println(limbNo + " = < ( " + startJoint + ", " + endJoint + ") " + orientAngle + " " + limbLen + " "
                + texPath + " " + visibleLimb + ">");
    }

    public void growLimb(HashMap joints) {
        TransformGroup startLimbTG = (TransformGroup) joints.get(startJoint);
        if (startLimbTG == null) {
            System.out.println("No TG info for " + startJoint);
        } else {
            setOrientation(startLimbTG);
            makeLimb(joints);
        }
    }

    private void setOrientation(TransformGroup tg) {
        TransformGroup orientTG = new TransformGroup();

        if (orientAngle != 0) {
            Transform3D trans = new Transform3D();
            if (orientAxis == X_AXIS) {
                trans.rotX(Math.toRadians(orientAngle));
            } else if (orientAxis == Y_AXIS) {
                trans.rotY(Math.toRadians(orientAngle));
            } else {
                trans.rotZ(Math.toRadians(orientAngle));
            }
            orientTG.setTransform(trans);
        }

        xAxisTG = new TransformGroup();
        xAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        xAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        yAxisTG = new TransformGroup();
        yAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        yAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        zAxisTG = new TransformGroup();
        zAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        zAxisTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        tg.addChild(orientTG);
        orientTG.addChild(xAxisTG);
        xAxisTG.addChild(yAxisTG);
        yAxisTG.addChild(zAxisTG);
    }

    private void makeLimb(HashMap joints) {
        if (visibleLimb) {
            makeShape();
        }

        TransformGroup endLimbTG = new TransformGroup();
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(0.0, limbLen * (1.0 - OVERLAP), 0.0));
        endLimbTG.setTransform(trans);
        zAxisTG.addChild(endLimbTG);

        joints.put(endJoint, endLimbTG);
    }

    protected void makeShape() {
        LatheShape3D latheShape3D;
        if (texPath != null) {
            TextureLoader textureLoader = new TextureLoader("textures/" + texPath, null);
            Texture texture = textureLoader.getTexture();
            latheShape3D = new LatheShape3D(xsIn, ysIn, texture);
        } else {
            latheShape3D = new LatheShape3D(xsIn, ysIn, null);
        }
        zAxisTG.addChild(latheShape3D);
    }

    public void updateLimb(int axis, double angleStep) {
    }

    public void reset() {
    }
}
