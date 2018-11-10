package com.example.trees3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.ArrayList;
import java.util.Enumeration;

public class GrowthBehavior extends Behavior {

    private static final int TIME_DELAY = 100;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private WakeupCondition timeOut;
    private ArrayList treeLimbs;
    private ImageComponent2D[] leafIms;

    public GrowthBehavior(ImageComponent2D[] leafIms) {
        timeOut = new WakeupOnElapsedTime(TIME_DELAY);
        treeLimbs = new ArrayList();
        this.leafIms = leafIms;
    }

    public void addLimb(TreeLimb treeLimb) {
        treeLimbs.add(treeLimb);
    }

    @Override
    public void initialize() {
        wakeupOn(timeOut);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        applyRulesToLimbs();
        wakeupOn(timeOut);
    }

    private void applyRulesToLimbs() {
        TreeLimb treeLimb;
        for (int i = 0; i < treeLimbs.size(); i++) {
            treeLimb = (TreeLimb) treeLimbs.get(i);
            applyRules(treeLimb);
            treeLimb.incrAge();
        }
    }

    private void applyRules(TreeLimb treeLimb) {
        if ((treeLimb.getLength() < 1.0f) && !treeLimb.isHasLeaves()) {
            treeLimb.scaleLength(1.1f);
        }

        if ((treeLimb.getRadius() <= (-0.05f * treeLimb.getLevel() + 0.25f)) && !treeLimb.isHasLeaves()) {
            treeLimb.scaleRadius(1.05f);
        }

        treeLimb.stepToBrown();

        int axis;
        if ((treeLimb.getAge() == 5) && (treeLimbs.size() <= 256) && !treeLimb.isHasLeaves() &&
                (treeLimb.getLevel() < 10)) {
            axis = (Math.random() < 0.5) ? Z_AXIS : X_AXIS;
            if (Math.random() < 0.85) {
                makeChild(axis, randomRange(10, 30), 0.05f, 0.5f, treeLimb);
            }

            axis = (Math.random() < 0.5) ? Z_AXIS : X_AXIS;
            if (Math.random() < 0.85) {
                makeChild(axis, randomRange(-30, -10), 0.05f, 0.5f, treeLimb);
            }
        }

        if ((treeLimb.getLevel() > 3) && (Math.random() < 0.08) && (treeLimb.getNumChildren() == 0) &&
                !treeLimb.isHasLeaves()) {
            makeLeaves(treeLimb);
        }

        if (treeLimb.getAge() % 10 == 0) {
            treeLimb.showNextLeaf();
        }

        if ((treeLimb.getAge() == 100) && (treeLimb.getLevel() == 1)) {
            treeLimb.setRadius(2.0f * treeLimb.getRadius());
            treeLimb.setCurrColor3f(new Color3f(0.0f, 0.0f, 1.0f));
        }
    }

    private void makeChild(int axis, double angle, float radius, float limbLen, TreeLimb parent) {
        TransformGroup startLimbTG = parent.getEndLimbTG();
        TreeLimb child = new TreeLimb(axis, angle, radius, limbLen, startLimbTG, parent);
        treeLimbs.add(child);
    }

    private void makeLeaves(TreeLimb treeLimb) {
        ImageCsSeries frontLeafShape = new ImageCsSeries(0.5f, 2.0f, leafIms);
        ImageCsSeries backLeafShape = new ImageCsSeries(-0.5f, 2.0f, leafIms);

        treeLimb.addLeaves(frontLeafShape, backLeafShape);
    }

    private double randomRange(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
}
