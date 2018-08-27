package com.example.animTour3D;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class WrapAnimTour3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapAnimTour3D() {
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
        bounds = new BoundingSphere(new Point3d(0, 0, 0), 100);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());
        addTourist();
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
        TextureLoader bgTexture = new TextureLoader("models/bigSky.jpg", null);
        Background background = new Background(bgTexture.getImage());
        background.setImageScaleMode(Background.SCALE_FIT_MAX);
        background.setApplicationBounds(bounds);
        sceneBG.addChild(background);
    }

    private void addTourist() {
        AnimSprite3D bob = new AnimSprite3D();
        bob.setPosition(2.0, 1.0);
        sceneBG.addChild(bob.getObjectTG());

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup viewerTG = viewingPlatform.getViewPlatformTransform();

        Animator animBeh = new Animator(20, bob, viewerTG);
        animBeh.setSchedulingBounds(bounds);
        sceneBG.addChild(animBeh);

        KeyBehavior keyBehavior = new KeyBehavior(animBeh);
        keyBehavior.setSchedulingBounds(bounds);
        sceneBG.addChild(keyBehavior);
    }
}
