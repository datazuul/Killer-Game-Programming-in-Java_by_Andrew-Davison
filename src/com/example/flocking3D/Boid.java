package com.example.flocking3D;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class Boid extends BranchGroup {

    private static final int FLOOR_LEN = 20;

    private static final Point3f MIN_PT = new Point3f(-(float) FLOOR_LEN / 2.0f, 0.05f, -(float) FLOOR_LEN / 2.0f);
    private static final Point3f MAX_PT = new Point3f((float) FLOOR_LEN / 2.0f, 8.0f, (float) FLOOR_LEN / 2.0f);

    private static final float MAX_SPEED = 0.2f;
    private static final float AVOID_WEIGHT = 0.2f;

    private static final int PERCH_TIME = 5;
    private static final int PERCH_INTERVAL = 100;

    private static final float BOID_RADIUS = 0.3f;

    protected FlockBehavior behavior;
    protected ArrayList velChanges = new ArrayList();

    protected Vector3f boidPos = new Vector3f();
    protected Vector3f boidVel = new Vector3f();

    private TransformGroup boidTG = new TransformGroup();
    private Obstacles obstacles;
    private BoundingSphere boundingSphere = new BoundingSphere();
    private float maxSpeed;

    private int perchTime = 0;
    private int perchInterval = 0;
    private boolean isPerching = false;

    private Vector3f avoidOb = new Vector3f();
    private Transform3D transform3D = new Transform3D();
    private Vector3f newVel = new Vector3f();

    public Boid(Color3f boidColour, float velFactor, Obstacles obs, FlockBehavior flockBehavior) {
        maxSpeed = MAX_SPEED * velFactor;
        obstacles = obs;
        behavior = flockBehavior;

        boidTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        boidTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        addChild(boidTG);

        boidPos.set(randPosn(), (float) (Math.random() * 6.0), randPosn());
        boidVel.set(randVel(), randVel(), randVel());
        boundingSphere.setRadius(BOID_RADIUS);

        moveBoid();
        boidTG.addChild(new BoidShape(boidColour));
    }

    private float randPosn() {
        return (float) (Math.random() * FLOOR_LEN - FLOOR_LEN / 2);
    }

    private float randVel() {
        return (float) (Math.random() * MAX_SPEED * 2 - MAX_SPEED);
    }

    private void moveBoid() {
        transform3D.setIdentity();
        transform3D.rotY(Math.atan2(boidVel.x, boidVel.z));
        transform3D.setTranslation(boidPos);
        boidTG.setTransform(transform3D);
    }

    public void animateBoid() {
        if (isPerching) {
            if (perchTime > 0) {
                perchTime--;
                return;
            } else {
                isPerching = false;
                boidPos.y = 0.1f;
                perchInterval = 0;
            }
        }
        boidVel.set(calcNewVel());
        boidPos.add(boidVel);
        keepInBounds();
        moveBoid();
    }

    private Vector3f calcNewVel() {
        velChanges.clear();

        Vector3f vector3f = avoidObstacles();
        if ((vector3f.x == 0.0f) && (vector3f.z == 0.0f)) {
            doVelocityRules();
        } else {
            velChanges.add(vector3f);
        }

        newVel.set(boidVel);
        for (int i = 0; i < velChanges.size(); i++) {
            newVel.add((Vector3f) velChanges.get(i));
        }
        newVel.scale(limitMaxSpeed());
        return newVel;
    }

    protected void doVelocityRules() {
        Vector3f vector3f1 = behavior.cohesion(boidPos);
        Vector3f vector3f2 = behavior.separation(boidPos);
        Vector3f vector3f3 = behavior.alignment(boidPos, boidVel);
        velChanges.add(vector3f1);
        velChanges.add(vector3f2);
        velChanges.add(vector3f3);
    }

    private Vector3f avoidObstacles() {
        avoidOb.set(0, 0, 0);
        boundingSphere.setCenter(new Point3d((double) boidPos.x, (double) boidPos.y, (double) boidPos.z));

        if (obstacles.isOverlapping(boundingSphere)) {
            avoidOb.set(-(float) Math.random() * boidPos.x, 0.0f, -(float) Math.random() * boidPos.z);
            avoidOb.scale(AVOID_WEIGHT);
        }
        return avoidOb;
    }

    private float limitMaxSpeed() {
        float speed = boidVel.length();
        if (speed > maxSpeed) {
            return maxSpeed / speed;
        } else {
            return 1.0f;
        }
    }

    private void keepInBounds() {
        if (boidPos.x > MAX_PT.x) {
            boidPos.x = MAX_PT.x;
            boidVel.x = -Math.abs(boidVel.x);
        } else if (boidPos.x < MIN_PT.x) {
            boidPos.x = MIN_PT.x;
            boidVel.x = Math.abs(boidVel.x);
        }

        if (boidPos.z > MAX_PT.z) {
            boidPos.z = MAX_PT.z;
            boidVel.z = -Math.abs(boidVel.z);
        } else if (boidPos.z < MIN_PT.z) {
            boidPos.z = MIN_PT.z;
            boidVel.z = Math.abs(boidVel.z);
        }

        if (boidPos.y > MAX_PT.y) {
            boidPos.y = MAX_PT.y;
            boidVel.y = -Math.abs(boidVel.y);
        } else if (boidPos.y < MIN_PT.y) {
            boidPos.y = MIN_PT.y;
            boidVel.y = Math.abs(boidVel.y);
        }

        perchInterval++;
        if ((perchInterval > PERCH_INTERVAL) && (boidPos.y <= MIN_PT.y)) {
            boidPos.y = 0.0f;
            perchTime = PERCH_TIME;
            isPerching = true;
        }
    }

    public Vector3f getBoidPos() {
        return boidPos;
    }

    public Vector3f getBoidVel() {
        return boidVel;
    }
}
