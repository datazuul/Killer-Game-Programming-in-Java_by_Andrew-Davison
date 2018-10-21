package com.example.flocking3D;

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

public class WrapFlocking3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;
    private static final int BOUNDSIZE = 100;

    private static final Point3d USERPOSN = new Point3d(0, 5, 20);

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapFlocking3D(int numPreds, int numPrey, int numObs) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);
        createSceneGraph(numPreds, numPrey, numObs);
        initUserPosition();
        orbitControls(canvas3D);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(int numPreds, int numPrey, int numObs) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        addFlockingBoids(numPreds, numPrey, numObs);
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

    private void addFlockingBoids(int numPreds, int numPrey, int numObs) {
        Obstacles obstacles = new Obstacles(numObs);
        sceneBG.addChild(obstacles.getObsBG());

        PredatorBehavior predatorBehavior = new PredatorBehavior(numPreds, obstacles);
        predatorBehavior.setSchedulingBounds(bounds);
        sceneBG.addChild(predatorBehavior.getBoidsBG());

        PreyBehavior preyBehavior = new PreyBehavior(numPrey, obstacles);
        preyBehavior.setSchedulingBounds(bounds);
        sceneBG.addChild(preyBehavior.getBoidsBG());

        predatorBehavior.setPreyBehavior(preyBehavior);
        preyBehavior.setPredatorBehavior(predatorBehavior);
    }
}
