package com.example.wormchase;

import java.awt.*;
import java.util.ArrayList;

public class Obstacles {
    private static final int BOX_LENGTH = 12;

    private ArrayList boxes;
    private WormChase wormChase;
    //private WormPanel wormPanel;

    public Obstacles(WormChase wc) {
        //public Obstacles(WormPanel wp) {
        boxes = new ArrayList();
        wormChase = wc;
        //wormPanel = wp;
    }

    synchronized public void add(int x, int y) {
        boxes.add(new Rectangle(x, y, BOX_LENGTH, BOX_LENGTH));
        wormChase.setBoxNumber(boxes.size());
        //wormPanel.setBoxNumber(boxes.size());
    }

    synchronized public boolean hits(Point p, int size) {
        Rectangle r = new Rectangle(p.x, p.y, size, size);
        Rectangle box;

        for (int i = 0; i < boxes.size(); i++) {
            box = (Rectangle) boxes.get(i);
            if (box.intersects(r)) {
                return true;
            }
        }

        return false;
    }

    synchronized public void draw(Graphics g) {
        Rectangle box;
        g.setColor(Color.blue);
        for (int i = 0; i < boxes.size(); i++) {
            box = (Rectangle) boxes.get(i);
            g.fillRect(box.x, box.y, box.width, box.height);
        }
    }

    synchronized public int getNumObstacles() {
        return boxes.size();
    }
}
