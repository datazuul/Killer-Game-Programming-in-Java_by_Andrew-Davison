package com.example.shooter3D;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

public class GunTurret {

    private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);

    private BranchGroup gunBG;
    private TransformGroup gunTG;
    private Vector3d startVec;

    private Transform3D gunTransform3D = new Transform3D();
    private Vector3d currTrans = new Vector3d();
    private Transform3D rotTransform3D = new Transform3D();

    public GunTurret(Vector3d svec) {
        startVec = svec;
        gunBG = new BranchGroup();
        Appearance apStone = stoneApp();
        placeGunBase(apStone);
        placeGun(apStone);
    }

    private Appearance stoneApp() {
        Material stoneMat = new Material();
        stoneMat.setLightingEnable(true);

        Appearance apStone = new Appearance();
        apStone.setMaterial(stoneMat);

        TextureLoader stoneTex = new TextureLoader("/media/mahedi/4ACA850ECA84F789/FALSE/WORKSPACE/JAVA/Killer Game Programming in Java_Andrew Davison/src/com/example/shooter3D/images/stone.jpg", null);
        if (stoneTex != null) {
            apStone.setTexture(stoneTex.getTexture());
        }

        TextureAttributes textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.MODULATE);
        apStone.setTextureAttributes(textureAttributes);

        return apStone;
    }

    private void placeGunBase(Appearance apStone) {
        Transform3D baseTransform3D = new Transform3D();
        baseTransform3D.set(new Vector3d(0, 1, 0));
        TransformGroup baseTG = new TransformGroup();
        baseTG.setTransform(baseTransform3D);

        Cylinder cylinder = new Cylinder(0.25f, 2.0f, Cylinder.GENERATE_NORMALS |
                Cylinder.GENERATE_TEXTURE_COORDS, apStone);
        cylinder.setPickable(false);
        baseTG.addChild(cylinder);

        gunBG.addChild(baseTG);
    }

    private void placeGun(Appearance apStone) {
        gunTG = new TransformGroup();
        gunTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        gunTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        gunTransform3D.set(startVec);
        gunTG.setTransform(gunTransform3D);
        Cone cone = new Cone(1.0f, 2.0f, Cone.GENERATE_NORMALS | Cone.GENERATE_TEXTURE_COORDS, apStone);
        cone.setPickable(false);
        gunTG.addChild(cone);

        gunBG.addChild(gunTG);
    }

    public BranchGroup getGunBG() {
        return gunBG;
    }

    public void makeRotation(AxisAngle4d rotAxisAngle4d) {
        gunTG.getTransform(gunTransform3D);
        gunTransform3D.get(currTrans);
        gunTransform3D.setTranslation(ORIGIN);

        rotTransform3D.setRotation(rotAxisAngle4d);
        gunTransform3D.mul(rotTransform3D);

        gunTransform3D.setTranslation(currTrans);
        gunTG.setTransform(gunTransform3D);
    }
}
