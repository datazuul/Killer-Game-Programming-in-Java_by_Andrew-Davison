package com.example.fractalland3D;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class WrapFractalLand3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final int BOUNDSIZE = 100;

    private Color3f skyColour = new Color3f(0.17f, 0.07f, 0.45f);

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private Landscape landscape;

    public WrapFractalLand3D(double flatness) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph(flatness);
        createUserControls();

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(double flatness) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        addFog();

        landscape = new Landscape(flatness);
        sceneBG.addChild(landscape.getLandBG());

        sceneBG.compile();
    }

    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f vector3f = new Vector3f(1.0f, -1.0f, -0.8f);

        DirectionalLight light1 = new DirectionalLight(white, vector3f);
        light1.setInfluencingBounds(bounds);
        sceneBG.addChild(light1);
    }

    private void addBackground() {
        Background background = new Background();
        background.setApplicationBounds(bounds);
        background.setColor(skyColour);
        sceneBG.addChild(background);
    }

    private void addFog() {
        LinearFog linearFog = new LinearFog(skyColour, 15.0f, 30.0f);
        linearFog.setInfluencingBounds(bounds);
        sceneBG.addChild(linearFog);
    }

    private void createUserControls() {
        View view = simpleUniverse.getViewer().getView();
        view.setBackClipDistance(20);
        view.setFrontClipDistance(0.05);

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();

        KeyBehavior keyBehavior = new KeyBehavior(landscape, steerTG);
        keyBehavior.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(keyBehavior);
    }
}
