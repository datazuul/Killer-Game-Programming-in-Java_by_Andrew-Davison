package com.example.terra3D;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class HeightFinder extends Thread {

    private static final Vector3d DOWN_VEC = new Vector3d(0.0, -1.0, 0.0);

    private Landscape landscape;
    private KeyBehavior keyBehavior;

    private PickTool pickTool;
    private double scaleLen;
    private Vector3d theMoveVector3d;
    private boolean calcRequested;

    public HeightFinder(Landscape landscape, KeyBehavior keyBehavior) {
        this.landscape = landscape;
        this.keyBehavior = keyBehavior;

        pickTool = new PickTool(landscape.getLandBG());
        pickTool.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        scaleLen = landscape.getScaleLen();
        theMoveVector3d = new Vector3d();
        calcRequested = false;
    }

    synchronized public void requestMoveHeight(Vector3d moveVector3d) {
        theMoveVector3d.set(moveVector3d.x, moveVector3d.y, moveVector3d.z);
        calcRequested = true;
    }

    synchronized private Vector3d getMove() {
        calcRequested = false;
        return new Vector3d(theMoveVector3d.x, theMoveVector3d.y, theMoveVector3d.z);
    }

    public void run() {
        Vector3d vector3d;
        while (true) {
            if (calcRequested) {
                vector3d = getMove();
                getLandHeight(vector3d.x, vector3d.z);
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    private void getLandHeight(double x, double z) {
        Point3d pickStart = new Point3d(x, 2000, z);
        pickTool.setShapeRay(pickStart, DOWN_VEC);

        PickResult pickResult = pickTool.pickClosest();

        if (pickResult != null) {
            if (pickResult.numIntersections() != 0) {
                PickIntersection pickIntersection = pickResult.getIntersection(0);
                Point3d nextPoint3d;
                try {
                    nextPoint3d = pickIntersection.getPointCoordinates();
                } catch (Exception e) {
                    System.out.println(e);
                    return;
                }

                double nextYHeight = nextPoint3d.z * scaleLen;
                keyBehavior.adjustHeight(nextYHeight);
            }
        }
    }
}
