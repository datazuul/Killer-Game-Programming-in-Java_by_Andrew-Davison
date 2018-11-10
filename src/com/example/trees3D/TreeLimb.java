package com.example.trees3D;

import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class TreeLimb {

    private static final double OVERLAP = 0.1;
    private static final int MAX_COLOUR_STEP = 15;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f green = new Color3f(0.0f, 1.0f, 0.1f);
    private static final Color3f brown = new Color3f(0.35f, 0.29f, 0.0f);

    private float redShift = (brown.x - green.x) / ((float) MAX_COLOUR_STEP);
    private float greenShift = (brown.y - green.y) / ((float) MAX_COLOUR_STEP);
    private float blueShift = (brown.z - green.z) / ((float) MAX_COLOUR_STEP);

    private int orientAxis;
    private double orientAngle = 0;

    private float radius;
    private float limbLen;

    private TreeLimb parent;

    private TransformGroup scaleTG;
    private TransformGroup endLimbTG;
    private Material limbMaterial;

    private Transform3D currTransform3D, toMoveTransform3D;
    private Vector3d endPos;

    private Vector3d scaleLimb;
    private Color3f currColor3f;
    private int colourStep = 0;
    private int age = 0;

    private int numChildren = 0;
    private ArrayList limbChildren;
    private int level;

    private boolean hasLeaves;
    private ImageCsSeries frontLeafShape, backLeafShape;

    public TreeLimb(int axis, double angle, float radius, float limbLen, TransformGroup startLimbTG, TreeLimb parent) {
        orientAxis = axis;
        orientAngle = angle;
        this.radius = radius;
        this.limbLen = limbLen;
        this.parent = parent;

        scaleLimb = new Vector3d(1, 1, 1);
        currColor3f = new Color3f(green);

        limbChildren = new ArrayList();

        if (parent == null) {
            level = 1;
        } else {
            level = parent.getLevel() + 1;
        }

        hasLeaves = false;
        frontLeafShape = null;
        backLeafShape = null;

        currTransform3D = new Transform3D();
        toMoveTransform3D = new Transform3D();
        endPos = new Vector3d();

        buildSubgraph(startLimbTG);

        if (parent != null) {
            parent.addChildLimb(this);
        }
    }

    private void buildSubgraph(TransformGroup startLimbTG) {
        BranchGroup startBG = new BranchGroup();

        TransformGroup orientTG = new TransformGroup();
        if (orientAngle != 0) {
            Transform3D transform3D = new Transform3D();
            if (orientAxis == X_AXIS) {
                transform3D.rotX(Math.toRadians(orientAngle));
            } else if (orientAxis == Y_AXIS) {
                transform3D.rotY(Math.toRadians(orientAngle));
            } else {
                transform3D.rotZ(Math.toRadians(orientAngle));
            }
            orientTG.setTransform(transform3D);
        }

        scaleTG = new TransformGroup();
        scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        startBG.addChild(orientTG);
        orientTG.addChild(scaleTG);
        scaleTG.addChild(makeLimb());

        TransformGroup endLimbTG = locateEndLimb();
        orientTG.addChild(endLimbTG);

        startBG.compile();

        startLimbTG.addChild(startBG);
    }

    private TransformGroup makeLimb() {
        TransformGroup baseTG = new TransformGroup();
        Transform3D transform3D1 = new Transform3D();
        transform3D1.setTranslation(new Vector3d(0, limbLen / 2, 0));
        baseTG.setTransform(transform3D1);

        Appearance appearance = new Appearance();
        limbMaterial = new Material(black, black, green, brown, 50.0f);
        limbMaterial.setCapability(Material.ALLOW_COMPONENT_READ);
        limbMaterial.setCapability(Material.ALLOW_COMPONENT_WRITE);

        limbMaterial.setLightingEnable(true);

        appearance.setMaterial(limbMaterial);
        Cylinder cylinder = new Cylinder(radius, limbLen, appearance);

        baseTG.addChild(cylinder);
        return baseTG;
    }

    private TransformGroup locateEndLimb() {
        endLimbTG = new TransformGroup();
        endLimbTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        endLimbTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        endLimbTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Transform3D transform3D2 = new Transform3D();
        transform3D2.setTranslation(new Vector3d(0, limbLen * (1.0 - OVERLAP), 0));
        endLimbTG.setTransform(transform3D2);

        return endLimbTG;
    }

    public TransformGroup getEndLimbTG() {
        return endLimbTG;
    }

    public void incrAge() {
        age++;
    }

    public int getAge() {
        return age;
    }

    public int getLevel() {
        return level;
    }

    public double getScaleLength() {
        return scaleLimb.y;
    }

    public double getScaleRadius() {
        return scaleLimb.x;
    }

    public void scaleLength(double yChange) {
        scaleLimb.y *= yChange;
        applyScale();
    }

    public void scaleRadius(double radChange) {
        scaleLimb.x *= radChange;
        scaleLimb.z *= radChange;
        applyScale();
    }

    private void applyScale() {
        moveEndLimbTG(scaleLimb.y);

        scaleTG.getTransform(currTransform3D);
        currTransform3D.setScale(scaleLimb);
        scaleTG.setTransform(currTransform3D);
    }

    private void moveEndLimbTG(double yScale) {
        endLimbTG.getTransform(currTransform3D);
        currTransform3D.get(endPos);
        double currLimbLen = endPos.y;
        double changedLen = ((double) limbLen * (1.0 - OVERLAP) * yScale) - currLimbLen;

        endPos.set(0, changedLen, 0);
        toMoveTransform3D.setTranslation(endPos);
        currTransform3D.mul(toMoveTransform3D);
        endLimbTG.setTransform(currTransform3D);
    }

    public float getRadius() {
        return (radius * (float) scaleLimb.x);
    }

    public void setRadius(float newRadius) {
        double scaledRadius = ((double) radius) * scaleLimb.x;
        double radChange = ((double) newRadius) / scaledRadius;
        scaleRadius(radChange);
    }

    public float getLength() {
        return (limbLen * (float) scaleLimb.y);
    }

    public void setLength(float newLimbLen) {
        double scaledLimbLen = ((double) limbLen) * scaleLimb.y;
        double lenChange = ((double) newLimbLen) / scaledLimbLen;
        scaleLength(lenChange);
    }

    public Color3f getCurrColor3f() {
        return currColor3f;
    }

    public void setCurrColor3f(Color3f color3f) {
        currColor3f.x = color3f.x;
        currColor3f.y = color3f.y;
        currColor3f.z = color3f.z;
        limbMaterial.setDiffuseColor(currColor3f);
    }

    public void stepToBrown() {
        if (colourStep <= MAX_COLOUR_STEP) {
            currColor3f.x += redShift;
            currColor3f.y += greenShift;
            currColor3f.z += blueShift;
            limbMaterial.setDiffuseColor(currColor3f);
            colourStep++;
        }
    }

    public TreeLimb getParent() {
        return parent;
    }

    public int getNumChildren() {
        return numChildren;
    }

    public void addChildLimb(TreeLimb child) {
        limbChildren.add(child);
        numChildren++;
    }

    public ArrayList getLimbChildren() {
        return limbChildren;
    }

    public boolean isHasLeaves() {
        return hasLeaves;
    }

    public void addLeaves(ImageCsSeries frontLeafShape, ImageCsSeries backLeafShape) {
        if (!hasLeaves) {
            this.frontLeafShape = frontLeafShape;
            this.backLeafShape = backLeafShape;

            BranchGroup leafBG1 = new BranchGroup();
            leafBG1.addChild(this.frontLeafShape);
            endLimbTG.addChild(leafBG1);

            BranchGroup leafBG2 = new BranchGroup();
            leafBG2.addChild(this.backLeafShape);
            endLimbTG.addChild(leafBG2);

            hasLeaves = true;
        }
    }

    public void showLeaf(int i) {
        if (hasLeaves) {
            frontLeafShape.showImage(i);
            backLeafShape.showImage(i);
        }
    }

    public void showNextLeaf() {
        if (hasLeaves) {
            frontLeafShape.showNext();
            backLeafShape.showNext();
        }
    }

    public void showPrevLeaf() {
        if (hasLeaves) {
            frontLeafShape.showPrev();
            backLeafShape.showPrev();
        }
    }
}
