package com.example.maze3D;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class KeyBehavior extends ViewPlatformBehavior {

    private static final int FORWARD = 0;
    private static final int LEFT = 1;
    private static final int BACK = 2;
    private static final int RIGHT = 3;

    private static final double MOVE_AMT = 1.0;
    private static final double ROT_AMT = Math.PI / 2.0;

    private static final Vector3d VFWD = new Vector3d(0, 0, -MOVE_AMT);
    private static final Vector3d VBACK = new Vector3d(0, 0, MOVE_AMT);
    private static final Vector3d VLEFT = new Vector3d(-MOVE_AMT, 0, 0);
    private static final Vector3d VRIGHT = new Vector3d(MOVE_AMT, 0, 0);
    private static final Vector3d VDOWN = new Vector3d(0, -MOVE_AMT, 0);
    private static final Vector3d VUP = new Vector3d(0, MOVE_AMT, 0);

    private int forwardKey = KeyEvent.VK_UP;
    private int backKey = KeyEvent.VK_DOWN;
    private int leftKey = KeyEvent.VK_LEFT;
    private int rightKey = KeyEvent.VK_RIGHT;

    private WakeupCondition keyPress;

    private MazeManager mazeManager;
    private BirdsEye birdsEye;
    private int zOffset;

    private TransformGroup camera2TG;
    private Transform3D transform3D = new Transform3D();
    private Transform3D toMoveTransform3D = new Transform3D();
    private Transform3D toRotTransform3D = new Transform3D();
    private Vector3d transVector3d = new Vector3d();

    public KeyBehavior(MazeManager mazeManager, BirdsEye birdsEye, TransformGroup camera2TG) {
        keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        this.mazeManager = mazeManager;
        this.birdsEye = birdsEye;
        this.camera2TG = camera2TG;
        zOffset = 0;
    }

    @Override
    public void initialize() {
        wakeupOn(keyPress);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        WakeupCriterion wakeupCriterion;
        AWTEvent[] events;

        while (enumeration.hasMoreElements()) {
            wakeupCriterion = (WakeupCriterion) enumeration.nextElement();
            if (wakeupCriterion instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent) wakeupCriterion).getAWTEvent();
                for (int i = 0; i < events.length; i++) {
                    if (events[i].getID() == KeyEvent.KEY_PRESSED) {
                        processKeyEvent((KeyEvent) events[i]);
                    }
                }
            }
        }
        wakeupOn(keyPress);
    }

    private void processKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.isAltDown()) {
            altMove(keyCode);
        } else {
            standardMove(keyCode);
        }
    }

    private void standardMove(int keyCode) {
        if (keyCode == forwardKey) {
            moveBy(VFWD, FORWARD, VBACK);
        } else if (keyCode == backKey) {
            moveBy(VBACK, BACK, VFWD);
        } else if (keyCode == leftKey) {
            doRotateY(ROT_AMT, LEFT);
        } else if (keyCode == rightKey) {
            doRotateY(-ROT_AMT, RIGHT);
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == backKey) {
            if (zOffset > 0) {
                doMove(VDOWN);
                doMoveC2(VDOWN);
                zOffset--;
            }
        } else if (keyCode == forwardKey) {
            doMove(VUP);
            doMoveC2(VUP);
            zOffset++;
        } else if (keyCode == leftKey) {
            moveBy(VLEFT, LEFT, VRIGHT);
        } else if (keyCode == rightKey) {
            moveBy(VRIGHT, RIGHT, VLEFT);
        }
    }

    private void moveBy(Vector3d theMoveVector3d, int dir, Vector3d theMoveC2Vector3d) {
        Point3d nextLocation = possibleMove(theMoveVector3d);
        if (mazeManager.canMoveTo(nextLocation.x, nextLocation.z)) {
            targetTG.setTransform(transform3D);
            doMoveC2(theMoveC2Vector3d);
            birdsEye.setMove(dir);
        } else {
            birdsEye.bangAlert();
        }
    }

    private Point3d possibleMove(Vector3d theMoveVector3d) {
        targetTG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveVector3d);
        transform3D.mul(toMoveTransform3D);
        transform3D.get(transVector3d);
        return new Point3d(transVector3d.x, transVector3d.y, transVector3d.z);
    }

    private void doMove(Vector3d theMoveVector3d) {
        targetTG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveVector3d);
        transform3D.mul(toMoveTransform3D);
        targetTG.setTransform(transform3D);
    }

    private void doMoveC2(Vector3d theMoveC2Vector3d) {
        camera2TG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveC2Vector3d);
        transform3D.mul(toMoveTransform3D);
        camera2TG.setTransform(transform3D);
    }

    private void doRotateY(double radians, int dir) {
        targetTG.getTransform(transform3D);
        toRotTransform3D.rotY(radians);
        transform3D.mul(toRotTransform3D);
        targetTG.setTransform(transform3D);

        camera2TG.getTransform(transform3D);
        transform3D.mul(toRotTransform3D);
        camera2TG.setTransform(transform3D);

        birdsEye.setRotation(dir);

    }
}
