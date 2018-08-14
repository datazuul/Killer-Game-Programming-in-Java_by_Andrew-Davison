package com.example.tour3D;

import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

public class Sprite3D {

    private static final double OBS_FACTOR = 0.5;

    private TransformGroup objectTG;
    private Transform3D transform3D, toMove, toRot;
    private Switch visSwitch;

    private double radius;
    private boolean isActive;

    private Obstacles obstacles;
    private DecimalFormat decimalFormat;

    public Sprite3D(String fnm, Obstacles obstacles) {
        decimalFormat = new DecimalFormat("0.###");
        this.obstacles = obstacles;

        PropManager propManager = new PropManager(fnm, true);
        radius = propManager.getScale();

        visSwitch = new Switch();
        visSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        visSwitch.addChild(propManager.getTG());
        visSwitch.setWhichChild(Switch.CHILD_ALL);

        objectTG = new TransformGroup();
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objectTG.addChild(visSwitch);

        transform3D = new Transform3D();
        toMove = new Transform3D();
        toRot = new Transform3D();
        isActive = true;
    }

    public TransformGroup getTG() {
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
            if (obstacles.nearObstacle(nextLoc, radius * OBS_FACTOR)) {
                return false;
            } else {
                doMove(new Vector3d(x, 0, z));
                return true;
            }
        } else {
            return false;
        }
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
        objectTG.getTransform(transform3D);
        toRot.rotY(radians);
        transform3D.mul(toRot);
        objectTG.setTransform(transform3D);
    }

    public Point3d getCurrLoc() {
        objectTG.getTransform(transform3D);
        Vector3d trans = new Vector3d();
        transform3D.get(trans);
        return new Point3d(trans.x, trans.y, trans.z);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        if (!isActive) {
            visSwitch.setWhichChild(Switch.CHILD_NONE);
        } else if (isActive) {
            visSwitch.setWhichChild(Switch.CHILD_ALL);
        }
    }

    protected void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x: " + decimalFormat.format(tuple3d.x) + ", " + id + " y: " + decimalFormat.format(tuple3d.y) + ", " + id + " z: " + decimalFormat.format(tuple3d.z));
    }
}
