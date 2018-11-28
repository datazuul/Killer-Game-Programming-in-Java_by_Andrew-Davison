package com.example.nettour3D;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.DecimalFormat;

public class WrapNetTour3D extends JPanel {

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 300;
    private static final int BOUND_SIZE = 100;

    private static final int PORT = 5555;
    private static final String HOST = "localhost";

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere boundingSphere;

    private Obstacles obstacles;
    private TourSprite tourSprite;

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private DecimalFormat decimalFormat;

    public WrapNetTour3D(String userName, String tourFileName, double xPosition, double zPosition) {
        setLayout(new BorderLayout());
        setOpaque(false);
        decimalFormat = new DecimalFormat("0.###");

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);

        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);
        createSceneGraph(userName, tourFileName, xPosition, zPosition);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(String userName, String tourFileName, double xPosition, double zPosition) {
        sceneBG = new BranchGroup();
        boundingSphere = new BoundingSphere(new Point3d(0, 0, 0), BOUND_SIZE);

        sceneBG.setCapability(Group.ALLOW_CHILDREN_READ);
        sceneBG.setCapability(Group.ALLOW_CHILDREN_WRITE);
        sceneBG.setCapability(Group.ALLOW_CHILDREN_EXTEND);

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getFloorBG());

        makeScenery(tourFileName);
        makeContact();
        addTourist(userName, xPosition, zPosition);

        sceneBG.compile();
    }

    private void lightScene() {
        Color3f color3f = new Color3f(1.0f, 1.0f, 1.0f);

        AmbientLight ambientLight = new AmbientLight(color3f);
        ambientLight.setInfluencingBounds(boundingSphere);
        sceneBG.addChild(ambientLight);

        Vector3f vector3f1 = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f vector3f2 = new Vector3f(1.0f, -1.0f, 1.0f);

        DirectionalLight directionalLight1 = new DirectionalLight(color3f, vector3f1);
        directionalLight1.setInfluencingBounds(boundingSphere);
        sceneBG.addChild(directionalLight1);

        DirectionalLight directionalLight2 = new DirectionalLight(color3f, vector3f2);
        directionalLight2.setInfluencingBounds(boundingSphere);
        sceneBG.addChild(directionalLight2);
    }

    private void addBackground() {
        Background background = new Background();
        background.setApplicationBounds(boundingSphere);
        background.setColor(0.17f, 0.65f, 0.92f);
        sceneBG.addChild(background);
    }

    private void makeScenery(String tourFileName) {
        obstacles = new Obstacles();
        PropManager propManager;
        String tourFile = "models/" + tourFileName;//..................................................
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
                    sceneBG.addChild(propManager.getMoveTG());
                }
            }
            bufferedReader.close();
            sceneBG.addChild(obstacles.getObstaclesGroup());
        } catch (IOException e) {
            System.out.println("Error reading tour file : " + tourFile);
            System.exit(1);
        }
    }

    private void addTourist(String userName, double xPosition, double zPosition) {
        tourSprite = new TourSprite(userName, "Coolrobo.3ds", obstacles, xPosition, zPosition, printWriter);
        sceneBG.addChild(tourSprite.getObjectBG());

        ViewingPlatform viewingPlatform = simpleUniverse.getViewingPlatform();
        TransformGroup transformGroup = viewingPlatform.getViewPlatformTransform();

        TouristControls touristControls = new TouristControls(tourSprite, transformGroup);
        touristControls.setSchedulingBounds(boundingSphere);

        sceneBG.addChild(touristControls);
    }

    public DistanceTourSprite addVisitor(String userName, double xPosition, double zPosition, double rotationRadians) {
        DistanceTourSprite distanceTourSprite = new DistanceTourSprite(userName, "Coolrobo.3ds", obstacles,
                xPosition, zPosition);
        if (rotationRadians != 0) {
            distanceTourSprite.setCurrentRotation(rotationRadians);
        }

        BranchGroup branchGroup = distanceTourSprite.getObjectBG();
        branchGroup.compile();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        sceneBG.addChild(branchGroup);

        if (!branchGroup.isLive()) {
            System.out.println("Visitor Sprite is not live");
        } else {
            System.out.println("Visitor Sprite is now live");
        }
        return distanceTourSprite;
    }

    private void makeContact() {
        try {
            socket = new Socket(HOST, PORT);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

            new Watcher(this, bufferedReader, obstacles).start();
        } catch (Exception e) {
            System.out.println("No contact with server");
            System.exit(0);
        }
    }

    public void closeLink() {
        try {
            printWriter.println("bye");
            socket.close();
        } catch (Exception e) {
            System.out.println("Link terminated");
        }
        System.exit(0);
    }

    public void sendDetails(String clientAddress, String stringPort) {
        Point3d point3d = tourSprite.getCurrentLocation();
        double currentRotation = tourSprite.getCurrentRotation();
        String message = new String("detailsFor " + clientAddress + " " + stringPort + " " +
                decimalFormat.format(point3d.x) + " " + decimalFormat.format(point3d.z) + " " +
                decimalFormat.format(currentRotation));
        printWriter.println(message);
    }
}
