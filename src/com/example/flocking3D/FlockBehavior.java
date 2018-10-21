package com.example.flocking3D;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Vector3f;
import java.util.Enumeration;

public class FlockBehavior extends Behavior {

    protected static final float PROXIMITY = 2.0f;

    private static final float COHESION_WEIGHT = 0.2f;
    private static final float SEPERATION_WEIGHT = 0.2f;
    private static final float ALIGNMENT_WEIGHT = 0.2f;

    private static final int DELAY = 50;

    protected BoidsList boidsList;
    protected BranchGroup boidsBG;

    private WakeupCondition timeOut;

    private Vector3f avgPosn = new Vector3f();
    private Vector3f distFrom = new Vector3f();
    private Vector3f moveAway = new Vector3f();
    private Vector3f avgVel = new Vector3f();
    private Vector3f clearDist = new Vector3f();

    public FlockBehavior(int numBoids) {
        boidsList = new BoidsList(numBoids);
        boidsBG = new BranchGroup();
        timeOut = new WakeupOnElapsedTime(DELAY);
    }

    @Override
    public void initialize() {
        wakeupOn(timeOut);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        Boid boid;
        int i = 0;
        while ((boid = boidsList.getBoid(i)) != null) {
            boid.animateBoid();
            i++;
        }
        wakeupOn(timeOut);
    }

    public BoidsList getBoidsList() {
        return boidsList;
    }

    public BranchGroup getBoidsBG() {
        return boidsBG;
    }

    public Vector3f cohesion(Vector3f boidPos) {
        avgPosn.set(0, 0, 0);
        int numFlockMates = 0;
        Vector3f pos;
        Boid boid;

        int i = 0;
        while ((boid = boidsList.getBoid(i)) != null) {
            distFrom.set(boidPos);
            pos = boid.getBoidPos();
            distFrom.sub(pos);
            if (distFrom.length() < PROXIMITY) {
                avgPosn.add(pos);
                numFlockMates++;
            }
            i++;
        }
        avgPosn.sub(boidPos);
        numFlockMates--;

        if (numFlockMates > 0) {
            avgPosn.scale(1.0f / numFlockMates);
            avgPosn.sub(boidPos);
            avgPosn.scale(COHESION_WEIGHT);
        }
        return avgPosn;
    }

    public Vector3f separation(Vector3f boidPos) {
        moveAway.set(0, 0, 0);
        int numFlockMates = 0;
        Vector3f pos;
        Boid boid;

        int i = 0;
        while ((boid = boidsList.getBoid(i)) != null) {
            distFrom.set(boidPos);
            distFrom.sub(boid.getBoidPos());
            if (distFrom.length() < PROXIMITY) {
                moveAway.add(distFrom);
                numFlockMates++;
            }
            i++;
        }
        numFlockMates--;
        if (numFlockMates > 0) {
            moveAway.scale(1.0f / numFlockMates);
            moveAway.scale(SEPERATION_WEIGHT);
        }
        return moveAway;
    }

    public Vector3f alignment(Vector3f boidPos, Vector3f boidVel) {
        avgVel.set(0, 0, 0);
        int numFlockMates = 0;
        Vector3f pos;
        Boid boid;

        int i = 0;
        while ((boid = boidsList.getBoid(i)) != null) {
            distFrom.set(boidPos);
            pos = boid.getBoidPos();
            distFrom.sub(pos);
            if (distFrom.length() < PROXIMITY) {
                avgVel.add(((Boid) boidsList.get(i)).getBoidVel());
                numFlockMates++;
            }
            i++;
        }
        avgVel.sub(boidVel);
        numFlockMates--;

        if (numFlockMates > 0) {
            avgVel.scale(1.0f / numFlockMates);
            avgVel.scale(ALIGNMENT_WEIGHT);
        }
        return avgVel;
    }
}
