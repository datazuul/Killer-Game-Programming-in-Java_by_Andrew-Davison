package com.example.particles3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.util.Enumeration;

public class QuadParticles extends OrientedShape3D {

    private static final String TEX_FNM = "smoke.gif";
    private static final float QUAD_LEN = 0.5f;

    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f red = new Color3f(0.9f, 0.1f, 0.2f);
    private static final Color3f darkRed = new Color3f(1.0f, 0.0f, 0.0f);

    private static final float GRAVITY = 9.8f;
    private static final float TIMESTEP = 0.05f;
    private static final float XZ_VELOCITY = 2.0f;
    private static final float Y_VELOCITY = 8.0f;

    private static final float DELTA = 0.05f;

    private QuadArray quadParts;
    private ParticlesControl partBeh;

    private float[] cs, vels, accs, norms;
    private float[] tcoords;

    private float[] p1 = {-QUAD_LEN / 2, 0.0f, 0.0f};
    private float[] p2 = {QUAD_LEN / 2, 0.0f, 0.0f};
    private float[] p3 = {QUAD_LEN / 2, QUAD_LEN, 0.0f};
    private float[] p4 = {-QUAD_LEN / 2, QUAD_LEN, 0.0f};

    private int numPoints;

    public QuadParticles(int nps, int delay) {
        if (nps % 4 != 0) {
            nps = ((int) ((nps + 4) / 4) * 4);
        }
        numPoints = nps;

        setAlignmentAxis(0.0f, 0.0f, 1.0f);

        quadParts = new QuadArray(numPoints, GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2 |
                GeometryArray.NORMALS | GeometryArray.BY_REFERENCE);

        quadParts.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
        quadParts.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);

        QuadsUpdater updater = new QuadsUpdater();
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
        norms = new float[numPoints * 3];
        tcoords = new float[numPoints * 2];

        for (int i = 0; i < numPoints * 3; i = i + 12) {
            initQuadParticle(i);
        }
        quadParts.setCoordRefFloat(cs);

        for (int i = 0; i < numPoints * 2; i = i + 8) {
            tcoords[i] = 0.0f;
            tcoords[i + 1] = 0.0f;
            tcoords[i + 2] = 1.0f;
            tcoords[i + 3] = 0.0f;
            tcoords[i + 4] = 1.0f;
            tcoords[i + 5] = 1.0f;
            tcoords[i + 6] = 0.0f;
            tcoords[i + 7] = 1.0f;
        }
        quadParts.setTexCoordRefFloat(0, tcoords);

        Vector3f norm = new Vector3f();
        for (int i = 0; i < numPoints * 3; i = i + 3) {
            randomNormal(norm);
            norms[i] = norm.x;
            norms[i + 1] = norm.y;
            norms[i + 2] = norm.z;
        }
        quadParts.setNormalRefFloat(norms);

        setGeometry(quadParts);
    }

    private void initQuadParticle(int i) {
        setCoord(cs, i, p1);
        setCoord(cs, i + 3, p2);
        setCoord(cs, i + 6, p3);
        setCoord(cs, i + 9, p4);

        double xvel = Math.random() * XZ_VELOCITY;
        double zvel = Math.sqrt((XZ_VELOCITY * XZ_VELOCITY) - (xvel * xvel));

        vels[i] = (float) ((Math.random() < 0.5) ? -xvel : xvel);
        vels[i + 2] = (float) ((Math.random() < 0.5) ? -zvel : zvel);
        vels[i + 1] = (float) (Math.random() * Y_VELOCITY);

        accs[i] = 0.0f;
        accs[i + 1] = -GRAVITY;
        accs[i + 2] = 0.0f;

        copyCoord(vels, i + 3, i);
        copyCoord(vels, i + 6, i);
        copyCoord(vels, i + 9, i);

        copyCoord(accs, i + 3, i);
        copyCoord(accs, i + 6, i);
        copyCoord(accs, i + 9, i);
    }

    private void setCoord(float[] fs, int i, float[] p) {
        fs[i] = p[0];
        fs[i + 1] = p[1];
        fs[i + 2] = p[2];
    }

    private void copyCoord(float[] fs, int to, int from) {
        fs[to] = fs[from];
        fs[to + 1] = fs[from + 1];
        fs[to + 2] = fs[from + 2];
    }

    private void randomNormal(Vector3f vector3f) {
        float z = (float) Math.random();
        float x = (float) (Math.random() * 2.0 - 1.0);
        float y = (float) (Math.random() * 2.0 - 1.0);

        vector3f.set(x, y, z);
        vector3f.normalize();
    }

    private void createAppearance() {
        Appearance appearance = new Appearance();

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        appearance.setTransparencyAttributes(transparencyAttributes);

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        System.out.println("Loading textures from " + TEX_FNM);
        TextureLoader loader = new TextureLoader(TEX_FNM, null);
        Texture2D texture2D = (Texture2D) loader.getTexture();
        appearance.setTexture(texture2D);

        Material material = new Material(darkRed, black, red, white, 20.0f);
        material.setLightingEnable(true);
        appearance.setMaterial(material);

        setAppearance(appearance);
    }

    public class QuadsUpdater implements GeometryUpdater {

        @Override
        public void updateData(Geometry geometry) {
            for (int i = 0; i < numPoints * 3; i = i + 12) {
                updateQuadParticle(i);
            }
        }

        private void updateQuadParticle(int i) {
            if ((cs[i + 1] < 0.0f) && (cs[i + 4] < 0.0f) && (cs[i + 7] < 0.0f) && (cs[i + 10] < 0.0f)) {
                initQuadParticle(i);
            } else {
                updateParticle(i);
                updateParticle(i + 3);
                updateParticle(i + 6);
                updateParticle(i + 9);
            }
        }

        private void updateParticle(int i) {
            cs[i] += vels[i] * TIMESTEP + 0.5 * accs[i] * TIMESTEP * TIMESTEP;
            cs[i + 1] += vels[i + 1] * TIMESTEP + 0.5 * accs[i + 1] * TIMESTEP * TIMESTEP;
            cs[i + 2] += vels[i + 2] * TIMESTEP + 0.5 * accs[i + 2] * TIMESTEP * TIMESTEP;

            cs[i] = perturbate(cs[i], DELTA);
            cs[i + 1] = perturbate(cs[i + 1], DELTA);
            cs[i + 2] = perturbate(cs[i + 2], DELTA);

            vels[i] += accs[i] * TIMESTEP;
            vels[i + 1] += accs[i + 1] * TIMESTEP;
            vels[i + 2] += accs[i + 2] * TIMESTEP;
        }

        private float perturbate(float f, float range) {
            float randomRange = ((float) (Math.random() * range * 2.0f)) - range;
            return (f + randomRange);
        }
    }

    public class ParticlesControl extends Behavior {

        private WakeupCondition timeDelay;
        private QuadsUpdater updater;

        public ParticlesControl(int delay, QuadsUpdater quadsUpdater) {
            timeDelay = new WakeupOnElapsedTime(delay);
            updater = quadsUpdater;
        }

        @Override
        public void initialize() {
            wakeupOn(timeDelay);
        }

        @Override
        public void processStimulus(Enumeration enumeration) {
            quadParts.updateData(updater);
            wakeupOn(timeDelay);
        }
    }
}
