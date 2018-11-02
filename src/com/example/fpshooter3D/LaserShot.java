package com.example.fpshooter3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

public class LaserShot {

    private static final double MAX_RANGE = 6.0;
    private static final double STEP = 1.0;
    private static final Vector3d INCR_VEC = new Vector3d(0, 0, -STEP);
    private static final long SLEEP_TIME = 100;
    private static final double HIT_RANGE = 1.0;

    private TransformGroup steerTG;

    private TransformGroup beamTG;
    private Switch beamSW;
    private Cylinder beam;
    private ImageCsSeries explShape;

    private boolean inUse;
    private Vector3d targetVector3d;

    private Transform3D tempTransform3D = new Transform3D();
    private Transform3D toMoveTransform3D = new Transform3D();
    private Transform3D localTransform3D = new Transform3D();
    private Vector3d currVector3d = new Vector3d();

    public LaserShot(TransformGroup steerTG, ImageComponent2D[] exploIms, Vector3d targetVector3d) {
        this.steerTG = steerTG;
        makeBeam(exploIms);
        this.targetVector3d = targetVector3d;
        inUse = false;
    }

    private void makeBeam(ImageComponent2D[] exploIms) {
        Transform3D beamTransform3D = new Transform3D();
        beamTransform3D.rotX(Math.PI / 2);
        beamTransform3D.setTranslation(new Vector3d(0, -0.3, -0.25));
        TransformGroup beamDir = new TransformGroup();
        beamDir.setTransform(beamTransform3D);

        beam = new Cylinder(0.05f, 0.5f, makeRedApp());
        beam.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);

        beamDir.addChild(beam);

        beamSW = new Switch();
        beamSW.setCapability(Switch.ALLOW_SWITCH_WRITE);

        beamSW.addChild(beamDir);
        beamSW.setWhichChild(Switch.CHILD_NONE);

        explShape = new ImageCsSeries(new Point3f(), 2.0f, exploIms);
        beamSW.addChild(explShape);
        beamSW.setWhichChild(Switch.CHILD_NONE);

        beamTG = new TransformGroup();
        beamTG.addChild(beamSW);
        beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    }

    private Appearance makeRedApp() {
        Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

        Material redMaterial = new Material(medRed, black, medRed, specular, 80.0f);
        redMaterial.setLightingEnable(true);

        Appearance redAppearance = new Appearance();
        redAppearance.setMaterial(redMaterial);
        return redAppearance;
    }

    public TransformGroup getBeamTG() {
        return beamTG;
    }

    public boolean requestFiring() {
        if (inUse) {
            return false;
        } else {
            inUse = true;
            new AnimBeam(this).start();
            return true;
        }
    }

    public void moveBeam() {
        steerTG.getTransform(tempTransform3D);
        beamTG.setTransform(tempTransform3D);
        showBeam(true);

        double currDist = 0.0;
        boolean hitTarget = closeToTarget();
        while ((currDist < MAX_RANGE) && (!hitTarget)) {
            doMove(INCR_VEC);
            hitTarget = closeToTarget();
            currDist += STEP;
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {

            }
        }
        showBeam(false);
        if (hitTarget) {
            showExplosion();
        }
        inUse = false;
    }

    private void doMove(Vector3d moveVector3d) {
        beamTG.getTransform(tempTransform3D);
        toMoveTransform3D.setTranslation(moveVector3d);
        tempTransform3D.mul(toMoveTransform3D);
        beamTG.setTransform(tempTransform3D);
    }

    private boolean closeToTarget() {
        beam.getLocalToVworld(localTransform3D);
        localTransform3D.get(currVector3d);

        currVector3d.sub(targetVector3d);
        double sqLen = currVector3d.lengthSquared();
        if (sqLen < HIT_RANGE * HIT_RANGE) {
            return true;
        }
        return false;
    }

    private void showBeam(boolean toVisible) {
        if (toVisible) {
            beamSW.setWhichChild(0);
        } else {
            beamSW.setWhichChild(Switch.CHILD_NONE);
        }
    }

    private void showExplosion() {
        beamSW.setWhichChild(1);
        explShape.showSeries();
        beamSW.setWhichChild(Switch.CHILD_NONE);
    }

    private void printTG(TransformGroup transformGroup, String name) {
        Transform3D transform3D = new Transform3D();
        transformGroup.getTransform(transform3D);
        Vector3d vector3d = new Vector3d();
        transform3D.get(vector3d);

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        System.out.println(name + " Vector : (" + decimalFormat.format(vector3d.x) + ", " +
                decimalFormat.format(vector3d.y) + ", " + decimalFormat.format(vector3d.z) + ")");
    }
}
