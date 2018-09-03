package com.example.mover3D;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Figure {

    private static final double MOVERATE = 0.3;
    private static final double ROTATE_AMT = Math.PI / 16.0;

    private static final int FWD = 0;
    private static final int BACK = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int UP = 4;
    private static final int DOWN = 5;

    private static final int CLOCK = 0;
    private static final int CCLOCK = 1;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private static final Vector3d fwdVec = new Vector3d(0, 0, MOVERATE);
    private static final Vector3d backVec = new Vector3d(0, 0, -MOVERATE);

    private static final Vector3d leftVec = new Vector3d(-MOVERATE, 0, 0);
    private static final Vector3d rightVec = new Vector3d(MOVERATE, 0, 0);

    private static final Vector3d upVec = new Vector3d(0, MOVERATE, 0);
    private static final Vector3d downVec = new Vector3d(0, -MOVERATE, 0);

    private ArrayList limbs;
    private HashMap limbNames;

    private TransformGroup figureTG;
    private Transform3D transform3D, toMove, toRot;
    private int yCount;

    public Figure() {
        yCount = 0;
        transform3D = new Transform3D();
        toMove = new Transform3D();
        toRot = new Transform3D();

        limbs = new ArrayList();
        limbNames = new HashMap();

        buildTorso();
        buildHead();

        buildRightArm();
        buildLeftArm();

        buildRightLeg();
        buildLeftLeg();

        printLimbsInfo();
        buildFigureGraph();
    }

    private void buildTorso() {
        double xsIn1[] = {0, -0.1, 0.22, -0.2, 0.001};
        double ysIn1[] = {0, 0.03, 0.08, 0.25, 0.25};
        EllipticLimb limb1 = new EllipticLimb(1, "j0", "j1", Z_AXIS, 0, xsIn1, ysIn1, "denim.jpg");

        double xsIn2[] = {-0.001, -0.2, 0.36, 0.001};
        double ysIn2[] = {0, 0, 0.50, 0.68};
        MoveableEllipticLimb limb2 = new MoveableEllipticLimb("chest", 2, "j1", "j2", Z_AXIS, 0,
                xsIn2, ysIn2, "camoflage.jpg");
        limb2.setRanges(0, 120, -60, 60, -40, 40);

        limbs.add(limb1);
        limbs.add(limb2);

        limbNames.put("chest", new Integer(2));
    }

    private void buildHead() {
        double xsIn3[] = {-0.001, -0.09, -0.09, 0.001};
        double ysIn3[] = {0, 0, 0.1, 0.1};
        Limb limb3 = new Limb(3, "j2", "j3", Z_AXIS, 0, xsIn3, ysIn3, "skin.jpg");

        double xsIn4[] = {-0.001, 0.09, 0.17, 0};
        double ysIn4[] = {0, 0, 0.2, 0.4};
        MoveableLimb limb4 = new MoveableLimb("head", 4, "j3", "j4", Z_AXIS, 0, xsIn4, ysIn4,
                "head.jpg");
        limb4.setRanges(-40, 40, -40, 40, -30, 30);

        limbs.add(limb3);
        limbs.add(limb4);

        limbNames.put("head", new Integer(4));
    }

    private void buildRightArm() {
        Limb limb5 = new Limb(5, "j2", "j5", Z_AXIS, 95, 0.35);

        double xsIn6[] = {0, 0.1, 0.08, 0};
        double ysIn6[] = {0, 0.08, 0.45, 0.55};
        MoveableLimb limb6 = new MoveableLimb("urArm", 6, "j5", "j6", Z_AXIS, 80, xsIn6,
                ysIn6, "rightarm.jpg");
        limb6.setRanges(-60, 180, -90, 90, -90, 30);

        double xsIn7[] = {0, 0.08, 0.055, 0};
        double ysIn7[] = {0, 0.08, 0.38, 0.43};
        MoveableLimb limb7 = new MoveableLimb("lrArm", 7, "j6", "j7", Z_AXIS, 5, xsIn7,
                ysIn7, "skin.jpg");
        limb7.setRanges(0, 150, -90, 90, -90, 90);

        double xsIn8[] = {0, 0.06, 0.04, 0};
        double ysIn8[] = {0, 0.07, 0.16, 0.2};
        MoveableEllipticLimb limb8 = new MoveableEllipticLimb("rHand", 8, "j7", "j8", Z_AXIS, 0,
                xsIn8, ysIn8, "skin.jpg");
        limb8.setRanges(-50, 50, -40, 90, -40, 40);

        limbs.add(limb5);
        limbs.add(limb6);
        limbs.add(limb7);
        limbs.add(limb8);

        limbNames.put("urArm", new Integer(6));
        limbNames.put("lrArm", new Integer(7));
        limbNames.put("rHand", new Integer(8));
    }

    private void buildLeftArm() {
        Limb limb9 = new Limb(9, "j2", "j9", Z_AXIS, -95, 0.35);

        double xsIn10[] = {0, 0.1, 0.08, 0};
        double ysIn10[] = {0, 0.08, 0.45, 0.55};
        MoveableLimb limb10 = new MoveableLimb("ulArm", 10, "j9", "j10", Z_AXIS, -80, xsIn10,
                ysIn10, "leftarm.jpg");
        limb10.setRanges(-60, 180, -90, 90, -30, 90);

        double xsIn11[] = {0, 0.08, 0.055, 0};
        double ysIn11[] = {0, 0.08, 0.38, 0.43};
        MoveableLimb limb11 = new MoveableLimb("llArm", 11, "j10", "j11", Z_AXIS, -5, xsIn11,
                ysIn11, "skin.jpg");
        limb11.setRanges(0, 150, -90, 90, -90, 90);

        double xsIn12[] = {0, 0.06, 0.04, 0};
        double ysIn12[] = {0, 0.07, 0.16, 0.2};
        MoveableEllipticLimb limb12 = new MoveableEllipticLimb("lHand", 12, "j11", "j12", Z_AXIS, 0,
                xsIn12, ysIn12, "skin.jpg");
        limb12.setRanges(-50, 50, -90, 40, -40, 40);

        limbs.add(limb9);
        limbs.add(limb10);
        limbs.add(limb11);
        limbs.add(limb12);

        limbNames.put("ulArm", new Integer(10));
        limbNames.put("llArm", new Integer(11));
        limbNames.put("lHand", new Integer(12));
    }

    private void buildRightLeg() {
        Limb limb13 = new Limb(13, "j0", "j13", Z_AXIS, 50, 0.20);

        double xsIn14[] = {0, 0.12, 0.1, 0};
        double ysIn14[] = {0, 0.1, 0.6, 0.7};
        MoveableLimb limb14 = new MoveableLimb("urLeg", 14, "j13", "j14", Z_AXIS, 130, xsIn14,
                ysIn14, "denim.jpg");
        limb14.setRanges(-45, 80, -20, 20, -45, 30);

        double xsIn15[] = {0, 0.1, 0.06, 0};
        double ysIn15[] = {0, 0.15, 0.62, 0.7};
        MoveableLimb limb15 = new MoveableLimb("lrLeg", 15, "j14", "j15", Z_AXIS, 0, xsIn15,
                ysIn15, "lowDenim.jpg");
        limb15.setRange(X_AXIS, -120, 0);

        Limb limb16 = new Limb(16, "j15", "j16", Z_AXIS, 0, 0.07);

        double xsIn17[] = {0, 0.08, 0.06, 0};
        double ysIn17[] = {0, 0.07, 0.21, 0.25};
        MoveableEllipticLimb limb17 = new MoveableEllipticLimb("rFoot", 17, "j16", "j17", X_AXIS, 90,
                xsIn17, ysIn17, "shoes.jpg");
        limb17.setRanges(-90, 0, 0, 0, -30, 30);

        limbs.add(limb13);
        limbs.add(limb14);
        limbs.add(limb15);
        limbs.add(limb16);
        limbs.add(limb17);

        limbNames.put("urLeg", new Integer(14));
        limbNames.put("lrLeg", new Integer(15));
        limbNames.put("rFoot", new Integer(17));
    }

    private void buildLeftLeg() {
        Limb limb18 = new Limb(18, "j0", "j18", Z_AXIS, -50, 0.20);

        double xsIn19[] = {0, 0.12, 0.1, 0};
        double ysIn19[] = {0, 0.1, 0.6, 0.7};
        MoveableLimb limb19 = new MoveableLimb("ulLeg", 19, "j18", "j19", Z_AXIS, -130, xsIn19,
                ysIn19, "denim.jpg");
        limb19.setRanges(-45, 80, -20, 20, -30, 45);

        double xsIn20[] = {0, 0.1, 0.06, 0};
        double ysIn20[] = {0, 0.15, 0.62, 0.7};
        MoveableLimb limb20 = new MoveableLimb("llLeg", 20, "j19", "j20", Z_AXIS, 0, xsIn20,
                ysIn20, "lowDenim.jpg");
        limb20.setRange(X_AXIS, -120, 0);

        Limb limb21 = new Limb(21, "j20", "j21", Z_AXIS, 0, 0.07);

        double xsIn22[] = {0, 0.08, 0.06, 0};
        double ysIn22[] = {0, 0.07, 0.21, 0.25};
        MoveableEllipticLimb limb22 = new MoveableEllipticLimb("lFoot", 22, "j21", "j22", X_AXIS, 90,
                xsIn22, ysIn22, "shoes.jpg");
        limb22.setRanges(-90, 0, 0, 0, -30, 30);

        limbs.add(limb18);
        limbs.add(limb19);
        limbs.add(limb20);
        limbs.add(limb21);
        limbs.add(limb22);

        limbNames.put("ulLeg", new Integer(19));
        limbNames.put("llLeg", new Integer(20));
        limbNames.put("lFoot", new Integer(22));
    }

    private void printLimbsInfo() {
        int i = 0;
        Iterator iterator = limbNames.keySet().iterator();
        while (iterator.hasNext()) {
            String lNm = (String) iterator.next();
            System.out.println("( " + lNm + " = " + limbNames.get(lNm) + ") ");
            i++;
            if (i == 5) {
                System.out.println();
                i = 0;
            }
        }
        System.out.println();
    }

    private void buildFigureGraph() {
        HashMap joints = new HashMap();

        figureTG = new TransformGroup();
        figureTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        figureTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        TransformGroup offsetTG = new TransformGroup();
        Transform3D trans = new Transform3D();
        trans.setTranslation(new Vector3d(0, 1.24, 0));

        offsetTG.setTransform(trans);

        joints.put("j0", offsetTG);

        Limb limb;
        for (int i = 0; i < limbs.size(); i++) {
            limb = (Limb) limbs.get(i);
            limb.growLimb(joints);
        }

        figureTG.addChild(offsetTG);
    }

    public TransformGroup getFigureTG() {
        return figureTG;
    }

    public int checkLimbNo(int no) {
        if (limbNames.containsValue(new Integer(no))) {
            return no;
        } else {
            return -1;
        }
    }

    public int findLimbNo(String name) {
        Integer iNo = (Integer) limbNames.get(name);
        if (iNo == null) {
            return -1;
        } else {
            return iNo.intValue();
        }
    }

    public void updateLimb(int limbNo, int axis, double angle) {
        Limb limb = (Limb) limbs.get(limbNo - 1);
        limb.updateLimb(axis, angle);
    }

    public void reset() {
        Limb limb;
        for (int i = 0; i < limbs.size(); i++) {
            limb = (Limb) limbs.get(i);
            limb.reset();
        }
    }

    public void doMove(int dir) {
        if (dir == FWD) {
            doMoveVec(fwdVec);
        } else if (dir == BACK) {
            doMoveVec(backVec);
        } else if (dir == LEFT) {
            doMoveVec(leftVec);
        } else if (dir == RIGHT) {
            doMoveVec(rightVec);
        } else if (dir == UP) {
            doMoveVec(upVec);
            yCount++;
        } else if (dir == DOWN) {
            if (yCount > 0) {
                doMoveVec(downVec);
                yCount--;
            }
        } else {
            System.out.println("Unknown doMove() call");
        }
    }

    private void doMoveVec(Vector3d theMove) {
        figureTG.getTransform(transform3D);
        toMove.setTranslation(theMove);
        transform3D.mul(toMove);
        figureTG.setTransform(transform3D);
    }

    public void doRotateY(int rotDir) {
        if (rotDir == CLOCK) {
            doRotateYRad(-ROTATE_AMT);
        } else if (rotDir == CCLOCK) {
            doRotateYRad(ROTATE_AMT);
        } else {
            System.out.println("Unknown doRotateY() call");
        }
    }

    private void doRotateYRad(double radian) {
        figureTG.getTransform(transform3D);
        toRot.rotY(radian);
        transform3D.mul(toRot);
        figureTG.setTransform(transform3D);
    }
}
