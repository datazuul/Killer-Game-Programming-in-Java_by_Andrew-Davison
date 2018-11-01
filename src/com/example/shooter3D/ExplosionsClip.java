package com.example.shooter3D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ExplosionsClip {

    private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);

    private BranchGroup explBG;
    private Switch explSwitch;
    private TransformGroup explTG;
    private ImagesSeries explShape;
    private PointSound explPS;

    private Vector3d startVec, endVec;

    private Transform3D explTransform3D = new Transform3D();
    private Transform3D rotTransform3D = new Transform3D();

    public ExplosionsClip(Vector3d svec, PointSound sound) {
        startVec = svec;
        explPS = sound;
        endVec = new Vector3d(0, 0, 0);

        explTG = new TransformGroup();
        explTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        explTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        explTransform3D.set(startVec);
        explTG.setTransform(explTransform3D);

        explShape = new ImagesSeries(2.0f, "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/shooter3D/images/explo", 6);
        explShape.setPickable(false);
        explTG.addChild(explShape);

        explSwitch = new Switch();
        explSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        explSwitch.addChild(explTG);
        explSwitch.setWhichChild(Switch.CHILD_NONE);

        explBG = new BranchGroup();
        explBG.addChild(explSwitch);
        explBG.addChild(explPS);
    }

    public BranchGroup getExplBG() {
        return explBG;
    }

    public void showExplosion(double turnAngle, Point3d intercept) {
        endVec.set(intercept.x, intercept.y, intercept.z);
        rotateMove(turnAngle, endVec);

        explSwitch.setWhichChild(Switch.CHILD_ALL);
        explPS.setPosition((float) intercept.x, (float) intercept.y, (float) intercept.z);
        explPS.setEnable(true);
        explShape.showSeries();
        explPS.setEnable(false);
        explSwitch.setWhichChild(Switch.CHILD_NONE);

        rotateMove(-turnAngle, startVec);
    }

    private void rotateMove(double turn, Vector3d vector3d) {
        explTG.getTransform(explTransform3D);
        explTransform3D.setTranslation(ORIGIN);

        rotTransform3D.rotY(turn);
        explTransform3D.mul(rotTransform3D);

        explTransform3D.setTranslation(vector3d);
        explTG.setTransform(explTransform3D);
    }
}
