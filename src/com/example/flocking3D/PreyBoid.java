package com.example.flocking3D;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class PreyBoid extends Boid {

    private static final Color3f orange = new Color3f(1.0f, 0.75f, 0.0f);

    public PreyBoid(Obstacles obs, PreyBehavior preyBehavior) {
        super(orange, 2.0f, obs, preyBehavior);
        setCapability(BranchGroup.ALLOW_DETACH);
    }

    @Override
    protected void doVelocityRules() {
        Vector3f vector3f = ((PreyBehavior) behavior).seePredators(boidPos);
        velChanges.add(vector3f);
        super.doVelocityRules();
    }

    public void boidDetach() {
        detach();
    }
}
