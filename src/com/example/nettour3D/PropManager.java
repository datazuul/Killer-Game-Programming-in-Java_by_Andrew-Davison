package com.example.nettour3D;

import com.sun.j3d.loaders.Scene;
import ncsa.j3d.loaders.ModelLoader;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PropManager {

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;
    private static final int INCREMENT = 0;
    private static final int DECREMENT = 1;

    private static final double MOVE_INCREMENT = 0.1;
    private static final double ROTATION_INCREMENT = 10;
    private static final double ROTATION_AMOUNT = Math.toRadians(ROTATION_INCREMENT);

    private TransformGroup moveTG, rotationTG, scaleTG;
    private Transform3D transform3D;
    private Transform3D changeT3D;

    private String fileName;
    private double xRotation, yRotation, zRotation;
    private ArrayList rotationInfoAL;
    private double scale;

    private DecimalFormat decimalFormat;

    public PropManager(String fileName, boolean hasCoordinatesInfo) {
        this.fileName = fileName;
        xRotation = 0.0;
        yRotation = 0.0;
        zRotation = 0.0;
        rotationInfoAL = new ArrayList();
        scale = 1.0;

        transform3D = new Transform3D();
        changeT3D = new Transform3D();

        decimalFormat = new DecimalFormat("0.###");

        loadFile(fileName);
        if (hasCoordinatesInfo) {
            getFileCoordinates(fileName);
        }
    }

    private void loadFile(String fileName) {
        System.out.println("Loading object file : models/" + fileName);

        Scene scene = null;
        ModelLoader modelLoader = new ModelLoader();
        try {
            scene = modelLoader.load("models/" + fileName);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        BranchGroup branchGroup = scene.getSceneGroup();

        TransformGroup objectBoundsTG = new TransformGroup();
        objectBoundsTG.addChild(branchGroup);

        String extension = getExtension(fileName);
        BoundingSphere boundingSphere = (BoundingSphere) branchGroup.getBounds();
        setBSPosition(objectBoundsTG, boundingSphere.getRadius(), extension);

        scaleTG = new TransformGroup();
        scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        scaleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        scaleTG.addChild(objectBoundsTG);

        rotationTG = new TransformGroup();
        rotationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        rotationTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        rotationTG.addChild(scaleTG);

        moveTG = new TransformGroup();
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        moveTG.addChild(rotationTG);
    }

    private String getExtension(String fileName) {
        int dotPosition = fileName.lastIndexOf(".");
        if (dotPosition == -1) {
            return "(none)";
        } else {
            return fileName.substring(dotPosition + 1).toLowerCase();
        }
    }

    private void setBSPosition(TransformGroup objectBoundsTG, double radius, String extension) {
        Transform3D objectT3D = new Transform3D();
        objectBoundsTG.getTransform(objectT3D);

        Transform3D scaleT3D = new Transform3D();
        double scaleFactor = 1.0 / radius;
        scaleT3D.setScale(scaleFactor);

        objectT3D.mul(scaleT3D);

        if (extension.equals("3ds")) {
            Transform3D rotationT3D = new Transform3D();
            rotationT3D.rotX(-Math.PI / 2.0);
            objectT3D.mul(rotationT3D);
        }

        objectBoundsTG.setTransform(objectT3D);
    }

    public TransformGroup getMoveTG() {
        return moveTG;
    }

    private void getFileCoordinates(String fileName) {
        String coordinateFile = "models/" + getName(fileName) + "Coords.txt";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(coordinateFile));
            bufferedReader.readLine();
            String line;
            char ch;
            while ((line = bufferedReader.readLine()) != null) {
                ch = line.charAt(1);
                if (ch == 'p') {
                    setCurrentPosition(line);
                } else if (ch == 'r') {
                    setCurrentRotation(line);
                } else if (ch == 's') {
                    setCurrentScale(line);
                } else {
                    System.out.println(coordinateFile + " : did not recognise line : " + line);
                }
            }
            bufferedReader.close();
            System.out.println("Read in coordinates file : " + coordinateFile);
        } catch (IOException e) {
            System.out.println("Error reading coordinates file : " + coordinateFile);
            System.out.println(e);
            System.exit(1);
        }
    }

    private String getName(String fileName) {
        int dotPosition = fileName.lastIndexOf(".");
        if (dotPosition == -1) {
            return fileName;
        } else {
            return fileName.substring(0, dotPosition);
        }
    }

    private void setCurrentPosition(String line) {
        double values[] = new double[3];
        values[0] = 0;
        values[1] = 0;
        values[2] = 0;

        StringTokenizer tokenizer = new StringTokenizer(line);
        String token = tokenizer.nextToken();
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            try {
                values[count] = Double.parseDouble(token);
                count++;
            } catch (NumberFormatException e) {
                System.out.println("Incorrect format for position data in coordinates file");
                break;
            }
        }
        if (count != 3) {
            System.out.println("Insufficient position data in coordinates file");
        }
        doMove(new Vector3d(values[0], values[1], values[2]));
    }

    private void setCurrentRotation(String line) {
        int rotationNumber;
        StringTokenizer tokenizer = new StringTokenizer(line);
        String token = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            return;
        }
        token = tokenizer.nextToken();
        for (int i = 0; i < token.length(); i++) {
            try {
                rotationNumber = Character.digit(token.charAt(i), 10);
            } catch (NumberFormatException e) {
                System.out.println(e);
                break;
            }
            if (rotationNumber == 1) {
                rotate(X_AXIS, INCREMENT);
            } else if (rotationNumber == 2) {
                rotate(X_AXIS, DECREMENT);
            } else if (rotationNumber == 3) {
                rotate(Y_AXIS, INCREMENT);
            } else if (rotationNumber == 4) {
                rotate(Y_AXIS, DECREMENT);
            } else if (rotationNumber == 5) {
                rotate(Z_AXIS, INCREMENT);
            } else if (rotationNumber == 6) {
                rotate(Z_AXIS, DECREMENT);
            } else {
                System.out.println("Did not recognise");
            }
        }
    }

    private void setCurrentScale(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        String token = tokenizer.nextToken();
        double startScale;

        token = tokenizer.nextToken();
        try {
            startScale = Double.parseDouble(token);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect format of data");
            startScale = 1.0;
        }
        if (startScale != 1.0) {
            scale(startScale);
        }
    }

    public void move(int axis, int change) {
        double moveStep = (change == INCREMENT) ? MOVE_INCREMENT : -MOVE_INCREMENT;
        Vector3d vector3d;
        if (axis == X_AXIS) {
            vector3d = new Vector3d(moveStep, 0, 0);
        } else if (axis == Y_AXIS) {
            vector3d = new Vector3d(0, moveStep, 0);
        } else {
            vector3d = new Vector3d(0, 0, moveStep);
        }
        doMove(vector3d);
    }

    private void doMove(Vector3d vector3d) {
        moveTG.getTransform(transform3D);
        changeT3D.setIdentity();
        changeT3D.setTranslation(vector3d);
        transform3D.mul(changeT3D);
        moveTG.setTransform(transform3D);
    }

    public void rotate(int axis, int change) {
        doRotate(axis, change);
        storeRotate(axis, change);
    }

    private void doRotate(int axis, int change) {
        double radians = (change == INCREMENT) ? ROTATION_AMOUNT : -ROTATION_AMOUNT;
        rotationTG.getTransform(transform3D);
        changeT3D.setIdentity();
        switch (axis) {
            case X_AXIS:
                changeT3D.rotX(radians);
                break;
            case Y_AXIS:
                changeT3D.rotY(radians);
                break;
            case Z_AXIS:
                changeT3D.rotZ(radians);
                break;
            default:
                System.out.println("Unknown axis");
                break;
        }
        transform3D.mul(changeT3D);
        rotationTG.setTransform(transform3D);
    }

    private void storeRotate(int axis, int change) {
        double degrees = (change == INCREMENT) ? ROTATION_INCREMENT : -ROTATION_INCREMENT;
        switch (axis) {
            case X_AXIS:
                storeRotateX(degrees);
                break;
            case Y_AXIS:
                storeRotateY(degrees);
                break;
            case Z_AXIS:
                storeRotateZ(degrees);
                break;
            default:
                System.out.println("Unknown storage axis");
                break;
        }
    }

    private void storeRotateX(double degrees) {
        xRotation = (xRotation + degrees) % 360;
        if (degrees == ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(1));
        } else if (degrees == -ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(2));
        } else {
            System.out.println("No X-axis rotation number for " + degrees);
        }
    }

    private void storeRotateY(double degrees) {
        yRotation = (yRotation + degrees) % 360;
        if (degrees == ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(3));
        } else if (degrees == -ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(4));
        } else {
            System.out.println("No y-axis rotation number for " + degrees);
        }
    }

    private void storeRotateZ(double degrees) {
        zRotation = (zRotation + degrees) % 360;
        if (degrees == ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(5));
        } else if (degrees == -ROTATION_INCREMENT) {
            rotationInfoAL.add(new Integer(6));
        } else {
            System.out.println("No Z-axis rotation number for " + degrees);
        }
    }

    public void scale(double d) {
        scaleTG.getTransform(transform3D);
        changeT3D.setIdentity();
        changeT3D.setScale(d);
        transform3D.mul(changeT3D);
        scaleTG.setTransform(transform3D);

        scale *= d;
    }

    public Vector3d getLocation() {
        moveTG.getTransform(transform3D);
        Vector3d vector3d = new Vector3d();
        transform3D.get(vector3d);
        return vector3d;
    }

    public Point3d getRotations() {
        return new Point3d(xRotation, yRotation, zRotation);
    }

    public double getScale() {
        return scale;
    }

    public void saveCoordinateFile() {
        String coordinateFileName = "models/" + getName(fileName) + "Coords.txt";
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(coordinateFileName));
            printWriter.println(fileName);
            Vector3d vector3d = getLocation();
            printWriter.println("-p " + decimalFormat.format(vector3d.x) + " " + decimalFormat.format(vector3d.y) +
                    " " + decimalFormat.format(vector3d.z));
            printWriter.print("-r ");
            for (int i = 0; i < rotationInfoAL.size(); i++) {
                printWriter.print("" + ((Integer) rotationInfoAL.get(i)).intValue());
            }
            printWriter.println("");

            printWriter.println("-s " + decimalFormat.format(scale));

            printWriter.close();
            System.out.println("Saved to coordinate file : " + coordinateFileName);
        } catch (IOException e) {
            System.out.println("Error writing to coordinate file : " + coordinateFileName);
        }
    }

    private void printTG(TransformGroup transformGroup, String id) {
        Transform3D transform3D = new Transform3D();
        transformGroup.getTransform(transform3D);
        Vector3d vector3d = new Vector3d();
        transform3D.get(vector3d);
        printTuple(vector3d, id);
    }

    private void printTuple(Tuple3d tuple3d, String id) {
        System.out.println(id + " x : " + decimalFormat.format(tuple3d.x) + ", " + id + " y : " +
                decimalFormat.format(tuple3d.y) + ", " + id + " z : " + decimalFormat.format(tuple3d.z));
    }
}
