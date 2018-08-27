package com.example.animTour3D;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;

public class Animator extends Behavior {

    private static final double MOVERATE = 0.3;
    private static final double ROTATE_AMT = Math.PI / 16.0;

    private static final double HEIGHT = 2.0;
    private static final double ZOFFSET = 8.0;
    private static final double ZSTEP = 1.0;

    private static final String forwards[] = {"walk1", "walk2", "stand"};
    private static final String backwards[] = {"rev1", "rev2", "stand"};
    private static final String rotClock[] = {"rotClock", "stand"};
    private static final String rotCounterClock[] = {"rotCC", "stand"};
    private static final String moveLeft[] = {"mleft", "stand"};
    private static final String moveRight[] = {"mright", "stand"};
    private static final String active[] = {"toggle", "stand"};
    private static final String punch[] = {"punch1", "punch1", "punch2", "punch2", "stand"};

    private static final int MAX_SEQS = 4;

    private WakeupCondition timeDelay;

    private TransformGroup viewerTG;
    private Transform3D transform3D, toMove;

    private AnimSprite3D bob;
    private Point3d bobPosn;
    private boolean isActive;

    private ArrayList animSchedule;
    private int seqCount;

    private DecimalFormat decimalFormat;

    public Animator(int td, AnimSprite3D b, TransformGroup vTG) {
        decimalFormat = new DecimalFormat("0.###");

        timeDelay = new WakeupOnElapsedTime(td);

        animSchedule = new ArrayList();
        seqCount = 0;

        bob = b;
        bobPosn = bob.getCurrLoc();
        viewerTG = vTG;
        isActive = true;
        transform3D = new Transform3D();
        toMove = new Transform3D();
        setViewer();
    }

    private void setViewer() {
        bobPosn = bob.getCurrLoc();
        viewerTG.getTransform(transform3D);
        transform3D.lookAt(new Point3d(bobPosn.x, HEIGHT, bobPosn.z + ZOFFSET),
                new Point3d(bobPosn.x, HEIGHT, bobPosn.z),
                new Vector3d(0, 1, 0));
        transform3D.invert();
        viewerTG.setTransform(transform3D);
    }

    @Override
    public void initialize() {
        wakeupOn(timeDelay);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        String anim = getNextAnim();
        if (anim != null) {
            doAnimation(anim);
        }
        wakeupOn(timeDelay);
    }

    private void doAnimation(String anim) {
        if (anim.equals("walk1") || anim.equals("walk2")) {
            bob.moveBy(0.0, MOVERATE / 2);
        } else if (anim.equals("rev1") || anim.equals("rev2")) {
            bob.moveBy(0.0, -MOVERATE / 2);
        } else if (anim.equals("rotClock")) {
            bob.doRotateY(-ROTATE_AMT);
        } else if (anim.equals("rotCC")) {
            bob.doRotateY(ROTATE_AMT);
        } else if (anim.equals("mleft")) {
            bob.moveBy(-MOVERATE, 0.0);
        } else if (anim.equals("mright")) {
            bob.moveBy(MOVERATE, 0.0);
        } else if (anim.equals("toggle")) {
            isActive = !isActive;
            bob.setActive(isActive);
        }
        if (!anim.equals("toggle")) {
            bob.setPose(anim);
        }

        viewerMove();
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

    public void shiftInViewer() {
        shiftViewer(-ZSTEP);
    }

    public void shiftOutViewer() {
        shiftViewer(ZSTEP);
    }

    private void shiftViewer(double zDist) {
        Vector3d trans = new Vector3d(0, 0, zDist);
        viewerTG.getTransform(transform3D);
        toMove.setTranslation(trans);
        transform3D.mul(toMove);
        viewerTG.setTransform(transform3D);
    }

    synchronized private void addAnims(String ims[]) {
        if (seqCount < MAX_SEQS) {
            for (int i = 0; i < ims.length; i++) {
                animSchedule.add(ims[i]);
            }
            seqCount++;
        }
    }

    synchronized private String getNextAnim() {
        if (animSchedule.isEmpty()) {
            return null;
        } else {
            String anim = (String) animSchedule.remove(0);
            if (anim.equals("stand")) {
                seqCount--;
            }
            return anim;
        }
    }

    public void moveForward() {
        addAnims(forwards);
    }

    public void moveBackward() {
        addAnims(backwards);
    }

    public void moveLeft() {
        addAnims(moveLeft);
    }

    public void moveRight() {
        addAnims(moveRight);
    }

    public void rotClock() {
        addAnims(rotClock);
    }

    public void rotCounterClock() {
        addAnims(rotCounterClock);
    }

    public void punch() {
        addAnims(punch);
    }

    public void toggleActive() {
        addAnims(active);
    }

    private void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x: " + decimalFormat.format(tuple3d.x) + ", " + id + " y: " + decimalFormat.format(tuple3d.y) + ", " + id + " z: " + decimalFormat.format(tuple3d.z));
    }
}
