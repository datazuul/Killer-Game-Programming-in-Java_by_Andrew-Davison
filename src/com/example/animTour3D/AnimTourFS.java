package com.example.animTour3D;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AnimTourFS {

    private GraphicsDevice graphicsDevice;
    private DisplayMode origDM = null;

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private Frame win;

    public AnimTourFS() {
        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();

        win = new Frame("AnimTourFS", configuration);
        win.setUndecorated(true);
        win.setResizable(false);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        if (!graphicsDevice.isFullScreenSupported()) {
            System.out.println("FSEM not supported.");
            System.out.println("Device = " + graphicsDevice);
            System.exit(0);
        }

        Canvas3D canvas3D = new Canvas3D(configuration);
        win.add(canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        canvas3D.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) || (keyCode == KeyEvent.VK_END) ||
                        ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
                    if (origDM != null) {
                        graphicsDevice.setDisplayMode(origDM);
                    }
                    graphicsDevice.setFullScreenWindow(null);
                    win.dispose();
                    System.exit(0);
                }
            }
        });
        graphicsDevice.setFullScreenWindow(win);
        if (graphicsDevice.getFullScreenWindow() == null) {
            System.out.println("Did not get FSEM");
        } else {
            System.out.println("Got FSEM");
        }

        if (graphicsDevice.isDisplayChangeSupported()) {
            origDM = graphicsDevice.getDisplayMode();
            graphicsDevice.setDisplayMode(new DisplayMode(origDM.getWidth(), origDM.getHeight(), origDM.getBitDepth() / 2, origDM.getRefreshRate()));
        }

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

    public static void main(String[] args) {
        new AnimTourFS();
    }
}
