package com.example.mover3D;

import javax.media.j3d.Transform3D;

public class MoveableLimb extends Limb {

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private String limbName;
    private double xMin, xMax, yMin, yMax, zMin, zMax;
    private double xCurrAng, yCurrAng, zCurrAng;

    private Transform3D currTrans, rotTrans;

    public MoveableLimb(String lName, int lNo, String jn0, String jn1, int axis, double angle, double[] xs, double[] ys,
                        String tex) {
        super(lNo, jn0, jn1, axis, angle, xs, ys, tex);

        rotTrans = new Transform3D();
        currTrans = new Transform3D();

        limbName = lName;
        xMin = 0;
        xMax = 0;
        yMin = 0;
        yMax = 0;
        zMin = 0;
        zMax = 0;
        xCurrAng = 0;
        yCurrAng = 0;
        zCurrAng = 0;
    }

    public void setRanges(double x1, double x2, double y1, double y2, double z1, double z2) {
        setRange(X_AXIS, x1, x2);
        setRange(Y_AXIS, y1, y2);
        setRange(Z_AXIS, z1, z2);
    }

    public void setRange(int axis, double angle1, double angle2) {
        if (angle1 > angle2) {
            System.out.println(limbName + " : range in wrong order.....swapping");
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }
        if (axis == X_AXIS) {
            xMin = angle1;
            xMax = angle2;
        } else if (axis == Y_AXIS) {
            yMin = angle1;
            yMax = angle2;
        } else {
            zMin = angle1;
            zMax = angle2;
        }
    }

    public void printLimb() {
        super.printLimb();
        System.out.println("< " + limbName + " ( " + xMin + ", " + xMax + ") ( " + yMin + ", " + yMax + ") ( " + zMin +
                ", " + zMax + ")>");
    }

    public void updateLimb(int axis, double angleStep) {
        if (axis == X_AXIS) {
            applyAngleStep(angleStep, xCurrAng, axis, xMax, xMin);
        } else if (axis == Y_AXIS) {
            applyAngleStep(angleStep, yCurrAng, axis, yMax, yMin);
        } else {
            applyAngleStep(angleStep, zCurrAng, axis, zMax, zMin);
        }
    }

    private void applyAngleStep(double angleStep, double currAngle, int axis, double max, double min) {
        if ((currAngle >= max) && (angleStep > 0)) {
            System.out.println(limbName + ": no rot; already at max");
            return;
        }
        if (currAngle <= min && (angleStep < 0)) {
            System.out.println(limbName + ": no rot; already at min");
            return;
        }

        double newAngle = currAngle + angleStep;
        if (newAngle > max) {
            System.out.println(limbName + ": reached max angle");
            angleStep = max - currAngle;
        } else if (newAngle < min) {
            System.out.println(limbName + ": reached min angle");
            angleStep = min - currAngle;
        }

        makeUpdate(axis, angleStep);
    }

    private void makeUpdate(int axis, double angleStep) {
        if (axis == X_AXIS) {
            rotTrans.rotX(Math.toRadians(angleStep));
            xAxisTG.getTransform(currTrans);
            currTrans.mul(rotTrans);
            xAxisTG.setTransform(currTrans);
            xCurrAng += angleStep;
        } else if (axis == Y_AXIS) {
            rotTrans.rotY(Math.toRadians(angleStep));
            yAxisTG.getTransform(currTrans);
            currTrans.mul(rotTrans);
            yAxisTG.setTransform(currTrans);
            yCurrAng += angleStep;
        } else {
            rotTrans.rotZ(Math.toRadians(angleStep));
            zAxisTG.getTransform(currTrans);
            currTrans.mul(rotTrans);
            zAxisTG.setTransform(currTrans);
            zCurrAng += angleStep;
        }
    }

    public void reset() {
        rotTrans.rotX(Math.toRadians(-xCurrAng));
        xAxisTG.getTransform(currTrans);
        currTrans.mul(rotTrans);
        xAxisTG.setTransform(currTrans);
        xCurrAng = 0;

        rotTrans.rotY(Math.toRadians(-yCurrAng));
        yAxisTG.getTransform(currTrans);
        currTrans.mul(rotTrans);
        yAxisTG.setTransform(currTrans);
        yCurrAng = 0;

        rotTrans.rotZ(Math.toRadians(-zCurrAng));
        zAxisTG.getTransform(currTrans);
        currTrans.mul(rotTrans);
        zAxisTG.setTransform(currTrans);
        zCurrAng = 0;
    }
}
