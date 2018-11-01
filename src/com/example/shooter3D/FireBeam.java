package com.example.shooter3D;

import javax.vecmath.Point3d;

public class FireBeam extends Thread {

    private Point3d intercept;
    private ShootingBehaviour shootingBehaviour;
    private LaserBeam laserBeam;
    private ExplosionsClip explosionsClip;
    private double turnAngle;

    public FireBeam(Point3d intercept, ShootingBehaviour shootingBehaviour, LaserBeam laserBeam,
                    ExplosionsClip explosionsClip, double turnAngle) {
        this.intercept = intercept;
        this.shootingBehaviour = shootingBehaviour;
        this.laserBeam = laserBeam;
        this.explosionsClip = explosionsClip;
        this.turnAngle = turnAngle;
    }

    public void run() {
        laserBeam.shootBeam(intercept);
        shootingBehaviour.setFinishedShot();
        explosionsClip.showExplosion(turnAngle, intercept);
    }
}
