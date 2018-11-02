package com.example.fpshooter3D;

public class AnimBeam extends Thread {

    private LaserShot laserShot;

    public AnimBeam(LaserShot laserShot) {
        this.laserShot = laserShot;
    }

    public void run() {
        laserShot.moveBeam();
    }
}
