package com.example.mover3D;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class LocationBeh extends Behavior {

    private static final int FWD = 0;
    private static final int BACK = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int UP = 4;
    private static final int DOWN = 5;

    private static final int CLOCK = 0;
    private static final int CCLOCK = 1;

    private static final int forwardKey = KeyEvent.VK_DOWN;
    private static final int backKey = KeyEvent.VK_UP;
    private static final int leftKey = KeyEvent.VK_LEFT;
    private static final int rightKey = KeyEvent.VK_RIGHT;

    private Figure figure;
    private WakeupCondition keyPress;

    public LocationBeh(Figure fig) {
        figure = fig;
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
            figure.doMove(FWD);
        } else if (keyCode == backKey) {
            figure.doMove(BACK);
        } else if (keyCode == leftKey) {
            figure.doRotateY(CLOCK);
        } else if (keyCode == rightKey) {
            figure.doRotateY(CCLOCK);
        }
    }

    private void altMove(int keyCode) {
        if (keyCode == backKey) {
            figure.doMove(UP);
        } else if (keyCode == forwardKey) {
            figure.doMove(DOWN);
        } else if (keyCode == leftKey) {
            figure.doMove(LEFT);
        } else if (keyCode == rightKey) {
            figure.doMove(RIGHT);
        }
    }
}
