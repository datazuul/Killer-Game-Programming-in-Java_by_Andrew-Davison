package com.example.fpshooter3D;

import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;

public class WrapFPShooter3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final double Z_START = 9.0;

    private static final int BOUNDSIZE = 100;

    private static final String TARGET = "Coolrobo.3ds";
    private static final String GUN_PIC = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/fpshooter3D/images/gunDoom.gif";

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapFPShooter3D() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);

        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph();
        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph() {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        PropManager propManager = new PropManager(TARGET, true);
        sceneBG.addChild(propManager.getMoveTG());
        Vector3d targetVector3d = propManager.getLoc();
        System.out.println("Location of target : " + targetVector3d);

        initUserControls(targetVector3d);

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

    private void initUserControls(Vector3d targetVector3d) {
        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        PlatformGeometry platformGeometry = gunHand();
        viewingPlatform.setPlatformGeometry(platformGeometry);

        TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();
        Transform3D transform3D = new Transform3D();
        steerTG.getTransform(transform3D);
        transform3D.setTranslation(new Vector3d(0, 1, Z_START));
        steerTG.setTransform(transform3D);

        AmmoManager ammoManager = new AmmoManager(steerTG, sceneBG, targetVector3d);
        KeyBehavior keyBehavior = new KeyBehavior(ammoManager);
        keyBehavior.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(keyBehavior);
    }

    private PlatformGeometry gunHand() {
        PlatformGeometry platformGeometry = new PlatformGeometry();
        Point3f point3f1 = new Point3f(-0.1f, -0.3f, -0.7f);
        Point3f point3f2 = new Point3f(0.1f, -0.3f, -0.7f);
        Point3f point3f3 = new Point3f(0.1f, -0.1f, -0.7f);
        Point3f point3f4 = new Point3f(-0.1f, -0.1f, -0.7f);

        TexturedPlane texturedPlane = new TexturedPlane(point3f1, point3f2, point3f3, point3f4, GUN_PIC);
        platformGeometry.addChild(texturedPlane);
        return platformGeometry;
    }
}
