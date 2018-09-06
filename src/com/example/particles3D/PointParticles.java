package com.example.particles3D;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.util.Enumeration;

public class PointParticles extends Shape3D {

    private static final int POINTSIZE = 3;
    private static final float FADE_INCR = 0.05f;

    private static final float GRAVITY = 9.8f;
    private static final float TIMESTEP = 0.05f;
    private static final float XZ_VELOCITY = 2.0f;
    private static final float Y_VELOCITY = 6.0f;

    private static final Color3f yellow = new Color3f(1.0f, 1.0f, 0.6f);

    private PointArray pointParts;
    private ParticlesControl partBeh;

    private float[] cs, vels, accs, cols;
    private int numPoints;

    public PointParticles(int nps, int delay) {
        numPoints = nps;

        pointParts = new PointArray(numPoints, PointArray.COORDINATES | PointArray.COLOR_3 | PointArray.BY_REFERENCE);

        pointParts.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
        pointParts.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);

        PointsUpdater updater = new PointsUpdater();
        partBeh = new ParticlesControl(delay, updater);

        createGeometry();
        createAppearance();
    }

    public Behavior getParticleBeh() {
        return partBeh;
    }

    private void createGeometry() {
        cs = new float[numPoints * 3];
        vels = new float[numPoints * 3];
        accs = new float[numPoints * 3];
        cols = new float[numPoints * 3];

        for (int i = 0; i < numPoints * 3; i = i + 3) {
            initParticle(i);
        }

        pointParts.setCoordRefFloat(cs);
        pointParts.setColorRefFloat(cols);

        setGeometry(pointParts);
    }

    private void initParticle(int i) {
        cs[i] = 0.0f;
        cs[i + 1] = 0.0f;
        cs[i + 2] = 0.0f;

        double xvel = Math.random() * XZ_VELOCITY;
        double zvel = Math.sqrt((XZ_VELOCITY * XZ_VELOCITY) - (xvel * xvel));
        vels[i] = (float) ((Math.random() < 0.5) ? -xvel : xvel);
        vels[i + 2] = (float) ((Math.random() < 0.5) ? -zvel : zvel);
        vels[i + 1] = (float) (Math.random() * Y_VELOCITY);

        accs[i] = 0.0f;
        accs[i + 1] = -GRAVITY;
        accs[i + 2] = 0.0f;

        cols[i] = yellow.x;
        cols[i + 1] = yellow.y;
        cols[i + 2] = yellow.z;
    }

    private void createAppearance() {
        Appearance appearance = new Appearance();

        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setPointSize(POINTSIZE);
        appearance.setPointAttributes(pointAttributes);

        setAppearance(appearance);
    }

    public class PointsUpdater implements GeometryUpdater {

        @Override
        public void updateData(Geometry geometry) {
            for (int i = 0; i < numPoints * 3; i = i + 3) {
                if (cs[i + 1] < 0.0f) {
                    initParticle(i);
                } else {
                    updateParticle(i);
                }
            }
        }

        private void updateParticle(int i) {
            cs[i] += vels[i] * TIMESTEP + 0.5 * accs[i] * TIMESTEP * TIMESTEP;
            cs[i + 1] += vels[i + 1] * TIMESTEP + 0.5 * accs[i + 1] * TIMESTEP * TIMESTEP;
            cs[i + 2] += vels[i + 2] * TIMESTEP + 0.5 * accs[i + 2] * TIMESTEP * TIMESTEP;

            vels[i] += accs[i] * TIMESTEP;
            vels[i + 1] += accs[i + 1] * TIMESTEP;
            vels[i + 2] += accs[i + 2] * TIMESTEP;

            updateColour(i);
        }

        private void updateColour(int i) {
            cols[i + 1] = cols[i + 1] - FADE_INCR;
            if (cols[i + 1] < 0.0f) {
                cols[i + 1] = 0.0f;
            }

            cols[i + 2] = cols[i + 2] - FADE_INCR;
            if (cols[i + 2] < 0.0f) {
                cols[i + 2] = 0.0f;
            }
        }
    }

    public class ParticlesControl extends Behavior {

        private WakeupCondition timeDelay;
        private PointsUpdater updater;

        public ParticlesControl(int delay, PointsUpdater updt) {
            timeDelay = new WakeupOnElapsedTime(delay);
            updater = updt;
        }

        @Override
        public void initialize() {
            wakeupOn(timeDelay);
        }

        @Override
        public void processStimulus(Enumeration enumeration) {
            pointParts.updateData(updater);
            wakeupOn(timeDelay);
        }
    }
}
