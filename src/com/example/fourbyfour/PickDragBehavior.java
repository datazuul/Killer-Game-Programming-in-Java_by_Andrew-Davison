package com.example.fourbyfour;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

public class PickDragBehavior extends Behavior {

    private static final Vector3d IN_VEC = new Vector3d(0.0f, 0.0f, -1.0f);

    private static final double XFACTOR = 0.02;
    private static final double YFACTOR = 0.02;

    private WakeupCriterion[] mouseEvents;
    private WakeupOr mouseCriWakeupOr;

    private int xPrev, yPrev;
    private boolean isStartDrag;
    private Transform3D modelTransform3D;
    private Transform3D transform3DX, transform3DY;
    private TransformGroup boardTG;
    private BranchGroup branchGroup;
    private Canvas3D canvas3D;

    private Board board;

    private Point3d mousePos;
    private Transform3D imWorldtransform3D;
    private PickRay pickRay = new PickRay();
    private SceneGraphPath sceneGraphPath;

    public PickDragBehavior(Canvas3D canvas3D, Board board, BranchGroup branchGroup, TransformGroup boardTG) {
        this.canvas3D = canvas3D;
        this.board = board;
        this.branchGroup = branchGroup;
        this.boardTG = boardTG;

        modelTransform3D = new Transform3D();
        transform3DX = new Transform3D();
        transform3DY = new Transform3D();

        mousePos = new Point3d();
        imWorldtransform3D = new Transform3D();

        xPrev = 0;
        yPrev = 0;
        isStartDrag = true;
    }

    @Override
    public void initialize() {
        mouseEvents = new WakeupCriterion[3];

        mouseEvents[0] = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        mouseEvents[1] = new WakeupOnAWTEvent(MouseEvent.MOUSE_PRESSED);
        mouseEvents[2] = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);

        mouseCriWakeupOr = new WakeupOr(mouseEvents);
        wakeupOn(mouseCriWakeupOr);
    }

    @Override
    public void processStimulus(Enumeration enumeration) {
        WakeupCriterion wakeupCriterion;
        AWTEvent[] events;
        int id;
        int xPos, yPos;
        while (enumeration.hasMoreElements()) {
            wakeupCriterion = (WakeupCriterion) enumeration.nextElement();
            if (wakeupCriterion instanceof WakeupOnAWTEvent) {
                events = ((WakeupOnAWTEvent) wakeupCriterion).getAWTEvent();
                for (int i = 0; i < events.length; i++) {
                    xPos = ((MouseEvent) events[i]).getX();
                    yPos = ((MouseEvent) events[i]).getY();
                    id = events[i].getID();
                    if (id == MouseEvent.MOUSE_DRAGGED) {
                        processDrag(xPos, yPos);
                    } else if (id == MouseEvent.MOUSE_PRESSED) {
                        processPress(xPos, yPos);
                    } else if (id == MouseEvent.MOUSE_RELEASED) {
                        isStartDrag = true;
                    }
                }
            }
        }
        wakeupOn(mouseCriWakeupOr);
    }

    private void processDrag(int xPos, int yPos) {
        if (isStartDrag) {
            isStartDrag = false;
        } else {
            int dx = xPos - xPrev;
            int dy = yPos - yPrev;
            transform3DX.rotX(dy * YFACTOR);
            transform3DY.rotY(dx * XFACTOR);
            modelTransform3D.mul(transform3DX, modelTransform3D);
            modelTransform3D.mul(transform3DY, modelTransform3D);
            boardTG.setTransform(modelTransform3D);
        }
        xPrev = xPos;
        yPrev = yPos;
    }

    private void processPress(int xPos, int yPos) {
        canvas3D.getPixelLocationInImagePlate(xPos, yPos, mousePos);
        canvas3D.getImagePlateToVworld(imWorldtransform3D);
        imWorldtransform3D.transform(mousePos);

        pickRay.set(mousePos, IN_VEC);

        sceneGraphPath = branchGroup.pickClosest(pickRay);
        if (sceneGraphPath != null) {
            selectedPosition(sceneGraphPath);
        }
    }

    private void selectedPosition(SceneGraphPath sceneGraphPath) {
        Node node = sceneGraphPath.getObject();
        if (node instanceof Shape3D) {
            Integer posID = (Integer) node.getUserData();
            if (posID != null) {
                board.tryPosition(posID.intValue());
            }
        }
    }
}
