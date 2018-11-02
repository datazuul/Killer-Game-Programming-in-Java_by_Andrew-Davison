package com.example.fpshooter3D;

import com.sun.j3d.utils.behaviors.vp.ViewPlatformBehavior;

import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class KeyBehavior extends ViewPlatformBehavior {

    private static final double ROT_AMT = Math.PI / 36.0;
    private static final double MOVE_STEP = 0.2;

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
    private int fireKey = KeyEvent.VK_F;

    private WakeupCondition keyPress;
    private AmmoManager ammoManager;

    private Transform3D transform3D = new Transform3D();
    private Transform3D toMoveTransform3D = new Transform3D();
    private Transform3D toRotTransform3D = new Transform3D();

    public KeyBehavior(AmmoManager ammoManager) {
        this.ammoManager = ammoManager;
        keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
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
            doMove(FWD);
        } else if (keyCode == backKey) {
            doMove(BACK);
        } else if (keyCode == leftKey) {
            rotateY(ROT_AMT);
        } else if (keyCode == rightKey) {
            rotateY(-ROT_AMT);
        } else if (keyCode == fireKey) {
            ammoManager.fireBeam();
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == forwardKey) {
            doMove(UP);
        } else if (keyCode == backKey) {
            doMove(DOWN);
        } else if (keyCode == leftKey) {
            doMove(LEFT);
        } else if (keyCode == rightKey) {
            doMove(RIGHT);
        }
    }

    private void rotateY(double radians) {
        targetTG.getTransform(transform3D);
        toRotTransform3D.rotY(radians);
        transform3D.mul(toRotTransform3D);
        targetTG.setTransform(transform3D);
    }

    private void doMove(Vector3d theMoveVector3d) {
        targetTG.getTransform(transform3D);
        toMoveTransform3D.setTranslation(theMoveVector3d);
        transform3D.mul(toMoveTransform3D);
        targetTG.setTransform(transform3D);
    }
}
