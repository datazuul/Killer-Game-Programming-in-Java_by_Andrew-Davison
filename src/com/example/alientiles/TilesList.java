package com.example.alientiles;

import java.awt.*;
import java.util.ArrayList;

public class TilesList {

    protected ArrayList nodes;

    public TilesList(TileNode node) {
        nodes = new ArrayList();
        nodes.add(node);
    }

    public TilesList() {
        nodes = new ArrayList();
    }

    public void add(TileNode node) {
        nodes.add(node);
    }

    public TileNode findNode(Point p) {
        TileNode entry;
        for (int i = 0; i < nodes.size(); i++) {
            entry = (TileNode) nodes.get(i);
            if ((entry.getPoint()).equals(p)) {
                return entry;
            }
        }
        return null;
    }

    public boolean delete(Point p) {
        Point entry;
        for (int i = 0; i < nodes.size(); i++) {
            entry = ((TileNode) nodes.get(i)).getPoint();
            if (entry.equals(p)) {
                nodes.remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return nodes.size();
    }
}
