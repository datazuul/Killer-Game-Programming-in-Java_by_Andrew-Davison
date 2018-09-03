package com.example.mover3D;

import javax.vecmath.Point2d;

public class LatheCurve {

    private static final int STEP = 5;

    double xs[], ys[];
    double height;

    public LatheCurve(double xsIn[], double ysIn[]) {
        checkLength(xsIn, ysIn);
        checkYs(ysIn);
        int numVerts = xsIn.length;

        Point2d startTangent = new Point2d((Math.abs(xsIn[1]) - Math.abs(xsIn[0])) * 2, 0);
        Point2d endTangent = new Point2d((Math.abs(xsIn[numVerts - 1]) - Math.abs(xsIn[numVerts - 2])) * 2, 0);

        makeCurve(xsIn, ysIn, startTangent, endTangent);
    }

    public LatheCurve(double xsIn[], double ysIn[], Point2d startTangent, Point2d endTangent) {
        checkLength(xsIn, ysIn);
        checkYs(ysIn);
        checkTangent(startTangent);
        checkTangent(endTangent);
        makeCurve(xsIn, ysIn, startTangent, endTangent);
    }

    private void checkLength(double xsIn[], double ysIn[]) {
        int numVerts = xsIn.length;

        if (numVerts < 2) {
            System.out.println("Not enough points to make a curve");
            System.exit(0);
        } else if (numVerts != ysIn.length) {
            System.out.println("xsIn[] and ysIn[] do not have the same number of points");
            System.exit(0);
        }
    }

    private void checkYs(double[] ysIn) {
        if (ysIn[0] != 0) {
            System.out.println("The first y-coordinate must be 0; correcting it");
            ysIn[0] = 0;
        }

        height = ysIn[0];
        for (int i = 1; i < ysIn.length; i++) {
            if (ysIn[i] >= height) {
                height = ysIn[i];
            }
            if (ysIn[i] < 0) {
                System.out.println("Found a negative y-coord; changing it to be 0");
                ysIn[i] = 0;
            }
        }
        if (height != ysIn[ysIn.length - 1]) {
            System.out.println("Warning: max height is not the last y-coordinate");
        }
    }

    private void checkTangent(Point2d tangent) {
        if ((tangent.x == 0) && (tangent.y == 0)) {
            System.out.println("A tangent cannot be (0, 0)");
            System.exit(0);
        }
    }

    private void makeCurve(double xsIn[], double ysIn[], Point2d startTangent, Point2d endTangent) {
        int numInVerts = xsIn.length;
        int numOutVerts = countVerts(xsIn, numInVerts);
        xs = new double[numOutVerts];
        ys = new double[numOutVerts];

        xs[0] = Math.abs(xsIn[0]);
        ys[0] = ysIn[0];
        int startPosn = 1;

        Point2d t0 = new Point2d();
        Point2d t1 = new Point2d();

        for (int i = 0; i < numInVerts - 1; i++) {
            if (i == 0) {
                t0.set(startTangent.x, startTangent.y);
            } else {
                t0.set(t1.x, t1.y);
            }

            if (i == numInVerts - 2) {
                t1.set(endTangent.x, endTangent.y);
            } else {
                setTangent(t1, xsIn, ysIn, i + 1);
            }

            if (xsIn[i] < 0) {
                xs[startPosn] = Math.abs(xsIn[i + 1]);
                ys[startPosn] = ysIn[i + 1];
                startPosn++;
            } else {
                makeHermite(xs, ys, startPosn, xsIn[i], ysIn[i], xsIn[i + 1], ysIn[i + 1], t0, t1);
                startPosn += (STEP + 1);
            }
        }
    }

    private int countVerts(double xsIn[], int num) {
        int numOutVerts = 1;
        for (int i = 0; i < num - 1; i++) {
            if (xsIn[i] < 0) {
                numOutVerts++;
            } else {
                numOutVerts += (STEP + 1);
            }
        }
        return numOutVerts;
    }

    private void setTangent(Point2d tangent, double xsIn[], double ysIn[], int i) {
        double xLen = Math.abs(xsIn[i + 1]) - Math.abs(xsIn[i - 1]);
        double yLen = ysIn[i + 1] - ysIn[i - 1];
        tangent.set(xLen / 2, yLen / 2);
    }

    private void makeHermite(double[] xs, double[] ys, int startPosn, double x0, double y0, double x1, double y1,
                             Point2d t0, Point2d t1) {
        double xCoord, yCoord;
        double tStep = 1.0 / (STEP + 1);
        double t;

        if (x1 < 0) {
            x1 = -x1;
        }

        for (int i = 0; i < STEP; i++) {
            t = tStep * (i + 1);
            xCoord = (fh1(t) * x0) + (fh2(t) * x1) + (fh3(t) * t0.x) + (fh4(t) * t1.x);
            xs[startPosn + i] = xCoord;

            yCoord = (fh1(t) * y0) + (fh2(t) * y1) + (fh3(t) * t0.y) + (fh4(t) * t1.y);
            ys[startPosn + i] = yCoord;
        }

        xs[startPosn + STEP] = x1;
        ys[startPosn + STEP] = y1;
    }

    private double fh1(double t) {
        return (2.0) * Math.pow(t, 3) - (3.0 * t * t) + 1;
    }

    private double fh2(double t) {
        return (-2.0) * Math.pow(t, 3) + (3.0 * t * t);
    }

    private double fh3(double t) {
        return Math.pow(t, 3) - (2.0 * t * t) + t;
    }

    private double fh4(double t) {
        return Math.pow(t, 3) - (t * t);
    }

    public double[] getXs() {
        return xs;
    }

    public double[] getYs() {
        return ys;
    }

    public double getHeight() {
        return height;
    }
}
