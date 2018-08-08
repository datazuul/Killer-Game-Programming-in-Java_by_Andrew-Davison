package com.example.lathe3D;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Text2D;
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

public class WrapLathe3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final int BOUNDSIZE = 100;

    private static final Point3d USERPOSN = new Point3d(0, 7, 20);
    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapLathe3D() {
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

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph() {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getBG());

        addLatheShapes();

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
        Background back = new Background();
        back.setApplicationBounds(bounds);
        back.setColor(0.17f, 0.65f, 0.92f);
        sceneBG.addChild(back);
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

    private void addLatheShapes() {
        double xsIn1[] = {0, 1, 0};
        double ysIn1[] = {0, 1.5, 2.5};
        LatheShape3D latheShape3D1 = new LatheShape3D(xsIn1, ysIn1, null);
        displayLathe(latheShape3D1, -7.0f, -5.0f, "egg");

        System.out.println("Loading R texture");
        TextureLoader textureLoader1 = new TextureLoader("textures/r.gif", null);
        Texture rTex = textureLoader1.getTexture();

        System.out.println("Loading skin texture");
        TextureLoader textureLoader2 = new TextureLoader("textures/skin.jpg", null);
        Texture skinTex = textureLoader2.getTexture();

        System.out.println("Loading water texture");
        TextureLoader textureLoader3 = new TextureLoader("textures/water.jpg", null);
        Texture waterTex = textureLoader3.getTexture();

        System.out.println("Loading metal plate texture");
        TextureLoader textureLoader4 = new TextureLoader("textures/plate.jpg", null);
        Texture plateTex = textureLoader4.getTexture();

        System.out.println("Loading brick texture");
        TextureLoader textureLoader5 = new TextureLoader("textures/brick.gif", null);
        Texture brickTex = textureLoader5.getTexture();

        System.out.println("Loading lava texture");
        TextureLoader textureLoader6 = new TextureLoader("textures/lava.jpg", null);
        Texture lavaTex = textureLoader6.getTexture();

        System.out.println("Loading sky texture");
        TextureLoader textureLoader7 = new TextureLoader("textures/sky.jpg", null);
        Texture skyTex = textureLoader7.getTexture();

        System.out.println("Loading cobbles texture");
        TextureLoader textureLoader8 = new TextureLoader("textures/cobbles.jpg", null);
        Texture cobblesTex = textureLoader8.getTexture();

        System.out.println("Loading bark texture");
        TextureLoader textureLoader9 = new TextureLoader("textures/bark1.jpg", null);
        Texture barkTex = textureLoader9.getTexture();

        System.out.println("Loading swirled texture");
        TextureLoader textureLoader10 = new TextureLoader("textures/swirled.jpg", null);
        Texture swirledTex = textureLoader10.getTexture();

        double xsIn15[] = {0, 0.1, 0.7, 0};
        double ysIn15[] = {0, 0.1, 1.5, 2};
        LatheShape3D latheShape3D2 = new LatheShape3D(xsIn15, ysIn15, waterTex);
        displayLathe(latheShape3D2, -3.5f, -5.0f, "drip");

        double xsIn2[] = {-0.001, -0.7, -0.25, 0.25, 0.7, -0.6, -0.5};
        double ysIn2[] = {0, 0, 0.5, 1, 2.5, 3, 3};
        LatheShape3D latheShape3D3 = new LatheShape3D(xsIn2, ysIn2, swirledTex);
        displayLathe(latheShape3D3, -1.0f, -5.0f, "cup");

        double xsIn25[] = {0, 0.4, 0.6, 0};
        double ysIn25[] = {0, 0.4, 2.2, 3};
        LatheShape3D latheShape3D4 = new LatheShape3D(xsIn25, ysIn25, skinTex);
        displayLathe(latheShape3D4, 3.0f, -5.0f, "limb");

        double xsIn3[] = {-1, -1};
        double ysIn3[] = {0, 1};
        LatheShape3D latheShape3D5 = new LatheShape3D(xsIn3, ysIn3, rTex);
        displayLathe(latheShape3D5, 6.0f, -5.0f, "round R");

        EllipseShape3D ls6 = new EllipseShape3D(xsIn3, ysIn3, rTex);
        displayLathe(ls6, 6.0f, 0, "oval R");

        RhodoneaShape3D ls7 = new RhodoneaShape3D(xsIn3, ysIn3, null);
        displayLathe(ls7, 3.0f, 0, "flower");

        double xsIn4[] = {-0.001, -0.4, -0.2, 0.2, 0.3, -0.2, -0.3, -0.001};
        double ysIn4[] = {0, 0, 1, 1.2, 1.4, 1.6, 1.8, 1.8};
        LatheShape3D latheShape3D8 = new LatheShape3D(xsIn4, ysIn4, skyTex);
        displayLathe(latheShape3D8, -1.0f, 0, "chess");

        double xsIn5[] = {-0.001, -0.4, -0.15, -0.001};
        double ysIn5[] = {0, 0, 3, 3};
        LatheShape3D latheShape3D9 = new LatheShape3D(xsIn5, ysIn5, barkTex);
        displayLathe(latheShape3D9, -3.5f, 0, "branch");

        double xsIn6[] = {1, 1.5, 1, 0.5, 1};
        double ysIn6[] = {0, 0.5, 1, 0.5, 0};
        LatheShape3D latheShape3D10 = new LatheShape3D(xsIn6, ysIn6, lavaTex);
        displayLathe(latheShape3D10, -7.0f, 0, "torus");

        double xsIn7[] = {0, 0.4, -0.1, 0.1, 0.4, 0};
        double ysIn7[] = {0, 0.4, 0.8, 1.2, 1.6, 2};
        LatheShape3D latheShape3D11 = new LatheShape3D(xsIn7, ysIn7, cobblesTex);
        displayLathe(latheShape3D11, -3.5f, 5.0f, "dumbbel");

        double xsIn8[] = {-0.01, 1, 0};
        double ysIn8[] = {0, 0, 1};
        LatheShape3D latheShape3D12 = new LatheShape3D(xsIn8, ysIn8, brickTex);
        displayLathe(latheShape3D12, -1.0f, 5.0f, "dome");

        double xsIn9[] = {-0.01, 0.5, -1, -1.2, 1.4, -0.5, -0.5, 0};
        double ysIn9[] = {0, 0, 1.5, 1.5, 2, 2.5, 2.7, 2.7};
        EllipseShape3D ls13 = new EllipseShape3D(xsIn9, ysIn9, plateTex);
        displayLathe(ls13, 3.0f, 5.0f, "armour");

        Color3f brown = new Color3f(0.3f, 0.2f, 0.0f);
        Color3f darkBrown = new Color3f(0.15f, 0.1f, 0.0f);

        double xsIn10[] = {0, 0.75, 0.9, 0.75, 0};
        double ysIn10[] = {0, 0.23, 0.38, 0.53, 0.75};
        LatheShape3D latheShape3D14 = new LatheShape3D(xsIn10, ysIn10, darkBrown, brown);
        displayLathe(latheShape3D14, 6.0f, 5.0f, "saucer");
    }

    private void displayLathe(LatheShape3D latheShape3D, float x, float z, String label) {
        Transform3D transform3D = new Transform3D();
        transform3D.set(new Vector3f(x, 1.5f, z));
        TransformGroup transformGroup1 = new TransformGroup(transform3D);

        transformGroup1.addChild(latheShape3D);
        sceneBG.addChild(transformGroup1);

        Text2D message = new Text2D(label, white, "SansSerif", 72, Font.BOLD);
        transform3D.set(new Vector3f(x - 0.4f, 0.75f, z));
        TransformGroup transformGroup2 = new TransformGroup(transform3D);
        transformGroup2.addChild(message);
        sceneBG.addChild(transformGroup2);
    }
}
