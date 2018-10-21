package com.example.flocking3D;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3f;

public class PreyBehavior extends FlockBehavior {

    private static final float FLEE_WEIGHT = 0.2f;

    private BoidsList predsList;
    private PredatorBehavior predatorBehavior;

    private Vector3f avoidPred = new Vector3f();
    private Vector3f distFrom = new Vector3f();

    public PreyBehavior(int numBoids, Obstacles obstacles) {
        super(numBoids);
        System.out.println("Num. Prey : " + numBoids);
        createBoids(numBoids, obstacles);
    }

    private void createBoids(int numBoids, Obstacles obstacles) {
        boidsBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        boidsBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        PreyBoid preyBoid;
        for (int i = 0; i < numBoids; i++) {
            preyBoid = new PreyBoid(obstacles, this);
            boidsBG.addChild(preyBoid);
            boidsList.add(preyBoid);
        }
        boidsBG.addChild(this);
    }

    public void setPredatorBehavior(PredatorBehavior predatorBehavior) {
        this.predatorBehavior = predatorBehavior;
    }

    public Vector3f seePredators(Vector3f boidPos) {
        predsList = predatorBehavior.getBoidsList();
        avoidPred.set(0, 0, 0);
        Vector3f predPos;
        PredatorBoid predatorBoid;

        int i = 0;
        while ((predatorBoid = (PredatorBoid) predsList.getBoid(i)) != null) {
            distFrom.set(boidPos);
            predPos = predatorBoid.getBoidPos();
            distFrom.sub(predPos);
            if (distFrom.length() < PROXIMITY) {
                avoidPred.set(distFrom);
                avoidPred.scale(FLEE_WEIGHT);
                break;
            }
            i++;
        }
        return avoidPred;
    }

    public void eatBoid(int i) {
        ((PreyBoid) boidsList.getBoid(i)).boidDetach();
        boidsList.removeBoid(i);
    }
}
