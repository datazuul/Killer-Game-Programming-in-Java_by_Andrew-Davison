package com.example.tour3D;

import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WrapTour3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final int BOUNDSIZE = 100;

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private JFrame win;

    private Obstacles obstacles;
    private TourSprite bob;

    public WrapTour3D(String tourFnm, JFrame jFrame) {
        win = jFrame;
        setLayout(new BorderLayout());
        setOpaque(false);

        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();
        canvas3D.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
                        (keyCode == KeyEvent.VK_END) || ((keyCode == KeyEvent.VK_C) && e.isControlDown())) {
                    win.dispose();
                    System.exit(0);
                }
            }
        });

        simpleUniverse = new SimpleUniverse(canvas3D);
        createSceneGraph(tourFnm);
        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(String tourFnm) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getBG());

        makeScenery(tourFnm);
        addTourist();
        addAlien();

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
        TextureLoader bgTexture = new TextureLoader("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/tour3D/models/bigSky.jpg", null);
        Background background = new Background(bgTexture.getImage());
        background.setImageScaleMode(Background.SCALE_FIT_MAX);
        background.setApplicationBounds(bounds);

        sceneBG.addChild(background);
    }

    private void makeScenery(String tourFnm) {
        obstacles = new Obstacles();
        PropManager propManager;
        String tourFile = "models/" + tourFnm;
        System.out.println("Loading tour file : " + tourFile);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(tourFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("-o")) {
                    obstacles.store(line.substring(2).trim());
                } else {
                    propManager = new PropManager(line.trim(), true);
                    sceneBG.addChild(propManager.getTG());
                }
            }
            bufferedReader.close();
            sceneBG.addChild(obstacles.getObsGroup());
        } catch (IOException e) {
            System.out.println("Error reading tour file : " + tourFile);
            System.exit(1);
        }
    }

    private void addTourist() {
        bob = new TourSprite("Coolrobo.3ds", obstacles);
        bob.setPosition(2.0, 1.0);
        sceneBG.addChild(bob.getTG());

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup viewerTG = viewingPlatform.getViewPlatformTransform();

        TouristControls touristControls = new TouristControls(bob, viewerTG);
        touristControls.setSchedulingBounds(bounds);
        sceneBG.addChild(touristControls);
    }

    private void addAlien() {
        AlienSprite alienSprite = new AlienSprite("hand1.obj", obstacles, bob);
        alienSprite.setPosition(-6.0, -6.0);
        sceneBG.addChild(alienSprite.getTG());

        TimeBehavior alienTimer = new TimeBehavior(500, alienSprite);
        alienTimer.setSchedulingBounds(bounds);
        sceneBG.addChild(alienTimer);
    }
}
