package com.example.nettour3D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Enumeration;

public class TouristControls extends Behavior {

    private WakeupCondition wakeupCondition;

    private static final int forwardKey = KeyEvent.VK_DOWN;
    private static final int backKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;

    private static final int inKey = KeyEvent.VK_I;
    private static final int outKey = KeyEvent.VK_O;

    private static final double HEIGHT = 2.0;
    private static final double Z_OFFSET = 8.0;
    private static final double Z_STEP = 1.0;

    private TourSprite tourSprite;

    private TransformGroup viewerTG;
    private Transform3D transform3D;
    private Transform3D toMoveT3D;
    private Point3d bobPositionP3D;

    private DecimalFormat decimalFormat;

    public TouristControls(TourSprite tourSprite, TransformGroup viewerTG) {
        decimalFormat = new DecimalFormat("0.###");
        this.tourSprite = tourSprite;
        this.viewerTG = viewerTG;
        transform3D = new Transform3D();
        toMoveT3D = new Transform3D();
        setViewer();
        wakeupCondition = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    }

    private void setViewer() {
        bobPositionP3D = tourSprite.getCurrentLocation();
        viewerTG.getTransform(transform3D);
        transform3D.lookAt(new Point3d(bobPositionP3D.x, HEIGHT, bobPositionP3D.z + Z_OFFSET), new Point3d(
                bobPositionP3D.x, HEIGHT, bobPositionP3D.z), new Vector3d(0, 1, 0));
        transform3D.invert();
        viewerTG.setTransform(transform3D);
    }

    @Override
    public void initialize() {
        wakeupOn(wakeupCondition);
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

        wakeupOn(wakeupCondition);
    }

    private void processKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.isAltDown()) {
            altMove(keyCode);
        } else {
            standardMove(keyCode);
        }
        viewerMove();
    }

    private void standardMove(int keyCode) {
        if (keyCode == forwardKey) {
            tourSprite.moveForward();
        } else if (keyCode == backKey) {
            tourSprite.moveBackward();
        } else if (keyCode == leftKey) {
            tourSprite.rotationClock();
        } else if (keyCode == rightKey) {
            tourSprite.rotationCounterClock();
        } else if (keyCode == inKey) {
            shiftViewer(-Z_STEP);
        } else if (keyCode == outKey) {
            shiftViewer(Z_STEP);
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == leftKey) {
            tourSprite.moveLeft();
        } else if (keyCode == rightKey) {
            tourSprite.moveRight();
        }
    }

    private void shiftViewer(double zDistance) {
        Vector3d vector3d = new Vector3d(0, 0, zDistance);
        viewerTG.getTransform(transform3D);
        toMoveT3D.setTranslation(vector3d);
        transform3D.mul(toMoveT3D);
        viewerTG.setTransform(transform3D);
    }

    private void viewerMove() {
        Point3d point3d = tourSprite.getCurrentLocation();
        Vector3d vector3d = new Vector3d(point3d.x - bobPositionP3D.x, 0, point3d.z - bobPositionP3D.z);
        viewerTG.getTransform(transform3D);
        toMoveT3D.setTranslation(vector3d);
        transform3D.mul(toMoveT3D);
        viewerTG.setTransform(transform3D);

        bobPositionP3D = point3d;
    }

    private void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x : " + decimalFormat.format(tuple3d.x) + ", " + id + " y : " +
                decimalFormat.format(tuple3d.y) + ", " + id + " z : " + decimalFormat.format(tuple3d.z));
    }
}
