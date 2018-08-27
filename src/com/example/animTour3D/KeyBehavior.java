package com.example.animTour3D;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class KeyBehavior extends Behavior {

    private WakeupCondition keyPress;

    private static final int forwardKey = KeyEvent.VK_DOWN;
    private static final int backKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;

    private static final int activeKey = KeyEvent.VK_A;
    private static final int punchKey = KeyEvent.VK_P;

    private static final int inKey = KeyEvent.VK_I;
    private static final int outKey = KeyEvent.VK_O;

    private Animator animBeh;

    public KeyBehavior(Animator ab) {
        animBeh = ab;
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
            animBeh.moveForward();
        } else if (keyCode == backKey) {
            animBeh.moveBackward();
        } else if (keyCode == leftKey) {
            animBeh.rotClock();
        } else if (keyCode == rightKey) {
            animBeh.rotCounterClock();
        } else if (keyCode == activeKey) {
            animBeh.toggleActive();
        } else if (keyCode == punchKey) {
            animBeh.punch();
        } else if (keyCode == inKey) {
            animBeh.shiftInViewer();
        } else if (keyCode == outKey) {
            animBeh.shiftOutViewer();
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == leftKey) {
            animBeh.moveLeft();
        } else if (keyCode == rightKey) {
            animBeh.moveRight();
        }
    }
}
