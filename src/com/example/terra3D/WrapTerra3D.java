package com.example.terra3D;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.Random;

public class WrapTerra3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;
    private static final int BOUNDSIZE = 100;

    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private static final int NUM_STARS = 5000;

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private Landscape landscape;

    public WrapTerra3D(String fname) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph(fname);
        createUserControls();

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(String fname) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        landscape = new Landscape(sceneBG, fname);

        sceneBG.compile();
    }

    private void lightScene() {
        AmbientLight ambientLight = new AmbientLight(white);
        ambientLight.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLight);

        Vector3f lightVector3f = new Vector3f(1.0f, -1.0f, 1.0f);

        DirectionalLight directionalLight = new DirectionalLight(white, lightVector3f);
        directionalLight.setInfluencingBounds(bounds);
        sceneBG.addChild(directionalLight);
    }

    private void addBackground() {
        Background background = new Background();
        background.setApplicationBounds(bounds);
        background.setColor(0.17f, 0.50f, 0.92f);
        background.setGeometry(addStars());
        sceneBG.addChild(background);
    }

    private BranchGroup addStars() {
        PointArray starField = new PointArray(NUM_STARS, PointArray.COORDINATES | PointArray.COLOR_3);

        float[] pt = new float[3];
        float[] brightness = new float[3];
        Random random = new Random();

        for (int i = 0; i < NUM_STARS; i++) {
            pt[0] = (random.nextInt(2) == 0) ? -random.nextFloat() : random.nextFloat();
            pt[1] = random.nextFloat();
            pt[2] = (random.nextInt(2) == 0) ? -random.nextFloat() : random.nextFloat();
            starField.setCoordinate(i, pt);

            float mag = random.nextFloat();
            brightness[0] = mag;
            brightness[1] = mag;
            brightness[2] = mag;
            starField.setColor(i, brightness);
        }

        BranchGroup branchGroup = new BranchGroup();
        branchGroup.addChild(new Shape3D(starField));
        return branchGroup;
    }

    private void createUserControls() {
        View view = simpleUniverse.getViewer().getView();
        view.setBackClipDistance(20);
        view.setFrontClipDistance(0.05);

        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();

        KeyBehavior keyBehavior = new KeyBehavior(landscape, steerTG);
        keyBehavior.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(keyBehavior);
    }
}
