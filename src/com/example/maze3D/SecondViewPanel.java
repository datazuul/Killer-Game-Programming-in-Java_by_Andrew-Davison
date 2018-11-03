package com.example.maze3D;

import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;

public class SecondViewPanel extends JPanel {

    private static final int PANEL_WIDTH = 256;
    private static final int PANEL_HEIGHT = 256;

    private MazeManager mazeManager;
    private TransformGroup camera2TG;

    public SecondViewPanel(MazeManager mazeManager) {
        this.mazeManager = mazeManager;

        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);

        initView(canvas3D);
    }

    private void initView(Canvas3D canvas3D) {
        ViewPlatform viewPlatform = new ViewPlatform();

        View view = new View();
        view.setPhysicalBody(new PhysicalBody());
        view.setPhysicalEnvironment(new PhysicalEnvironment());
        view.addCanvas3D(canvas3D);
        view.attachViewPlatform(viewPlatform);
        view.setFieldOfView(Math.toRadians(90.0));
        view.setBackClipDistance(20);
        view.setFrontClipDistance(0.05);

        camera2TG = setCameraPosition();
        camera2TG.addChild(viewPlatform);
    }

    private TransformGroup setCameraPosition() {
        Vector3d startVector3d = mazeManager.getMazeStartPosn();

        Transform3D transform3D = new Transform3D();
        transform3D.lookAt(new Point3d(startVector3d.x, startVector3d.y, startVector3d.z), new Point3d(startVector3d.x,
                startVector3d.y, -10), new Vector3d(0, 1, 0));
        transform3D.invert();

        TransformGroup transformGroup = new TransformGroup(transform3D);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        return transformGroup;
    }

    public TransformGroup getCamera2TG() {
        return camera2TG;
    }
}
