package com.example.fractalland3D;

import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class Landscape {

    private static final int WORLD_LEN = 64;

    private static final int NUM_TEXTURES = 5;
    private static final String textureFns[] = {"water-shallow.jpg", "sand.jpg", "grass.gif", "dryEarth.jpg", "stone.gif"};

    private static final double MIN_HEIGHT = -2.0;
    private static final double MAX_HEIGHT = 8.0;

    private static final Vector3d DOWN_VEC = new Vector3d(0.0, -1.0, 0.0);

    private BranchGroup landBG, floorBG;
    private Point3d vertices[];
    private double textureBoundaries[];

    private Vector3d originVector3d = new Vector3d();
    private boolean foundOrigin = false;
    private PickTool picker;

    public Landscape(double flatness) {
        landBG = new BranchGroup();
        floorBG = new BranchGroup();
        landBG.addChild(floorBG);

        setTexBoundaries();

        picker = new PickTool(floorBG);
        picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        FractalMesh fractalMesh = new FractalMesh(flatness);
        vertices = fractalMesh.getVertices();

        platifyFloor();
        addWalls();
    }

    private void setTexBoundaries() {
        textureBoundaries = new double[NUM_TEXTURES];
        double boundStep = (MAX_HEIGHT - MIN_HEIGHT) / NUM_TEXTURES;
        double boundary = MIN_HEIGHT + boundStep;
        for (int j = 0; j < NUM_TEXTURES; j++) {
            textureBoundaries[j] = boundary;
            boundary += boundStep;
        }
    }

    private void platifyFloor() {
        ArrayList[] coordsList = new ArrayList[NUM_TEXTURES];
        for (int i = 0; i < NUM_TEXTURES; i++) {
            coordsList[i] = new ArrayList();
        }

        int heightIdx;
        for (int j = 0; j < vertices.length; j = j + 4) {
            heightIdx = findHeightIdx(j);
            addCoords(coordsList[heightIdx], j);
            checkForOrigin(j);
        }

        for (int i = 0; i < NUM_TEXTURES; i++) {
            if (coordsList[i].size() > 0) {
                floorBG.addChild(new TexturedPlanes(coordsList[i], "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/fractalland3D/images/" + textureFns[i]));
            }
        }
    }

    private int findHeightIdx(int vertIndex) {
        double ah = avgHeight(vertIndex);
        for (int i = 0; i < textureBoundaries.length; i++) {
            if (ah < textureBoundaries[i]) {
                return i;
            }
        }
        return NUM_TEXTURES - 1;
    }

    private double avgHeight(int vi) {
        return (vertices[vi].y + vertices[vi + 1].y + vertices[vi + 2].y + vertices[vi + 3].y) / 4.0;
    }

    private void addCoords(ArrayList coords, int vi) {
        coords.add(vertices[vi]);
        coords.add(vertices[vi + 1]);
        coords.add(vertices[vi + 2]);
        coords.add(vertices[vi + 3]);
    }

    private void checkForOrigin(int vi) {
        if (!foundOrigin) {
            if ((vertices[vi].x == 0.0) && (vertices[vi].z == 0.0)) {
                originVector3d.y = vertices[vi].y;
                foundOrigin = true;
            }
        }
    }

    private void addWalls() {
        Color3f eveningBlue = new Color3f(0.17f, 0.07f, 0.45f);

        Point3d point3d1 = new Point3d(-WORLD_LEN / 2.0f, MIN_HEIGHT, -WORLD_LEN / 2.0f);
        Point3d point3d2 = new Point3d(-WORLD_LEN / 2.0f, MAX_HEIGHT, -WORLD_LEN / 2.0f);

        Point3d point3d3 = new Point3d(-WORLD_LEN / 2.0f, MIN_HEIGHT, WORLD_LEN / 2.0f);
        Point3d point3d4 = new Point3d(-WORLD_LEN / 2.0f, MAX_HEIGHT, WORLD_LEN / 2.0f);

        Point3d point3d5 = new Point3d(WORLD_LEN / 2.0f, MIN_HEIGHT, WORLD_LEN / 2.0f);
        Point3d point3d6 = new Point3d(WORLD_LEN / 2.0f, MAX_HEIGHT, WORLD_LEN / 2.0f);

        Point3d point3d7 = new Point3d(WORLD_LEN / 2.0f, MIN_HEIGHT, -WORLD_LEN / 2.0f);
        Point3d point3d8 = new Point3d(WORLD_LEN / 2.0f, MAX_HEIGHT, -WORLD_LEN / 2.0f);

        landBG.addChild(new ColouredPlane(point3d3, point3d1, point3d2, point3d4, new Vector3f(-1, 0, 0), eveningBlue));
        landBG.addChild(new ColouredPlane(point3d5, point3d3, point3d4, point3d6, new Vector3f(0, 0, -1), eveningBlue));
        landBG.addChild(new ColouredPlane(point3d7, point3d5, point3d6, point3d8, new Vector3f(-1, 0, 0), eveningBlue));
        landBG.addChild(new ColouredPlane(point3d7, point3d8, point3d2, point3d1, new Vector3f(0, 0, 1), eveningBlue));
    }

    public BranchGroup getLandBG() {
        return landBG;
    }

    public boolean inLandscape(double xPosn, double zPosn) {
        int x = (int) Math.round(xPosn);
        int z = (int) Math.round(zPosn);

        if ((x <= -WORLD_LEN / 2) || (x >= WORLD_LEN / 2) || (z <= -WORLD_LEN / 2) || (z >= WORLD_LEN / 2)) {
            return false;
        }
        return true;
    }

    public Vector3d getOriginVector3d() {
        return originVector3d;
    }

    public double getLandHeight(double x, double z, double currHeight) {
        Point3d pickStart = new Point3d(x, MAX_HEIGHT * 2, z);
        picker.setShapeRay(pickStart, DOWN_VEC);

        PickResult pickResult = picker.pickClosest();
        if (pickResult != null) {
            if (pickResult.numIntersections() != 0) {
                PickIntersection pickIntersection = pickResult.getIntersection(0);
                Point3d nextPoint3d;
                try {
                    nextPoint3d = pickIntersection.getPointCoordinates();
                } catch (Exception e) {
                    return currHeight;
                }
                return nextPoint3d.y;
            }
        }
        return currHeight;
    }
}
