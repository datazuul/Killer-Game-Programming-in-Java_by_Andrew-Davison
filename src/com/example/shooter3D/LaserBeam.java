package com.example.shooter3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class LaserBeam {

    private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);
    private static final double STEP_SIZE = 1.0;
    private static final long SLEEP_TIME = 100;

    private BranchGroup beamBG;
    private TransformGroup beamTG;
    private PointSound beamPS;
    private Vector3d startVec, currVec, stepVec;
    private Point3d startPt;

    private Transform3D beamTransform3D = new Transform3D();
    private Vector3d currTrans = new Vector3d();
    private Transform3D rotTransform3D = new Transform3D();

    public LaserBeam(Vector3d startVector3d, PointSound pointSound) {
        startVec = startVector3d;
        startPt = new Point3d(startVector3d.x, startVector3d.y, startVector3d.z);
        beamPS = pointSound;

        currVec = new Vector3d();
        stepVec = new Vector3d();

        makeBeam();
    }

    private void makeBeam() {
        beamTG = new TransformGroup();
        beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        beamTransform3D.set(startVec);
        beamTG.setTransform(beamTransform3D);

        Appearance redAppearance = new Appearance();
        ColoringAttributes redColoringAttributes = new ColoringAttributes();
        Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
        redColoringAttributes.setColor(medRed);
        redAppearance.setColoringAttributes(redColoringAttributes);

        Cylinder beam = new Cylinder(0.1f, 0.5f, redAppearance);
        beam.setPickable(false);

        beamTG.addChild(beam);
        beamTG.addChild(beamPS);

        beamBG = new BranchGroup();
        beamBG.addChild(beamTG);
    }

    public BranchGroup getBeamBG() {
        return beamBG;
    }

    public void shootBeam(Point3d intercept) {
        double travelDist = startPt.distance(intercept);
        calcStepVec(intercept, travelDist);

        beamPS.setEnable(true);

        double currDist = 0.0;
        currVec.set(startVec);
        beamTG.getTransform(beamTransform3D);

        while (currDist <= travelDist) {
            beamTransform3D.setTranslation(currVec);
            beamTG.setTransform(beamTransform3D);
            currVec.add(stepVec);
            currDist += STEP_SIZE;
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {

            }
        }

        beamTransform3D.setTranslation(startVec);
        beamTG.setTransform(beamTransform3D);

        beamPS.setEnable(false);
    }

    private void calcStepVec(Point3d intercept, double travelDist) {
        double moveFrac = STEP_SIZE / travelDist;
        double incrX = (intercept.x - startPt.x) * moveFrac;
        double incrY = (intercept.y - startPt.y) * moveFrac;
        double incrZ = (intercept.z - startPt.z) * moveFrac;
        stepVec.set(incrX, incrY, incrZ);
    }

    public void makeRotation(AxisAngle4d rotAxisAngle4d) {
        beamTG.getTransform(beamTransform3D);
        beamTransform3D.get(currTrans);
        beamTransform3D.setTranslation(ORIGIN);

        rotTransform3D.setRotation(rotAxisAngle4d);
        beamTransform3D.mul(rotTransform3D);

        beamTransform3D.setTranslation(currTrans);
        beamTG.setTransform(beamTransform3D);
    }
}
