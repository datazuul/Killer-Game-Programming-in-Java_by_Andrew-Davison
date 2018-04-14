package com.example.alientiles;

import java.awt.*;
import java.util.ArrayList;

public class AlienAStarSprite extends AlienSprite {

    private static final int MAX_MOVES = 5;

    private int numPlayerMoves = 0;

    private ArrayList path;
    private int pathIndex = 0;

    public AlienAStarSprite(int x, int y, int w, int h, ImagesLoader imsLd, WorldDisplay wd) {
        super(x, y, w, h, imsLd, wd);
        path = new ArrayList();
    }

    public void playerHasMoved(Point playerLoc) {
        if (numPlayerMoves == 0) {
            calcNewPath(playerLoc);
        } else {
            numPlayerMoves = (numPlayerMoves + 1) % MAX_MOVES;
        }
    }

    private void calcNewPath(Point playerLoc) {
        path = aStarSearch(getTileLoc(), playerLoc);
        pathIndex = 0;
    }

    private void printPath() {
        System.out.println("path : ");
        Point p;
        for (int i = 0; i < path.size(); i++) {
            p = (Point) path.get(i);
            System.out.print("( " + p.x + ", " + p.y + ")");
        }
        System.out.println();
    }

    protected void move() {
        if (pathIndex == path.size()) {
            calcNewPath(world.getPlayerLoc());
        }
        Point nextPt = (Point) path.get(pathIndex);
        pathIndex++;
        int quad = whichQuadrant(nextPt);
        setMove(nextPt, quad);
    }

    private ArrayList aStarSearch(Point startLoc, Point goalLoc) {
        double newCost;
        TileNode bestNode, newNode;

        TileNode startNode = new TileNode(startLoc);
        startNode.costToGoal(goalLoc);

        TilesPriQueue open = new TilesPriQueue(startNode);
        TilesList closed = new TilesList();

        while (open.size() != 0) {
            bestNode = open.removeFirst();
            if (goalLoc.equals(bestNode.getPoint())) {
                return bestNode.buildPath();
            } else {
                for (int i = 0; i < NUM_DIRS; i++) {
                    if ((newNode = bestNode.makeNeighbour(i, world)) != null) {
                        newCost = newNode.getCostFromStart();
                        TileNode oldVer;
                        if (((oldVer = open.findNode(newNode.getPoint())) != null) &&
                                (oldVer.getCostFromStart() <= newCost)) {
                            continue;
                        } else if (((oldVer = closed.findNode(newNode.getPoint())) != null) &&
                                (oldVer.getCostFromStart() <= newCost)) {
                            continue;
                        } else {
                            newNode.costToGoal(goalLoc);
                            closed.delete(newNode.getPoint());
                            open.delete(newNode.getPoint());
                            open.add(newNode);
                        }
                    }
                }
            }
            closed.add(bestNode);
        }
        return null;
    }
}
