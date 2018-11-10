package com.example.trees3D;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class WrapTrees3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final int BOUNDSIZE = 100;

    private static final Point3d USERPOSN = new Point3d(0, 7, 30);

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapTrees3D() {
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
        initUserPosition();
        orbitControls(canvas3D);

        View view = simpleUniverse.getViewer().getView();
        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph() {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        growTrees();

        sceneBG.compile();
    }

    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLight = new AmbientLight(white);
        ambientLight.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLight);

        Vector3f light1 = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f light2 = new Vector3f(1.0f, -1.0f, 1.0f);

        DirectionalLight directionalLight1 = new DirectionalLight(white, light1);
        directionalLight1.setInfluencingBounds(bounds);
        sceneBG.addChild(directionalLight1);

        DirectionalLight directionalLight2 = new DirectionalLight(white, light2);
        directionalLight2.setInfluencingBounds(bounds);
        sceneBG.addChild(directionalLight2);
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

    private void growTrees() {
        Transform3D transform3D = new Transform3D();
        transform3D.set(new Vector3f(0, 0, -5));
        TransformGroup transformGroup0 = new TransformGroup(transform3D);
        sceneBG.addChild(transformGroup0);
        TreeLimb treeLimb0 = new TreeLimb(Z_AXIS, 0, 0.05f, 0.5f, transformGroup0, null);

        transform3D.set(new Vector3f(-5, 0, 5));
        TransformGroup transformGroup1 = new TransformGroup(transform3D);
        sceneBG.addChild(transformGroup1);
        TreeLimb treeLimb1 = new TreeLimb(Y_AXIS, 45, 0.05f, 0.5f, transformGroup1, null);

        transform3D.set(new Vector3f(5, 0, 5));
        TransformGroup transformGroup2 = new TransformGroup(transform3D);
        sceneBG.addChild(transformGroup2);
        TreeLimb treeLimb2 = new TreeLimb(Y_AXIS, -60, 0.05f, 0.5f, transformGroup2, null);

        transform3D.set(new Vector3f(-9, 0, -9));
        TransformGroup transformGroup3 = new TransformGroup(transform3D);
        sceneBG.addChild(transformGroup3);
        TreeLimb treeLimb3 = new TreeLimb(Y_AXIS, 30, 0.05f, 0.5f, transformGroup3, null);

        transform3D.set(new Vector3f(9, 0, -9));
        TransformGroup transformGroup4 = new TransformGroup(transform3D);
        sceneBG.addChild(transformGroup4);
        TreeLimb treeLimb4 = new TreeLimb(Y_AXIS, -30, 0.05f, 0.5f, transformGroup4, null);

        ImageComponent2D[] leafIms = loadImages("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/trees3D/images/leaf", 6);

        GrowthBehavior growthBehavior = new GrowthBehavior(leafIms);
        growthBehavior.setSchedulingBounds(bounds);

        growthBehavior.addLimb(treeLimb0);
        growthBehavior.addLimb(treeLimb1);
        growthBehavior.addLimb(treeLimb2);
        growthBehavior.addLimb(treeLimb3);
        growthBehavior.addLimb(treeLimb4);

        sceneBG.addChild(growthBehavior);
    }

    private ImageComponent2D[] loadImages(String fNms, int numIms) {
        String fileName;
        TextureLoader textureLoader;
        ImageComponent2D[] ims = new ImageComponent2D[numIms];
        System.out.println("Loading " + numIms + " textures from " + fNms);
        for (int i = 0; i < numIms; i++) {
            fileName = new String(fNms + i + ".gif");
            textureLoader = new TextureLoader(fileName, null);
            ims[i] = textureLoader.getImage();
            if (ims[i] == null) {
                System.out.println("Loading failed for texture in : " + fileName);
            }
            ims[i].setCapability(ImageComponent2D.ALLOW_SIZE_READ);
        }
        return ims;
    }
}
