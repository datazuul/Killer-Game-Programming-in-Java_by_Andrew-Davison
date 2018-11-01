package com.example.shooter3D;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

import javax.media.j3d.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class ShootingBehaviour extends PickMouseBehavior {

    private static final Vector3d UPVEC = new Vector3d(0.0, 1.0, 0.0);

    private Point3d startPt;
    private boolean firstRotation, finishedShot;
    private double shootAngle;

    private ExplosionsClip explosionsClip;
    private LaserBeam laserBeam;
    private GunTurret gunTurret;

    private AxisAngle4d rotAxisAngle4d = new AxisAngle4d();
    private Vector3d clickVec = new Vector3d();
    private Vector3d axisVec = new Vector3d();

    public ShootingBehaviour(Canvas3D canvas3D, BranchGroup rootBranchGroup, Bounds bounds, Point3d startPoint3d,
                             ExplosionsClip explosionsClip, LaserBeam laserBeam, GunTurret gunTurret) {
        super(canvas3D, rootBranchGroup, bounds);
        setSchedulingBounds(bounds);

        pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);

        startPt = startPoint3d;
        this.explosionsClip = explosionsClip;
        this.laserBeam = laserBeam;
        this.gunTurret = gunTurret;

        firstRotation = true;
        finishedShot = true;
    }

    @Override
    public void updateScene(int xpos, int ypos) {
        if (finishedShot) {
            pickCanvas.setShapeLocation(xpos, ypos);

            Point3d eyePos = pickCanvas.getStartPosition();

            PickResult pickResult = null;
            pickResult = pickCanvas.pickClosest();

            if (pickResult != null) {
                PickIntersection pickIntersection = pickResult.getClosestIntersection(startPt);
                Point3d intercept = pickIntersection.getPointCoordinatesVW();

                rotateToPoint(intercept);
                double turnAngle = calcTurn(eyePos, intercept);

                finishedShot = false;
                new FireBeam(intercept, this, laserBeam, explosionsClip, turnAngle).start();
            }
        }
    }

    private void pickResultInfo(PickResult pickResult) {
        Shape3D shape3D = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
        if (shape3D != null) {
            System.out.println("Shape3D : " + shape3D);
        } else {
            System.out.println("No Shape3D found");
        }

        SceneGraphPath sceneGraphPath = pickResult.getSceneGraphPath();
        if (sceneGraphPath != null) {
            int pathLen = sceneGraphPath.nodeCount();
            if (pathLen == 0) {
                System.out.println("Empty path");
            } else {
                for (int i = 0; i < pathLen; i++) {
                    Node node = sceneGraphPath.getNode(i);
                    if (node instanceof Shape3D) {
                        System.out.print(i + ". Shape3D : " + node);
                    } else {
                        System.out.println(i + ". Node : " + node);
                        String name = (String) node.getUserData();
                        if (name != null) {
                            System.out.println(name);
                        }
                    }
                }
            }
        } else {
            System.out.println("Path is null");
        }
    }

    private void rotateToPoint(Point3d intercept) {
        if (!firstRotation) {
            axisVec.negate();
            rotAxisAngle4d.set(axisVec, shootAngle);
            gunTurret.makeRotation(rotAxisAngle4d);
            laserBeam.makeRotation(rotAxisAngle4d);
        }

        clickVec.set(intercept.x - startPt.x, intercept.y - startPt.y, intercept.z - startPt.z);
        clickVec.normalize();
        axisVec.cross(UPVEC, clickVec);

        shootAngle = UPVEC.angle(clickVec);
        rotAxisAngle4d.set(axisVec, shootAngle);

        gunTurret.makeRotation(rotAxisAngle4d);
        laserBeam.makeRotation(rotAxisAngle4d);

        firstRotation = false;
    }

    private double calcTurn(Point3d eyePos, Point3d intercept) {
        double zDiff = eyePos.z - intercept.z;
        double xDiff = eyePos.x - intercept.x;

        double turnAngle = Math.atan2(xDiff, zDiff);
        return turnAngle;
    }

    public void setFinishedShot() {
        finishedShot = true;
    }
}
