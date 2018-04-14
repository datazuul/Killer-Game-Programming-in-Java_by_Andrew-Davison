package com.example.alientiles;

import java.awt.*;
import java.util.ArrayList;

public class TileNode {

    private Point coord;
    private double costFromStart;
    private double costToGoal;

    private TileNode parent;

    public TileNode(Point p) {
        coord = p;
        parent = null;
        costFromStart = 0.0;
    }

    public double getCostFromStart() {
        return costFromStart;
    }

    public void setCostFromStart(double v) {
        costFromStart = v;
    }

    public void costToGoal(Point goal) {
        double dist = coord.distance(goal.x, goal.y);
        costToGoal = Math.floor(dist);
    }

    public double getScore() {
        return costFromStart + costToGoal;
    }

    public Point getPoint() {
        return coord;
    }

    public void setParent(TileNode p) {
        parent = p;
    }

    public TileNode getParent() {
        return parent;
    }

    public TileNode makeNeighbour(int quad, WorldDisplay wd) {
        TileNode newNode;
        int x = coord.x;
        int y = coord.y;
        if (quad == TiledSprite.NE) {
            newNode = ((y % 2 == 0) ? makeNode(x, y - 1, wd) : makeNode(x + 1, y - 1, wd));
        } else if (quad == TiledSprite.SE) {
            newNode = ((y % 2 == 0) ? makeNode(x, y + 1, wd) : makeNode(x + 1, y + 1, wd));
        } else if (quad == TiledSprite.SW) {
            newNode = ((y % 2 == 0) ? makeNode(x - 1, y + 1, wd) : makeNode(x, y + 1, wd));
        } else if (quad == TiledSprite.NW) {
            newNode = ((y % 2 == 0) ? makeNode(x - 1, y - 1, wd) : makeNode(x, y - 1, wd));
        } else {
            System.out.println("makeNeighbour() error");
            newNode = null;
        }
        return newNode;
    }

    private TileNode makeNode(int x, int y, WorldDisplay wd) {
        if (!wd.validTileLoc(x, y)) {
            return null;
        }
        TileNode newNode = new TileNode(new Point(x, y));
        newNode.setCostFromStart(getCostFromStart() + 1.0);
        newNode.setParent(this);
        return newNode;
    }

    public ArrayList buildPath() {
        ArrayList path = new ArrayList();
        path.add(coord);
        TileNode temp = parent;
        while (temp != null) {
            path.add(0, temp.getPoint());
            temp = temp.getParent();
        }
        path.remove(0);
        return path;
    }
}
