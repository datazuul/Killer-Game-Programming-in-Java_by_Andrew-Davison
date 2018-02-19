package com.example.test;

import com.sun.j3d.utils.timer.J3DTimer;

public class Test6 {
    public static void main(String[] args) {
        System.out.println("J3DTimer resolution (ns) : " + J3DTimer.getResolution());
        System.out.println("Current time (ns) : " + J3DTimer.getValue());
    }
}
