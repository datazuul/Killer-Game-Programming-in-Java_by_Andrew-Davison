package com.example.particles3D;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class WrapParticles3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;
    private static final int BOUNDSIZE = 100;
    private static final Point3d USERPOSN = new Point3d(0, 5, 20);

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapParticles3D(int numParticles, int fountainChoice) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph(numParticles, fountainChoice);
        initUserPosition();
        orbitControls(canvas3D);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(int numParts, int fountainChoice) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        switch (fountainChoice) {
            case 1:
                addPointsFountain(numParts);
                break;
            case 2:
                addLinesFountain(numParts);
                break;
            case 3:
                addQuadFountain(numParts);
                break;
            default:
                break;
        }

        sceneBG.compile();
    }

    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLightNode);

        Vector3f light1Direction = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f light2Direction = new Vector3f(1.0f, -1.0f, 1.0f);

        DirectionalLight light1 = new DirectionalLight(white, light1Direction);
        light1.setInfluencingBounds(bounds);
        sceneBG.addChild(light1);

        DirectionalLight light2 = new DirectionalLight(white, light2Direction);
        light2.setInfluencingBounds(bounds);
        sceneBG.addChild(light2);
    }

    private void addBackground() {
        Background background = new Background();
        background.setApplicationBounds(bounds);
        background.setColor(0.17f, 0.65f, 0.92f);
        sceneBG.addChild(background);
    }

    private void orbitControls(Canvas3D canvas3D) {
        OrbitBehavior orbitBehavior = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
        orbitBehavior.setSchedulingBounds(bounds);

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        viewingPlatform.setViewPlatformBehavior(orbitBehavior);
    }

    private void initUserPosition() {
        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();

        Transform3D transform3D = new Transform3D();
        steerTG.getTransform(transform3D);

        transform3D.lookAt(USERPOSN, new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
        transform3D.invert();

        steerTG.setTransform(transform3D);
    }

    private void addPointsFountain(int numParts) {
        PointParticles pointParticles = new PointParticles(numParts, 20);

        TransformGroup posnTG = new TransformGroup();
        Transform3D transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3d(2.0f, 0.0f, 1.0f));
        posnTG.setTransform(transform3D);
        posnTG.addChild(pointParticles);
        sceneBG.addChild(posnTG);

        Behavior partBeh = pointParticles.getParticleBeh();
        partBeh.setSchedulingBounds(bounds);
        sceneBG.addChild(partBeh);
    }

    private void addQuadFountain(int numParts) {
        QuadParticles quadParticles = new QuadParticles(numParts, 20);
        sceneBG.addChild(quadParticles);

        Behavior partBeh = quadParticles.getParticleBeh();
        partBeh.setSchedulingBounds(bounds);
        sceneBG.addChild(partBeh);
    }

    private void addLinesFountain(int numParts) {
        LineParticles lineParticles = new LineParticles(numParts, 20);
        sceneBG.addChild(lineParticles);

        Behavior partBeh = lineParticles.getParticleBeh();
        partBeh.setSchedulingBounds(bounds);
        sceneBG.addChild(partBeh);
    }
}
