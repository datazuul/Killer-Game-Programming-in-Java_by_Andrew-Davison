package com.example.fractalland3D;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class KeyBehavior extends ViewPlatformBehavior {

    private static final double ROT_AMT = Math.PI / 36.0;
    private static final double MOVE_STEP = 0.2;

    private static final double USER_HEIGHT = 1.0;

    private static final Vector3d FWD = new Vector3d(0, 0, -MOVE_STEP);
    private static final Vector3d BACK = new Vector3d(0, 0, MOVE_STEP);
    private static final Vector3d LEFT = new Vector3d(-MOVE_STEP, 0, 0);
    private static final Vector3d RIGHT = new Vector3d(MOVE_STEP, 0, 0);
    private static final Vector3d UP = new Vector3d(0, MOVE_STEP, 0);
    private static final Vector3d DOWN = new Vector3d(0, -MOVE_STEP, 0);

    private int forwardKey = KeyEvent.VK_UP;
    private int backKey = KeyEvent.VK_DOWN;
    private int leftKey = KeyEvent.VK_LEFT;
    private int rightKey = KeyEvent.VK_RIGHT;

    private WakeupCondition keyPress;

    private Landscape landscape;
    private double currLandHeight;
    private int zOffset;

    private Transform3D transform3D = new Transform3D();
    private Transform3D toMoveTransform3D = new Transform3D();
    private Transform3D toRotTransform3D = new Transform3D();
    private Vector3d transVector3d = new Vector3d();

    public KeyBehavior(Landscape landscape, TransformGroup steerTG) {
        this.landscape = landscape;
        zOffset = 0;
        initViewPosition(steerTG);

        keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    }

    private void initViewPosition(TransformGroup steerTG) {
        Vector3d startPosn = landscape.getOriginVector3d();

        currLandHeight = startPosn.y;
        startPosn.y += USER_HEIGHT;

        steerTG.getTransform(transform3D);
        transform3D.setTranslation(startPosn);
        steerTG.setTransform(transform3D);
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
            moveBy(FWD);
        } else if (keyCode == backKey) {
            moveBy(BACK);
        } else if (keyCode == leftKey) {
            rotateY(ROT_AMT);
        } else if (keyCode == rightKey) {
            rotateY(-ROT_AMT);
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == forwardKey) {
            doMove(UP);
            zOffset++;
        } else if (keyCode == backKey) {
            if (zOffset > 0) {
                doMove(DOWN);
                zOffset++;
            }
        } else if (keyCode == leftKey) {
            moveBy(LEFT);
        } else if (keyCode == rightKey) {
            moveBy(RIGHT);
        }
    }

    private void moveBy(Vector3d theMoveVector3d) {
        Vector3d nextLoc = tryMove(theMoveVector3d);
        if (!landscape.inLandscape(nextLoc.x, nextLoc.z)) {
            return;
        }

        double floorHeight = landscape.getLandHeight(nextLoc.x, nextLoc.z, currLandHeight);
        double heightChg = floorHeight - currLandHeight - (MOVE_STEP * zOffset);

        currLandHeight = floorHeight;
        zOffset = 0;
        Vector3d actualMove = new Vector3d(theMoveVector3d.x, heightChg, theMoveVector3d.z);
        doMove(actualMove);
    }

    private Vector3d tryMove(Vector3d theMoveVector3d) {
        targetTG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveVector3d);
        transform3D.mul(toMoveTransform3D);
        transform3D.get(transVector3d);
        return transVector3d;
    }

    private void doMove(Vector3d theMoveVector3d) {
        targetTG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveVector3d);
        transform3D.mul(toMoveTransform3D);
        targetTG.setTransform(transform3D);
    }

    private void rotateY(double radians) {
        targetTG.getTransform(transform3D);
        toRotTransform3D.rotY(radians);
        transform3D.mul(toRotTransform3D);
        targetTG.setTransform(transform3D);
    }
}
