package com.example.flocking3D;

import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class PredatorBoid extends Boid {

    private static final Color3f yellow = new Color3f(1.0f, 1.0f, 1.0f);
    private static final int HUNGER_TRIGGER = 3;

    private int hungerCount;

    public PredatorBoid(Obstacles obs, PredatorBehavior predatorBehavior) {
        super(yellow, 1.0f, obs, predatorBehavior);
        hungerCount = 0;
    }

    @Override
    public void animateBoid() {
        hungerCount++;
        if (hungerCount > HUNGER_TRIGGER) {
            hungerCount -= ((PredatorBehavior) behavior).eatClosePrey(boidPos);
        }
        super.animateBoid();
    }

    @Override
    protected void doVelocityRules() {
        if (hungerCount > HUNGER_TRIGGER) {
            Vector3f vector3f = ((PredatorBehavior) behavior).findClosePrey(boidPos);
            velChanges.add(vector3f);
        }
        super.doVelocityRules();
    }
}
