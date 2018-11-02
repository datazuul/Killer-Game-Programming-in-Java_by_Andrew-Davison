package com.example.fpshooter3D;

import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

public class AmmoManager {

    private static final int NUMBEAMS = 20;
    private LaserShot[] shots;

    public AmmoManager(TransformGroup steerTG, BranchGroup sceneBG, Vector3d targetVector3d) {
        ImageComponent2D[] exploIms = loadImages("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/fpshooter3D/explo/explo", 6);

        shots = new LaserShot[NUMBEAMS];
        for (int i = 0; i < NUMBEAMS; i++) {
            shots[i] = new LaserShot(steerTG, exploIms, targetVector3d);
            sceneBG.addChild(shots[i].getBeamTG());
        }
    }

    public void fireBeam() {
        for (int i = 0; i < NUMBEAMS; i++) {
            if (shots[i].requestFiring()) {
                return;
            }
        }
    }

    private ImageComponent2D[] loadImages(String fNms, int numIms) {
        String filename;
        TextureLoader textureLoader;
        ImageComponent2D[] imageComponent2DS = new ImageComponent2D[numIms];
        System.out.println("Loading " + numIms + " textures from " + fNms);
        for (int i = 0; i < numIms; i++) {
            filename = new String(fNms + i + ".gif");
            textureLoader = new TextureLoader(filename, null);
            imageComponent2DS[i] = textureLoader.getImage();
            if (imageComponent2DS[i] == null) {
                System.out.println("Load failed for texture in : " + filename);
            }
        }
        return imageComponent2DS;
    }
}
