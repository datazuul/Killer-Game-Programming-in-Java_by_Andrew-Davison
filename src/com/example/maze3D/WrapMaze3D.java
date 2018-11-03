package com.example.maze3D;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.PlatformGeometry;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class WrapMaze3D extends JPanel {

    private static final int PANEL_WIDTH = 512;
    private static final int PANEL_HEIGHT = 512;

    private static final int BOUNDSIZE = 100;
    private static final String SKY_TEXTURE = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/maze3D/images/lava.jpg";

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private MazeManager mazeManager;
    private TransformGroup camera2TG;

    public WrapMaze3D(MazeManager mazeManager, BirdsEye birdsEye, TransformGroup camera2TG) {
        this.mazeManager = mazeManager;
        this.camera2TG = camera2TG;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph();
        prepareViewPoint(birdsEye);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph() {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();

        TexturedFloor texturedFloor = new TexturedFloor();
        sceneBG.addChild(texturedFloor.getFloorBG());

        sceneBG.addChild(mazeManager.getMazeBG());
        sceneBG.addChild(camera2TG);

        sceneBG.compile();
    }

    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);

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
        System.out.println("Loading sky texture : " + SKY_TEXTURE);
        TextureLoader textureLoader = new TextureLoader(SKY_TEXTURE, null);

        Appearance appearance = new Appearance();
        appearance.setTexture(textureLoader.getTexture());

        Sphere sphere = new Sphere(100.0f, Sphere.GENERATE_NORMALS_INWARD | Sphere.GENERATE_TEXTURE_COORDS,
                4, appearance);
        sceneBG.addChild(sphere);
    }

    private void prepareViewPoint(BirdsEye birdsEye) {
        View userView = simpleUniverse.getViewer().getView();
        userView.setFieldOfView(Math.toRadians(90.0));
        userView.setBackClipDistance(20);
        userView.setFrontClipDistance(0.05);

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();

        PlatformGeometry platformGeometry = new PlatformGeometry();
        platformGeometry.addChild(makeSpot());
        viewingPlatform.setPlatformGeometry(platformGeometry);

        TransformGroup steerTG = viewingPlatform.getViewPlatformTransform();
        initViewPosition(steerTG);

        KeyBehavior keyBehavior = new KeyBehavior(mazeManager, birdsEye, camera2TG);
        keyBehavior.setSchedulingBounds(bounds);
        viewingPlatform.setViewPlatformBehavior(keyBehavior);
    }

    private void initViewPosition(TransformGroup steerTG) {
        Transform3D transform3D = new Transform3D();
        steerTG.getTransform(transform3D);
        Transform3D toRotTransform3D = new Transform3D();
        toRotTransform3D.rotY(-Math.PI);

        transform3D.mul(toRotTransform3D);
        transform3D.setTranslation(mazeManager.getMazeStartPosn());
        steerTG.setTransform(transform3D);
    }

    private SpotLight makeSpot() {
        SpotLight spotLight = new SpotLight();
        spotLight.setPosition(0.0f, 0.5f, 0.0f);
        spotLight.setAttenuation(0.0f, 1.2f, 0.0f);
        spotLight.setSpreadAngle((float) Math.toRadians(30.0));
        spotLight.setConcentration(5.0f);
        spotLight.setInfluencingBounds(bounds);
        return spotLight;
    }

    private TransformGroup makeAvatar() {
        Transform3D transform3D = new Transform3D();
        transform3D.rotX(Math.PI / 2);
        TransformGroup userTG = new TransformGroup(transform3D);

        userTG.addChild(new Cone(0.35f, 1.0f));
        return userTG;
    }
}
