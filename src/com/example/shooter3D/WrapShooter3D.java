package com.example.shooter3D;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
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

public class WrapShooter3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;
    private static final int BOUNDSIZE = 100;

    private static final Point3d USERPOSN = new Point3d(0, 5, 20);

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    public WrapShooter3D() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);

        PhysicalEnvironment physicalEnvironment = simpleUniverse.getViewer().getPhysicalEnvironment();
        AudioDevice audioDevice = new JavaSoundMixer(physicalEnvironment);
        audioDevice.initialize();
        physicalEnvironment.setAudioDevice(audioDevice);
        //AudioDevice audioDevice = simpleUniverse.getViewer().createAudioDevice();

        createSceneGraph(canvas3D);
        initUserPosition();
        orbitControls(canvas3D);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(Canvas3D canvas3D) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        makeGun(canvas3D);
    }

    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLightNode);

        Vector3f light1Direction = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f light2Direction = new Vector3f(1.0f, -1.0f, 1.0f);

        DirectionalLight directionalLight1 = new DirectionalLight(white, light1Direction);
        directionalLight1.setInfluencingBounds(bounds);
        sceneBG.addChild(directionalLight1);

        DirectionalLight directionalLight2 = new DirectionalLight(white, light2Direction);
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

    private void makeGun(Canvas3D canvas3D) {
        Vector3d startVec = new Vector3d(0, 2, 0);

        GunTurret gunTurret = new GunTurret(startVec);
        sceneBG.addChild(gunTurret.getGunBG());

        PointSound explPS = initSound("Explo1.wav");
        ExplosionsClip explosionsClip = new ExplosionsClip(startVec, explPS);
        sceneBG.addChild(explosionsClip.getExplBG());

        PointSound beamPS = initSound("laser2.wav");
        LaserBeam laserBeam = new LaserBeam(startVec, beamPS);
        sceneBG.addChild(laserBeam.getBeamBG());

        ShootingBehaviour shootingBehaviour = new ShootingBehaviour(canvas3D, sceneBG, bounds, new Point3d(0, 2, 0),
                explosionsClip, laserBeam, gunTurret);
        sceneBG.addChild(shootingBehaviour);
    }

    private PointSound initSound(String filename) {
        MediaContainer soundMediaContainer = null;
        try {
            soundMediaContainer = new MediaContainer("file:sounds/" + filename);
            soundMediaContainer.setCacheEnable(true);
        } catch (Exception e) {
            System.out.println(e);
        }

        PointSound pointSound = new PointSound();
        pointSound.setSchedulingBounds(bounds);
        pointSound.setSoundData(soundMediaContainer);

        pointSound.setInitialGain(1.0f);

        pointSound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        pointSound.setCapability(PointSound.ALLOW_POSITION_WRITE);

        System.out.println("PointSound created from sounds/ " + filename);
        return pointSound;
    }
}
