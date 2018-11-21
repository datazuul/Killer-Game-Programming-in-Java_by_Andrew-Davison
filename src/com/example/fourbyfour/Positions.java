package com.example.fourbyfour;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.util.BitSet;

public class Positions {

    private static final int NUM_SPOTS = 64;
    private static final int PLAYER1 = 1;
    private static final int PLAYER2 = 2;

    private Switch posSwitch, player1Switch, player2Switch;
    private BitSet posMask, player1Mask, player2Mask;

    private Appearance blueAppearance, redAppearance, whiteAppearance;

    private Vector3f[] points;
    private Group group;

    public Positions() {
        initAppearances();
        initLocations();

        group = new Group();

        makeWhiteSpheres();
        makeRedSpheres();
        makeBlueCubes();
    }

    private void initAppearances() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f red = new Color3f(0.9f, 0.1f, 0.2f);
        Color3f blue = new Color3f(0.3f, 0.3f, 0.8f);
        Color3f ambRed = new Color3f(0.3f, 0.03f, 0.03f);
        Color3f ambBlue = new Color3f(0.03f, 0.03f, 0.3f);
        Color3f ambWhite = new Color3f(0.3f, 0.3f, 0.3f);
        Color3f specular = new Color3f(1.0f, 1.0f, 1.0f);

        Material redMaterial = new Material(ambRed, black, red, specular, 100.0f);
        redMaterial.setLightingEnable(true);
        redAppearance = new Appearance();
        redAppearance.setMaterial(redMaterial);

        Material blueMaterial = new Material(ambBlue, black, blue, specular, 100.0f);
        blueMaterial.setLightingEnable(true);
        blueAppearance = new Appearance();
        blueAppearance.setMaterial(blueMaterial);

        Material whiteMaterial = new Material(ambWhite, black, white, specular, 100.0f);
        whiteMaterial.setLightingEnable(true);
        whiteAppearance = new Appearance();
        whiteAppearance.setMaterial(whiteMaterial);
    }

    private void initLocations() {
        points = new Vector3f[NUM_SPOTS];
        int count = 0;
        for (int z = -30; z < 40; z += 20) {
            for (int y = -30; y < 40; y += 20) {
                for (int x = -30; x < 40; x += 20) {
                    points[count] = new Vector3f((float) x, (float) y, (float) z);
                    count++;
                }
            }
        }
    }

    private void makeWhiteSpheres() {
        posSwitch = new Switch(Switch.CHILD_MASK);
        posSwitch.setCapability(Switch.ALLOW_SWITCH_READ);
        posSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        posMask = new BitSet();

        Sphere posSphere;
        for (int i = 0; i < NUM_SPOTS; i++) {
            Transform3D transform3D = new Transform3D();
            transform3D.set(points[i]);
            TransformGroup transformGroup = new TransformGroup(transform3D);
            posSphere = new Sphere(2.0f, whiteAppearance);
            Shape3D shape3D = posSphere.getShape();
            shape3D.setUserData(new Integer(i));
            transformGroup.addChild(posSphere);
            posSwitch.addChild(transformGroup);
            posMask.set(i);
        }
        posSwitch.setChildMask(posMask);
        group.addChild(posSwitch);
    }

    private void makeRedSpheres() {
        player1Switch = new Switch(Switch.CHILD_MASK);
        player1Switch.setCapability(Switch.ALLOW_SWITCH_READ);
        player1Switch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        player1Mask = new BitSet();

        for (int i = 0; i < NUM_SPOTS; i++) {
            Transform3D transform3D = new Transform3D();
            transform3D.set(points[i]);
            TransformGroup transformGroup = new TransformGroup(transform3D);
            transformGroup.addChild(new Sphere(7.0f, redAppearance));
            player1Switch.addChild(transformGroup);
            player1Mask.clear(i);
        }
        player1Switch.setChildMask(player1Mask);
        group.addChild(player1Switch);
    }

    private void makeBlueCubes() {
        player2Switch = new Switch(Switch.CHILD_MASK);
        player2Switch.setCapability(Switch.ALLOW_SWITCH_READ);
        player2Switch.setCapability(Switch.ALLOW_SWITCH_WRITE);
        player2Mask = new BitSet();

        for (int i = 0; i < NUM_SPOTS; i++) {
            Transform3D transform3D = new Transform3D();
            transform3D.set(points[i]);
            TransformGroup transformGroup = new TransformGroup(transform3D);
            transformGroup.addChild(new Box(5.0f, 5.0f, 5.0f, blueAppearance));
            player2Switch.addChild(transformGroup);
            player2Mask.clear(i);
        }
        player2Switch.setChildMask(player2Mask);
        group.addChild(player2Switch);
    }

    public Group getGroup() {
        return group;
    }

    public void set(int position, int player) {
        posMask.clear(position);
        posSwitch.setChildMask(posMask);

        if (player == PLAYER1) {
            player1Mask.set(position);
            player1Switch.setChildMask(player1Mask);
        } else if (player == PLAYER2) {
            player2Mask.set(position);
            player2Switch.setChildMask(player2Mask);
        } else {
            System.out.println("Positions set() call with illegal player value : " + player);
        }
    }
}
