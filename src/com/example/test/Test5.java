package com.example.test;

import com.sun.j3d.utils.timer.J3DTimer;

public class Test5 {
    public static void main(String[] args) {
        long t1 = J3DTimer.getValue();
        long t2 = J3DTimer.getValue();
        long diff = t2 - t1;

        System.out.println("t1 : " + t1);
        System.out.println("t2 : " + t2);
        System.out.println("diff : " + diff);
    }
}
