package com.example.nettour3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.text.DecimalFormat;

public class Sprite3D {

    private static final double OBSTACLES_FACTOR = 0.5;

    private BranchGroup objectBG;
    private TransformGroup objectTG;
    private Transform3D transform3D, toMoveT3D, toRotationT3D;
    private Switch visibleSwitch;

    private double radius;
    private boolean isActive;
    private double currentRotation;

    private Obstacles obstacles;
    private DecimalFormat decimalFormat;

    public Sprite3D(String userName, String fileName, Obstacles obstacles) {
        decimalFormat = new DecimalFormat("0.###");
        this.obstacles = obstacles;
        currentRotation = 0.0;

        PropManager propManager = new PropManager(fileName, true);

        Vector3d vector3d = propManager.getLocation();
        radius = propManager.getScale();

        visibleSwitch = new Switch();
        visibleSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        visibleSwitch.addChild(propManager.getMoveTG());
        visibleSwitch.setWhichChild(Switch.CHILD_ALL);

        objectTG = new TransformGroup();
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objectTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objectTG.addChild(visibleSwitch);
        objectTG.addChild(makeName(userName));

        objectBG = new BranchGroup();
        objectBG.setCapability(BranchGroup.ALLOW_DETACH);
        objectBG.addChild(objectTG);

        transform3D = new Transform3D();
        toMoveT3D = new Transform3D();
        toRotationT3D = new Transform3D();
        isActive = true;
    }

    private TransformGroup makeName(String userName) {
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f brightYellow = new Color3f(1.0f, 1.0f, 0.0f);
        Appearance appearance = new Appearance();
        Material material = new Material(brightYellow, black, brightYellow, brightYellow, 64f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        Font3D font3D = new Font3D(new Font("SansSerif", Font.PLAIN, 2), new FontExtrusion());
        Text3D text3D = new Text3D(font3D, userName);
        text3D.setAlignment(Text3D.ALIGN_CENTER);

        OrientedShape3D orientedShape3D = new OrientedShape3D();
        orientedShape3D.setGeometry(text3D);
        orientedShape3D.setAppearance(appearance);
        orientedShape3D.setAlignmentAxis(0.0f, 1.0f, 0.0f);

        TransformGroup transformGroup = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setScale(1.2 / (userName.length()));
        transform3D.setTranslation(new Vector3d(0, radius * 2, 0));
        transformGroup.setTransform(transform3D);
        transformGroup.addChild(orientedShape3D);

        return transformGroup;
    }

    public BranchGroup getObjectBG() {
        return objectBG;
    }

    public void detach() {
        objectBG.detach();
    }

    public void setPosition(double xPosition, double zPosition) {
        Point3d point3d = getCurrentLocation();
        double xMove = xPosition - point3d.x;
        double zMove = zPosition - point3d.z;
        moveBy(xMove, zMove);
    }

    public boolean moveBy(double xMove, double zMove) {
        if (isActive()) {
            Point3d point3d = tryMove(new Vector3d(xMove, 0, zMove));
            if (obstacles.nearObstacle(point3d, radius * OBSTACLES_FACTOR)) {
                return false;
            } else {
                doMove(new Vector3d(xMove, 0, zMove));
                return true;
            }
        } else {
            return false;
        }
    }

    private void doMove(Vector3d vector3d) {
        objectTG.getTransform(transform3D);
        toMoveT3D.setTranslation(vector3d);
        transform3D.mul(toMoveT3D);
        objectTG.setTransform(transform3D);
    }

    private Point3d tryMove(Vector3d vector3d) {
        objectTG.getTransform(transform3D);
        toMoveT3D.setTranslation(vector3d);
        transform3D.mul(toMoveT3D);
        Vector3d vector3d1 = new Vector3d();
        transform3D.get(vector3d1);
        return new Point3d(vector3d1.x, vector3d1.y, vector3d1.z);
    }

    public void doRotateY(double radians) {
        objectTG.getTransform(transform3D);
        toRotationT3D.rotY(radians);
        transform3D.mul(toRotationT3D);
        objectTG.setTransform(transform3D);
        currentRotation += radians;
    }

    public Point3d getCurrentLocation() {
        objectTG.getTransform(transform3D);
        Vector3d vector3d = new Vector3d();
        transform3D.get(vector3d);
        return new Point3d(vector3d.x, vector3d.y, vector3d.z);
    }

    public void setCurrentRotation(double currentRotation) {
        double rotationChange = currentRotation - this.currentRotation;
        doRotateY(rotationChange);
    }

    public double getCurrentRotation() {
        return currentRotation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (!isActive) {
            visibleSwitch.setWhichChild(Switch.CHILD_NONE);
        } else if (isActive) {
            visibleSwitch.setWhichChild(Switch.CHILD_ALL);
        }
    }

    private void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x : " + decimalFormat.format(tuple3d.x) + ", " + id + " y : " +
                decimalFormat.format(tuple3d.y) + ", " + id + " z : " + decimalFormat.format(tuple3d.z));
    }
}
