package com.example.tour3D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Enumeration;

public class TouristControls extends Behavior {

    private WakeupCondition keyPress;

    private static final int forwardKey = KeyEvent.VK_DOWN;
    private static final int backKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;

    private static final int inKey = KeyEvent.VK_I;
    private static final int outKey = KeyEvent.VK_O;

    private static final double HEIGHT = 2.0;
    private static final double ZOFFSET = 8.0;
    private static final double ZSTEP = 1.0;

    private TourSprite bob;

    private TransformGroup viewerTG;
    private Transform3D transform3D, toMove;
    private Point3d bobPosn;

    private DecimalFormat decimalFormat;

    public TouristControls(TourSprite b, TransformGroup vTG) {
        decimalFormat = new DecimalFormat("0.###");
        bob = b;
        viewerTG = vTG;
        transform3D = new Transform3D();
        toMove = new Transform3D();
        setViewer();
        keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    }

    private void setViewer() {
        bobPosn = bob.getCurrLoc();
        viewerTG.getTransform(transform3D);
        transform3D.lookAt(new Point3d(bobPosn.x, HEIGHT, bobPosn.z + ZOFFSET),
                new Point3d(bobPosn.x, HEIGHT, bobPosn.z), new Vector3d(0, 1, 0));
        transform3D.invert();
        viewerTG.setTransform(transform3D);
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

        viewerMove();
    }

    private void standardMove(int keyCode) {
        if (keyCode == forwardKey) {
            bob.moveForward();
        } else if (keyCode == backKey) {
            bob.moveBackward();
        } else if (keyCode == leftKey) {
            bob.rotClock();
        } else if (keyCode == rightKey) {
            bob.rotCounterClock();
        } else if (keyCode == inKey) {
            shiftViewer(-ZSTEP);
        } else if (keyCode == outKey) {
            shiftViewer(ZSTEP);
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == leftKey) {
            bob.moveLeft();
        } else if (keyCode == rightKey) {
            bob.moveRight();
        }
    }

    private void shiftViewer(double zDist) {
        Vector3d trans = new Vector3d(0, 0, zDist);
        viewerTG.getTransform(transform3D);
        toMove.setTranslation(trans);
        transform3D.mul(toMove);
        viewerTG.setTransform(transform3D);
    }

    private void viewerMove() {
        Point3d newLoc = bob.getCurrLoc();
        Vector3d trans = new Vector3d(newLoc.x - bobPosn.x, 0, newLoc.z - bobPosn.z);
        viewerTG.getTransform(transform3D);
        toMove.setTranslation(trans);
        transform3D.mul(toMove);
        viewerTG.setTransform(transform3D);
        bobPosn = newLoc;
    }

    private void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x: " + decimalFormat.format(tuple3d.x) +
                ", " + id + " y: " + decimalFormat.format(tuple3d.y) + ", " + id + " z: " + decimalFormat.format(tuple3d.z));
    }
}
