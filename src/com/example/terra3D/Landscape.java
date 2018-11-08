package com.example.terra3D;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Landscape {

    private static final double LAND_LEN = 60.0;
    private static final String WALL_PIC = "/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/mountain2Sq.jpg";

    private BranchGroup sceneBG;
    private BranchGroup landBG = null;
    private Shape3D landShape3D = null;

    private double landLength, minHeight, maxHeight;
    private double scaleLen;

    private Vector3d originVector3d = null;

    public Landscape(BranchGroup sceneBG, String fname) {
        this.sceneBG = sceneBG;
        loadMesh(fname);
        getLandShape(landBG);

        PickTool.setCapabilities(landShape3D, PickTool.INTERSECT_COORD);

        getLandDimensions(landShape3D);

        makeScenery(landBG, fname);
        addWalls();

        GroundCover groundCover = new GroundCover(fname);
        landBG.addChild(groundCover.getCoverBG());

        addLandtoScene(landBG);
        addLandTexture(landShape3D, fname);
    }

    private void loadMesh(String fname) {
        FileWriter fileWriter = null;
        String fn = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + fname + ".obj");
        System.out.println("Loading terrain mesh from : " + fn + "...");
        try {
            ObjectFile objectFile = new ObjectFile();
            Scene loadedScene = objectFile.load(fn);

            if (loadedScene == null) {
                System.out.println("Scene not found in : " + fn);
                System.exit(0);
            }

            landBG = loadedScene.getSceneGroup();
            if (landBG == null) {
                System.out.println("No land branch group found");
                System.exit(0);
            }
        } catch (IOException e) {
            System.err.println("Terrain mesh load error : " + fn);
            System.exit(0);
        }
    }

    private void getLandShape(BranchGroup landBG) {
        if (landBG.numChildren() > 1) {
            System.out.println("More than one child in land branch group");
        }

        Node node = landBG.getChild(0);
        if (!(node instanceof Shape3D)) {
            System.out.println("No Shape3D found in land branch group");
            System.exit(0);
        }

        landShape3D = (Shape3D) node;
        if (landShape3D == null) {
            System.out.println("Land Shape3D has no value");
            System.exit(0);
        }

        if (landShape3D.numGeometries() > 1) {
            System.out.println("More than 1 geometry in land branch group");
        }

        Geometry geometry = landShape3D.getGeometry();
        if (!(geometry instanceof GeometryArray)) {
            System.out.println("No Geometry Array found in land Shape3D");
            System.exit(0);
        }
    }

    private void getLandDimensions(Shape3D landShape3D) {
        BoundingBox boundingBox = new BoundingBox(landShape3D.getBounds());
        Point3d lower = new Point3d();
        Point3d upper = new Point3d();
        boundingBox.getLower(lower);
        boundingBox.getUpper(upper);
        System.out.println("lower : " + lower + "\nupper : " + upper);

        if ((lower.y == 0) && (upper.x == upper.y)) {
            //
        } else if ((lower.z == 0) && (upper.x == upper.z)) {
            System.out.println("Error : XZ set as the floor; change to XY in Terragen");
            System.exit(0);
        } else {
            System.out.println("Cannot determine floor axes");
            System.out.println("Y range should == X range, and start at 0");
            System.exit(0);
        }

        landLength = upper.x;
        scaleLen = LAND_LEN / landLength;
        System.out.println("scaleLen : " + scaleLen);
        minHeight = lower.z;
        maxHeight = upper.z;
    }

    private void addLandtoScene(BranchGroup landBG) {
        Transform3D transform3D = new Transform3D();
        transform3D.rotX(-Math.PI / 2.0);
        transform3D.setScale(new Vector3d(scaleLen, scaleLen, scaleLen));
        TransformGroup scaleTG = new TransformGroup(transform3D);
        scaleTG.addChild(landBG);

        Transform3D transform3D1 = new Transform3D();
        transform3D1.set(new Vector3d(-LAND_LEN / 2, 0, LAND_LEN / 2));
        TransformGroup posTG = new TransformGroup(transform3D1);
        posTG.addChild(scaleTG);

        sceneBG.addChild(posTG);
    }

    private void addLandTexture(Shape3D shape3D, String fname) {
        Appearance appearance = shape3D.getAppearance();
        appearance.setTexCoordGeneration(stampTexCoords(shape3D));

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        Texture2D texture2D = loadLandTexture(fname);
        if (texture2D != null) {
            appearance.setTexture(texture2D);
            shape3D.setAppearance(appearance);
        }
    }

    private Texture2D loadLandTexture(String fname) {
        String fn = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + fname + ".jpg");
        TextureLoader textureLoader = new TextureLoader(fn, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();

        if (texture2D == null) {
            System.out.println("Cannot load land texture from " + fn);
        } else {
            System.out.println("loaded land texture from " + fn);
            texture2D.setMagFilter(Texture.BASE_LEVEL_LINEAR);
            texture2D.setEnable(true);
        }
        return texture2D;
    }

    private TexCoordGeneration stampTexCoords(Shape3D shape3D) {
        Vector4f planeS = new Vector4f((float) (1.0 / landLength), 0.0f, 0.0f, 0.0f);
        Vector4f planeT = new Vector4f(0.0f, (float) (1.0 / landLength), 0.0f, 0.0f);

        TexCoordGeneration texCoordGeneration = new TexCoordGeneration();
        texCoordGeneration.setPlaneS(planeS);
        texCoordGeneration.setPlaneT(planeT);

        return texCoordGeneration;
    }

    private void makeScenery(BranchGroup landBG, String fname) {
        boolean startSet = false;
        String sceneryFile = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + fname + ".txt");
        System.out.println("Loading scenery file : " + sceneryFile);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(sceneryFile));
            String line, token, modelFnm;
            StringTokenizer tokenizer;
            double xCoord, yCoord, zCoord, scale;
            PropManager propManager;

            while ((line = bufferedReader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);
                modelFnm = tokenizer.nextToken();
                xCoord = Double.parseDouble(tokenizer.nextToken());
                yCoord = Double.parseDouble(tokenizer.nextToken());
                zCoord = Double.parseDouble(tokenizer.nextToken());

                if (!modelFnm.equals("start")) {
                    scale = Double.parseDouble(tokenizer.nextToken());
                } else {
                    scale = 0.0;
                }

                System.out.println("\nAdding : " + modelFnm + " (" + xCoord + ", " + yCoord + ", " + zCoord +
                        "), scale : " + scale);

                if (modelFnm.equals("start")) {
                    originVector3d = landToWorld(xCoord, yCoord, zCoord);
                    startSet = true;
                } else {
                    propManager = new PropManager(modelFnm, true);
                    placeScenery(landBG, propManager.getMoveTG(), xCoord, yCoord, zCoord, scale);
                }
            }
            bufferedReader.close();
            System.out.println();

            if (!startSet) {
                System.out.println("No starting position specified");
                System.exit(0);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private Vector3d landToWorld(double xCoord, double yCoord, double zCoord) {
        double x = (xCoord * scaleLen) - LAND_LEN / 2;
        double y = zCoord * scaleLen;
        double z = (-yCoord * scaleLen) + LAND_LEN / 2;
        return new Vector3d(x, y, z);
    }

    private void placeScenery(BranchGroup landBG, TransformGroup modelTG, double x, double y, double z, double scale) {
        modelTG.setPickable(false);

        Transform3D transform3D = new Transform3D();
        transform3D.rotX(Math.PI / 2.0);

        transform3D.setScale(new Vector3d(scale, scale, scale));
        TransformGroup scaleTG = new TransformGroup(transform3D);
        scaleTG.addChild(modelTG);

        Transform3D transform3D1 = new Transform3D();
        transform3D1.set(new Vector3d(x, y, z));
        TransformGroup posTG = new TransformGroup(transform3D1);
        posTG.addChild(scaleTG);

        landBG.addChild(posTG);
    }

    private void addWalls() {
        double minH = minHeight * scaleLen;
        double maxH = maxHeight * scaleLen;

        Point3d p1 = new Point3d(-LAND_LEN / 2.0f, minH, -LAND_LEN / 2.0f);
        Point3d p2 = new Point3d(-LAND_LEN / 2.0f, maxH, -LAND_LEN / 2.0f);

        Point3d p3 = new Point3d(-LAND_LEN / 2.0f, minH, LAND_LEN / 2.0f);
        Point3d p4 = new Point3d(-LAND_LEN / 2.0f, maxH, LAND_LEN / 2.0f);

        Point3d p5 = new Point3d(LAND_LEN / 2.0f, minH, LAND_LEN / 2.0f);
        Point3d p6 = new Point3d(LAND_LEN / 2.0f, maxH, LAND_LEN / 2.0f);

        Point3d p7 = new Point3d(LAND_LEN / 2.0f, minH, -LAND_LEN / 2.0f);
        Point3d p8 = new Point3d(LAND_LEN / 2.0f, maxH, -LAND_LEN / 2.0f);

        TextureLoader textureLoader = new TextureLoader(WALL_PIC, null);
        Texture2D texture2D = (Texture2D) textureLoader.getTexture();
        if (texture2D == null) {
            System.out.println("Cannot load wall image from " + WALL_PIC);
        } else {
            System.out.println("Loaded wall image : " + WALL_PIC);
            texture2D.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
        }

        sceneBG.addChild(new TexturedPlane(p3, p1, p2, p4, texture2D));
        sceneBG.addChild(new TexturedPlane(p5, p3, p4, p6, texture2D));
        sceneBG.addChild(new TexturedPlane(p7, p5, p6, p8, texture2D));
        sceneBG.addChild(new TexturedPlane(p1, p7, p8, p2, texture2D));
    }

    public Vector3d getOriginVector3d() {
        return originVector3d;
    }

    public BranchGroup getLandBG() {
        return landBG;
    }

    public double getScaleLen() {
        return scaleLen;
    }

    public boolean inLandscape(double x, double z) {
        Vector3d landVector3d = worldToLand(new Vector3d(x, 0, z));
        if ((landVector3d.x <= 0) || (landVector3d.x >= landLength) || (landVector3d.y <= 0) ||
                (landVector3d.y >= landLength)) {
            return false;
        }
        return true;
    }

    public Vector3d worldToLand(Vector3d worldVector3d) {
        double xCoord = (worldVector3d.x + LAND_LEN / 2) / scaleLen;
        double yCoord = (-worldVector3d.z + LAND_LEN / 2) / scaleLen;
        double zCoord = worldVector3d.y / scaleLen;
        return new Vector3d(xCoord, yCoord, zCoord);
    }
}
