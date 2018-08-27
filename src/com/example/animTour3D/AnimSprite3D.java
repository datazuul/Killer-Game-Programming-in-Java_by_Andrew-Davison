package com.example.animTour3D;

import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.util.BitSet;

public class AnimSprite3D {

    private static final double FLOOR_LEN = 20.0;

    private static final String poses[] = {
            "stand", "walk1", "walk2", "rev1", "rev2", "rotClock", "rotCC", "mleft", "mright", "punch1", "punch2"
    };
    private static final int STAND_NUM = 0;

    private TransformGroup objectTG;
    private Transform3D transform3D, toMove, toRot;

    private Switch imSwitch;
    private BitSet visIms;

    private int currPoseNo, maxPoses;
    private boolean isActive;
    private DecimalFormat decimalFormat;

    public AnimSprite3D() {
        decimalFormat = new DecimalFormat("0.###");
        loadPoses();

        objectTG = new TransformGroup();
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objectTG.addChild(imSwitch);

        transform3D = new Transform3D();
        toMove = new Transform3D();
        toRot = new Transform3D();
        isActive = true;
    }

    private void loadPoses() {
        PropManager propManager;

        imSwitch = new Switch(Switch.CHILD_MASK);
        imSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

        maxPoses = poses.length;
        for (int i = 0; i < maxPoses; i++) {
            propManager = new PropManager(poses[i] + ".3ds", true);
            imSwitch.addChild(propManager.getMoveTG());
        }

        visIms = new BitSet(maxPoses);
        currPoseNo = STAND_NUM;
        setPoseNum(currPoseNo);
    }

    public TransformGroup getObjectTG() {
        return objectTG;
    }

    public void setPosition(double xPos, double zPos) {
        Point3d currLoc = getCurrLoc();
        double xMove = xPos - currLoc.x;
        double zMove = zPos - currLoc.z;
        moveBy(xMove, zMove);
    }

    public boolean moveBy(double x, double z) {
        if (isActive()) {
            Point3d nextLoc = tryMove(new Vector3d(x, 0, z));
            if (beyondEdge(nextLoc.x) || beyondEdge(nextLoc.z)) {
                return false;
            } else {
                doMove(new Vector3d(x, 0, z));
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean beyondEdge(double pos) {
        if ((pos < -FLOOR_LEN / 2) || (pos > FLOOR_LEN / 2)) {
            return true;
        }
        return false;
    }

    private void doMove(Vector3d theMove) {
        objectTG.getTransform(transform3D);
        toMove.setTranslation(theMove);
        transform3D.mul(toMove);
        objectTG.setTransform(transform3D);
    }

    private Point3d tryMove(Vector3d theMove) {
        objectTG.getTransform(transform3D);
        toMove.setTranslation(theMove);
        transform3D.mul(toMove);
        Vector3d trans = new Vector3d();
        transform3D.get(trans);
        return new Point3d(trans.x, trans.y, trans.z);
    }

    public void doRotateY(double radians) {
        if (isActive()) {
            objectTG.getTransform(transform3D);
            toRot.rotY(radians);
            transform3D.mul(toRot);
            objectTG.setTransform(transform3D);
        }
    }

    public Point3d getCurrLoc() {
        objectTG.getTransform(transform3D);
        Vector3d trans = new Vector3d();
        transform3D.get(trans);
        return new Point3d(trans.x, trans.y, trans.z);
    }

    public boolean setPose(String name) {
        if (isActive()) {
            int idx = getPoseIndex(name);
            if ((idx < 0) || (idx > maxPoses - 1)) {
                return false;
            }
            setPoseNum(idx);
            return true;
        } else {
            return false;
        }
    }

    private int getPoseIndex(String name) {
        for (int i = 0; i < maxPoses; i++) {
            if (name.equals(poses[i])) {
                return i;
            }
        }
        return -1;
    }

    private void setPoseNum(int idx) {
        visIms.clear();
        visIms.set(idx);
        imSwitch.setChildMask(visIms);
        currPoseNo = idx;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (!isActive) {
            visIms.clear();
            imSwitch.setChildMask(visIms);
        } else if (isActive) {
            setPoseNum(currPoseNo);
        }
    }

    protected void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x: " + decimalFormat.format(tuple3d.x) + ", " + id + " y: " + decimalFormat.format(tuple3d.y) + ", " + id + " z: " + decimalFormat.format(tuple3d.z));
    }
}
