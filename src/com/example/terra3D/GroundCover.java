package com.example.terra3D;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GroundCover {

    private ArrayList coords;
    private BranchGroup coverBG;

    public GroundCover(String fnm) {
        coords = new ArrayList();
        coverBG = new BranchGroup();

        loadCoverInfo(fnm);
    }

    private void loadCoverInfo(String fnm) {
        String gcFile = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + fnm + "GC.txt");
        System.out.println("Loading ground cover file : " + gcFile);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(gcFile));
            String line;

            if ((line = bufferedReader.readLine()) != null) {
                loadObj(fnm, line);
            } else {
                System.out.println(gcFile + " is empty!!!");
                return;
            }

            StringTokenizer tokenizer;
            String gcFnm;
            int numItems = 0;
            while ((line = bufferedReader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);
                gcFnm = tokenizer.nextToken();
                double scale = Double.parseDouble(tokenizer.nextToken());
                int gcNo = Integer.parseInt(tokenizer.nextToken());
                loadGC(gcFnm, scale, gcNo);
                numItems++;
            }
            System.out.println(numItems + " ground cover types");
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadObj(String fnm, String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        tokenizer.nextToken();

        if (tokenizer.hasMoreTokens()) {
            try {
                double min = Double.parseDouble(tokenizer.nextToken());
                double max = Double.parseDouble(tokenizer.nextToken());
                readObj(fnm, min, max, true);
                System.out.println("Ground cover ranges -- min : " + min + "; max : " + max);
                return;
            } catch (NumberFormatException e) {
                System.out.println("min/max values must be double");
            }
        }
        readObj(fnm, 0, 0, false);
    }

    private void readObj(String fnm, double min, double max, boolean hasRange) {
        String objFile = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + fnm + ".obj");
        System.out.println("Loading terrain mesh file : " + objFile);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(objFile));
            String line, token;
            StringTokenizer tokenizer;
            double xCoord, yCoord, zCoord;
            int numExcluded = 0;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("v")) {
                    tokenizer = new StringTokenizer(line);
                    tokenizer.nextToken();
                    xCoord = Double.parseDouble(tokenizer.nextToken());
                    yCoord = Double.parseDouble(tokenizer.nextToken());
                    zCoord = Double.parseDouble(tokenizer.nextToken());

                    if (hasRange) {
                        if ((zCoord >= min) && (zCoord <= max)) {
                            coords.add(new Vector3d(xCoord, yCoord, zCoord));
                        } else {
                            numExcluded++;
                        }
                    } else {
                        if ((xCoord != 0) && (zCoord != 0)) {
                            coords.add(new Vector3d(xCoord, yCoord, zCoord));
                        } else {
                            numExcluded++;
                        }
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Error reading : " + objFile);
        }
    }

    private void loadGC(String gcFnm, double scale, int gcNo) {
        String gcFile = new String("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/terra3D/models/" + gcFnm);
        System.out.println("Loading GC file : " + gcFile + "; scale : " + scale + "; " + gcNo + " copies");

        SharedGroup gcSharedGroup = new SharedGroup();
        gcSharedGroup.addChild(new GroundShape((float) scale, gcFile));
        gcSharedGroup.setPickable(false);

        Vector3d coordVector3d;
        for (int i = 0; i < gcNo; i++) {
            coordVector3d = selectCoord();
            placeCover(gcSharedGroup, coordVector3d);
        }
    }

    private Vector3d selectCoord() {
        int index = (int) Math.floor(Math.random() * coords.size());
        return (Vector3d) coords.get(index);
    }

    private void placeCover(SharedGroup gcSharedGroup, Vector3d coordVector3d) {
        Transform3D transform3D = new Transform3D();
        transform3D.rotX(Math.PI / 2.0);

        TransformGroup rotTG = new TransformGroup(transform3D);
        rotTG.addChild(new Link(gcSharedGroup));

        Transform3D transform3D1 = new Transform3D();
        transform3D1.set(coordVector3d);
        TransformGroup posTG = new TransformGroup(transform3D1);
        posTG.addChild(rotTG);

        coverBG.addChild(posTG);
    }

    public BranchGroup getCoverBG() {
        return coverBG;
    }
}
