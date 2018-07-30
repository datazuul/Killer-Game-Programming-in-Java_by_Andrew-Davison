package com.example.loaderInfo3D;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import ncsa.j3d.loaders.ModelLoader;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Hashtable;

public class WrapLoaderInfo3D extends JPanel {

    private static final int PWIDTH = 512;
    private static final int PHEIGHT = 512;

    private static final int BOUNDSIZE = 100;
    private static final Point3d USERPOSN = new Point3d(0, 5, 20);

    private static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    private static final Color3f blue = new Color3f(0.6f, 0.6f, 1.0f);

    private static final String EXAMINE_FN = "examObj.txt";
    private static final String TEXTURE_FN = "models/stone.jpg";

    private SimpleUniverse simpleUniverse;
    private BranchGroup sceneBG;
    private BoundingSphere bounds;

    private FileWriter ofw;
    private DecimalFormat decimalFormat;

    private Scene loadedScene = null;
    private BranchGroup loadedBG = null;

    private int adaptNo;
    private Texture2D texture2D = null;

    public WrapLoaderInfo3D(String fn, int adaptNo) {
        this.adaptNo = adaptNo;
        setLayout(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

        GraphicsConfiguration configuration = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(configuration);
        add("Center", canvas3D);
        canvas3D.setFocusable(true);
        canvas3D.requestFocus();

        simpleUniverse = new SimpleUniverse(canvas3D);

        createSceneGraph(fn);
        initUserPosition();
        orbitControls(canvas3D);

        simpleUniverse.addBranchGraph(sceneBG);
    }

    private void createSceneGraph(String fn) {
        sceneBG = new BranchGroup();
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);
        decimalFormat = new DecimalFormat("0.###");

        lightScene();
        addBackground();
        sceneBG.addChild(new CheckerFloor().getBG());

        loadModel(fn);

        if (loadedScene != null) {
            showNamedObject(loadedScene);
            storeGraphInfo(loadedBG);
            adjustShapes(loadedBG);
        }

        sceneBG.compile();
    }

    private void lightScene() {
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

    private void loadModel(String fn) {
        FileWriter ofw = null;
        System.out.println("Loading : " + fn);

        try {
            ModelLoader loader = new ModelLoader(); // the NCSA portfolio loader
            loadedScene = loader.load(fn);

            if (loadedScene != null) {
                loadedBG = loadedScene.getSceneGroup();

                Transform3D transform3D = new Transform3D();
                transform3D.rotX(-Math.PI / 2.0);
                Vector3d scaleVec = calcScaleFactor(loadedBG, fn);
                transform3D.setScale(scaleVec);

                TransformGroup transformGroup = new TransformGroup(transform3D);
                transformGroup.addChild(loadedBG);

                sceneBG.addChild(transformGroup);
            } else {
                System.out.println("Load error with : " + fn);
            }
        } catch (IOException ioe) {
            System.err.println("Could not find object file : " + fn);
        }
    }

    private Vector3d calcScaleFactor(BranchGroup loadedBG, String fn) {
        BoundingBox boundingBox = new BoundingBox(loadedBG.getBounds());

        Point3d lower = new Point3d();
        boundingBox.getLower(lower);
        Point3d upper = new Point3d();
        boundingBox.getUpper(upper);

        double max = 0.0;
        if ((upper.x - lower.x) > max) {
            max = (upper.x - lower.x);
        }
        if ((upper.y - lower.y) > max) {
            max = (upper.y - lower.y);
        }
        if ((upper.z - lower.z) > max) {
            max = (upper.z - lower.z);
        }

        double scaleFactor = 10.0 / max;
        System.out.println("max dimension : " + decimalFormat.format(max) + "; scaleFactor : " + decimalFormat.format(scaleFactor));

        if (scaleFactor < 0.0005) {
            scaleFactor = 0.0005;
        }

        return new Vector3d(scaleFactor, scaleFactor, scaleFactor);
    }

    private void showNamedObject(Scene loadedScene) {
        String name;
        Hashtable namedObjects = loadedScene.getNamedObjects();
        Enumeration enumeration = namedObjects.keys();
        if (namedObjects.isEmpty()) {
            System.out.println("No Named Objects");
        } else {
            System.out.println("Named Objects");
            while (enumeration.hasMoreElements()) {
                name = (String) enumeration.nextElement();
                System.out.println(name);
            }
        }
    }

    private void storeGraphInfo(BranchGroup bg) {
        System.out.println("Writing model details to " + EXAMINE_FN);
        try {
            ofw = new FileWriter(EXAMINE_FN);
            examineNode(0, bg);
            ofw.close();
        } catch (IOException ioe) {
            System.err.println("Can't write to " + EXAMINE_FN);
        }
    }

    private void examineNode(int level, Node node) throws IOException {
        if (node instanceof Group) {
            Group group = (Group) node;
            levelPrint(level, "Group : " + group.getClass());

            if (group instanceof TransformGroup) {
                Transform3D transform3D = new Transform3D();
                ((TransformGroup) group).getTransform(transform3D);
                levelPrint(level, transform3D.toString());
            }

            levelPrint(level, group.numChildren() + " children");
            Enumeration enumKids = group.getAllChildren();
            while (enumKids.hasMoreElements()) {
                examineNode(level + 1, (Node) enumKids.nextElement());
            }
        } else if (node instanceof Leaf) {
            levelPrint(level, "Leaf : " + node.getClass());
            if (node instanceof Shape3D) {
                examineShape3D(level, (Shape3D) node);
            }
        } else {
            levelPrint(level, "Node : " + node.getClass());
        }
    }

    private void examineShape3D(int level, Shape3D shape) throws IOException {
        Appearance appearance = shape.getAppearance();
        if (appearance == null) {
            levelPrint(level + 1, "No Appearance Component");
        } else {
            printAppearance(level, appearance);
        }

        int numGeoms = shape.numGeometries();
        if (numGeoms == 0) {
            levelPrint(level + 1, "No Geometry Components");
        } else if (numGeoms == 1) {
            Geometry geometry = shape.getGeometry();
            examineGeometry(level + 1, 1, geometry);
        } else {
            levelPrint(level + 1, "No. of Geometries : " + numGeoms);
            Enumeration enumGeoms = shape.getAllGeometries();
            int i = 1;
            while (enumGeoms.hasMoreElements()) {
                examineGeometry(level + 1, i, (Geometry) enumGeoms.nextElement());
                i++;
            }
        }
        levelPrint(level, "");
    }

    private void printAppearance(int level, Appearance appearance) throws IOException {
        ColoringAttributes coloringAttributes = appearance.getColoringAttributes();
        if (coloringAttributes != null) {
            levelPrint(level, coloringAttributes.toString());
        }
        Material material = appearance.getMaterial();
        if (material != null) {
            levelPrint(level, material.toString());
        }
    }

    private void examineGeometry(int level, int index, Geometry geometry) throws IOException {
        levelPrint(level, "Geometry : " + geometry.getClass());
        if (geometry instanceof GeometryArray) {
            levelPrint(level, "Vertex count : " + ((GeometryArray) geometry).getVertexCount());
        }
    }

    private void levelPrint(int level, String s) throws IOException {
        for (int i = 0; i < level; i++) {
            ofw.write(" ");
        }
        ofw.write(s + "\n");
    }

    private void adjustShapes(Node node) {
        System.out.println("Adjusting shapes...");
        if ((adaptNo == 3) || (adaptNo == 4)) {
            loadTexture(TEXTURE_FN);
        }
        visitNode(node);
    }

    private void loadTexture(String fn) {
        TextureLoader textureLoader = new TextureLoader(fn, null);
        texture2D = (Texture2D) textureLoader.getTexture();
        if (texture2D == null) {
            System.out.println("Can't load texture from " + fn);
        } else {
            System.out.println("Loaded texture from " + fn);
            texture2D.setEnable(true);
        }
    }

    private void visitNode(Node node) {
        if (node instanceof Group) {
            Group group = (Group) node;
            Enumeration enumKids = group.getAllChildren();
            while (enumKids.hasMoreElements()) {
                SceneGraphObject object = (SceneGraphObject) enumKids.nextElement();
                if (object instanceof Node) {
                    visitNode((Node) object);
                }
            }
        } else if (node instanceof Shape3D) {
            adjustShape3D((Shape3D) node);
        }
    }

    private void adjustShape3D(Shape3D shape3D) {
        switch (adaptNo) {
            case 0:
                makeBlue(shape3D);
                break;
            case 1:
                drawOutline(shape3D);
                break;
            case 2:
                makeAlmostTransparent(shape3D);
                break;
            case 3:
                addTexture(shape3D);
                break;
            case 4:
                makeBlue(shape3D);
                addTexture(shape3D);
                break;
            default:
                break;
        }
    }

    private void makeBlue(Shape3D shape3D) {
        Appearance appearance = shape3D.getAppearance();
        Material blueMat = new Material(black, black, blue, white, 20.0f);
        blueMat.setLightingEnable(true);
        appearance.setMaterial(blueMat);
        shape3D.setAppearance(appearance);
    }

    private void drawOutline(Shape3D shape3D) {
        Appearance appearance = shape3D.getAppearance();
        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_LINE);

        appearance.setPolygonAttributes(polygonAttributes);
        shape3D.setAppearance(appearance);
    }

    private void makeAlmostTransparent(Shape3D shape3D) {
        Appearance appearance = shape3D.getAppearance();

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        transparencyAttributes.setTransparency(0.8f);

        appearance.setTransparencyAttributes(transparencyAttributes);
        shape3D.setAppearance(appearance);
    }

    private void addTexture(Shape3D shape3D) {
        if (shape3D.numGeometries() == 1) {
            Geometry geometry = shape3D.getGeometry();
            if (geometry instanceof GeometryArray) {
                addTextureGA(shape3D);
            } else {
                System.out.println("Shape geometry is not a GeometryArray");
            }
        } else {
            System.out.println("Shape has too many geometries");
        }
    }

    private void addTextureGA(Shape3D shape3D) {
        Appearance appearance = shape3D.getAppearance();

        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polygonAttributes);

        appearance.setTexCoordGeneration(stampTexCoords(shape3D));

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTextureAttributes(textureAttributes);

        if (texture2D != null) {
            appearance.setTexture(texture2D);
            shape3D.setAppearance(appearance);
        }
    }

    private TexCoordGeneration stampTexCoords(Shape3D shape3D) {
        BoundingBox boundingBox = new BoundingBox(shape3D.getBounds());
        Point3d lower = new Point3d();
        Point3d upper = new Point3d();
        boundingBox.getLower(lower);
        boundingBox.getUpper(upper);

        double width = upper.x - lower.x;
        double height = upper.y - lower.y;

        Vector4f planeS = new Vector4f((float) (1.0 / width), 0.0f, 0.0f, (float) (-lower.x / width));
        Vector4f planeT = new Vector4f(0.0f, (float) (1.0 / height), 0.0f, (float) (-lower.y / height));

        TexCoordGeneration texCoordGeneration = new TexCoordGeneration();
        texCoordGeneration.setPlaneS(planeS);
        texCoordGeneration.setPlaneT(planeT);

        return texCoordGeneration;
    }
}
