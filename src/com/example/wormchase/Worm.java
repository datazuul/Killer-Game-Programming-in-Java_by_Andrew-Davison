package com.example.wormchase;

import java.awt.*;
import java.awt.geom.Point2D;

public class Worm {
    private static final int DOTSIZE = 12;
    private static final int RADIUS = DOTSIZE / 2;
    private static final int MAXPOINT = 40;

    private static final int NUM_DIRS = 8;
    private static final int N = 0;
    private static final int NE = 1;
    private static final int E = 2;
    private static final int SE = 3;
    private static final int S = 4;
    private static final int SW = 5;
    private static final int W = 6;
    private static final int NW = 7;

    private int currCompass;

    Point2D.Double incrs[];

    private static final int NUM_PROBS = 9;
    private int probsForOffset[];

    private Point cells[];
    private int nPoints;
    private int tailPosn, headPosn;

    private int pWidth, pHeight;
    private long startTime;
    private Obstacles obs;

    public Worm(int pW, int pH, Obstacles os) {
        pWidth = pW;
        pHeight = pH;
        obs = os;
        cells = new Point[MAXPOINT];
        nPoints = 0;
        headPosn = -1;
        tailPosn = -1;

        incrs = new Point2D.Double[NUM_DIRS];
        incrs[N] = new Point2D.Double(0.0, -1.0);
        incrs[NE] = new Point2D.Double(0.7, -0.7);
        incrs[E] = new Point2D.Double(1.0, 0.0);
        incrs[SE] = new Point2D.Double(0.7, 0.7);
        incrs[S] = new Point2D.Double(0.0, 1.0);
        incrs[SW] = new Point2D.Double(-0.7, 0.7);
        incrs[W] = new Point2D.Double(-1.0, 0.0);
        incrs[NW] = new Point2D.Double(-0.7, -0.7);

        probsForOffset = new int[NUM_PROBS];
        probsForOffset[0] = 0;
        probsForOffset[1] = 0;
        probsForOffset[2] = 0;
        probsForOffset[3] = 1;
        probsForOffset[4] = 1;
        probsForOffset[5] = 2;
        probsForOffset[6] = -1;
        probsForOffset[7] = -1;
        probsForOffset[8] = -2;
    }

    public boolean nearHead(int x, int y) {
        if (nPoints > 0) {
            if ((Math.abs(cells[headPosn].x + RADIUS - x) <= DOTSIZE) &&
                    (Math.abs(cells[headPosn].y + RADIUS - y) <= DOTSIZE)) {
                return true;
            }
        }
        return false;
    }

    public boolean touchedAt(int x, int y) {
        int i = tailPosn;
        while (i != headPosn) {
            if ((Math.abs(cells[i].x + RADIUS - x) <= RADIUS) &&
                    (Math.abs(cells[i].y + RADIUS - y) <= RADIUS)) {
                return true;
            }
            i = (i + 1) % MAXPOINT;
        }
        return false;
    }

    public void move() {
        int prevPosn = headPosn;
        headPosn = (headPosn + 1) % MAXPOINT;

        if (nPoints == 0) {
            tailPosn = headPosn;
            currCompass = (int) (Math.random() * NUM_DIRS);
            cells[headPosn] = new Point(pWidth / 2, pHeight / 2);
            nPoints++;
        } else if (nPoints == MAXPOINT) {
            tailPosn = (tailPosn + 1) % MAXPOINT;
            newHead(prevPosn);
        } else {
            newHead(prevPosn);
            nPoints++;
        }
    }

    private void newHead(int prevPosn) {
        Point newPt;
        int newBearing;
        int fixedOffs[] = {-2, 2, -4};

        newBearing = varyBearing();
        newPt = nextPoint(prevPosn, newBearing);
        if (obs.hits(newPt, DOTSIZE)) {
            for (int i = 0; i < fixedOffs.length; i++) {
                newBearing = calcBearing(fixedOffs[i]);
                newPt = nextPoint(prevPosn, newBearing);
                if (!obs.hits(newPt, DOTSIZE)) {
                    break;
                }
            }
        }
        cells[headPosn] = newPt;
        currCompass = newBearing;
    }

    private int varyBearing() {
        int newOffset = probsForOffset[(int) (Math.random() * NUM_PROBS)];
        return calcBearing(newOffset);
    }

    private int calcBearing(int offset) {
        int turn = currCompass + offset;

        if (turn >= NUM_DIRS) {
            turn = turn - NUM_DIRS;
        } else if (turn < 0) {
            turn = NUM_DIRS + turn;
        }

        return turn;
    }

    private Point nextPoint(int prevPosn, int bearing) {
        Point2D.Double incr = incrs[bearing];

        int newX = cells[prevPosn].x + (int) (DOTSIZE * incr.x);
        int newY = cells[prevPosn].y + (int) (DOTSIZE * incr.y);

        if (newX + DOTSIZE < 0) {
            newX = newX + pWidth;
        } else if (newX > pWidth) {
            newX = newX - pWidth;
        }

        if (newY + DOTSIZE < 0) {
            newY = newY + pHeight;
        } else if (newY > pHeight) {
            newY = newY - pHeight;
        }

        return new Point(newX, newY);
    }

    public void draw(Graphics g) {
        if (nPoints > 0) {
            g.setColor(Color.black);
            int i = tailPosn;
            while (i != headPosn) {
                g.fillOval(cells[i].x, cells[i].y, DOTSIZE, DOTSIZE);
                i = (i + 1) % MAXPOINT;
            }
            g.setColor(Color.red);
            g.fillOval(cells[headPosn].x, cells[headPosn].y, DOTSIZE, DOTSIZE);
        }
    }
}
