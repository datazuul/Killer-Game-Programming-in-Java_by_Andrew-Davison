package com.example.flocking3D;

import java.util.ArrayList;

public class BoidsList extends ArrayList {

    public BoidsList(int num) {
        super(num);
    }

    synchronized public Boid getBoid(int i) {
        if (i < super.size()) {
            return (Boid) get(i);
        }
        return null;
    }

    synchronized public boolean removeBoid(int i) {
        if (i < super.size()) {
            super.remove(i);
            return true;
        }
        return false;
    }
}
