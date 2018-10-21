package com.example.flocking3D;

import javax.vecmath.Vector3f;

public class PredatorBehavior extends FlockBehavior {

    private static final float FIND_WEIGHT = 0.2f;

    private BoidsList preyList;
    private PreyBehavior preyBehavior;

    private Vector3f preyPos = new Vector3f();
    private Vector3f distFrom = new Vector3f();

    public PredatorBehavior(int numBoids, Obstacles obstacles) {
        super(numBoids);
        System.out.println("Num. Predators : " + numBoids);
        createBoids(numBoids, obstacles);
    }

    private void createBoids(int numBoids, Obstacles obstacles) {
        PredatorBoid predatorBoid;
        for (int i = 0; i < numBoids; i++) {
            predatorBoid = new PredatorBoid(obstacles, this);
            boidsBG.addChild(predatorBoid);
            boidsList.add(predatorBoid);
        }
        boidsBG.addChild(this);
    }

    public void setPreyBehavior(PreyBehavior preyBehavior) {
        this.preyBehavior = preyBehavior;
    }

    public int eatClosePrey(Vector3f boidPos) {
        preyList = preyBehavior.getBoidsList();
        int numPrey = preyList.size();
        int numEaten = 0;
        PreyBoid preyBoid;

        int i = 0;
        while ((preyBoid = (PreyBoid) preyList.getBoid(i)) != null) {
            distFrom.set(boidPos);
            distFrom.sub(preyBoid.getBoidPos());
            if (distFrom.length() < PROXIMITY / 3.0) {
                preyBehavior.eatBoid(i);
                numPrey--;
                numEaten++;
                System.out.println("Num. Prey : " + numPrey);
            } else {
                i++;
            }
        }
        return numEaten;
    }

    public Vector3f findClosePrey(Vector3f boidPos) {
        preyList = preyBehavior.getBoidsList();
        int numClosePrey = 0;
        preyPos.set(0, 0, 0);
        Vector3f pos;
        PreyBoid preyBoid;

        int i = 0;
        while ((preyBoid = (PreyBoid) preyList.getBoid(i)) != null) {
            pos = preyBoid.getBoidPos();
            distFrom.set(pos);
            distFrom.sub(boidPos);
            if (distFrom.length() < PROXIMITY * 1.5f) {
                preyPos.add(distFrom);
                numClosePrey++;
            }
            i++;
        }
        if (numClosePrey > 0) {
            preyPos.scale(1.0f / numClosePrey);
            preyPos.scale(FIND_WEIGHT);
        }
        return preyPos;
    }
}
