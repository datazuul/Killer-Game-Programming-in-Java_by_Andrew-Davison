package com.example.loader3D;

import javax.swing.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class Loader3D extends JFrame implements ActionListener {

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;
    private static final int INCR = 0;
    private static final int DECR = 1;

    private WrapLoader3D wrapLoader3D;

    private JButton xPosLeftBut, xPosRightBut, yPosLeftBut, yPosRightBut, zPosLeftBut, zPosRightBut;
    private JButton xRotLeftBut, xRotRightBut, yRotLeftBut, yRotRightBut, zRotLeftBut, zRotRightBut;

    private JTextField scaleTF;
    private JTextField xyzTF, rotTF, scaleRotTF;
    private JButton saveBut;

    private DecimalFormat decimalFormat;

    public Loader3D(String args[]) {
        super("3D Loader");

        boolean hasCoordsInfo = false;
        String filename = null;
        if ((args.length == 2) && (args[0].equals("-c"))) {
            hasCoordsInfo = true;
            filename = args[1];
        } else if (args.length == 1) {
            filename = args[0];
        } else {
            System.out.println("Usage: java Loader3D [-c] <file>");
            System.exit(0);
        }

        wrapLoader3D = new WrapLoader3D(filename, hasCoordsInfo);
        initGUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
    }

    private void initGUI() {
        ImageIcon upIcon = new ImageIcon("icons/up.gif");
        ImageIcon downIcon = new ImageIcon("icons/down.gif");
        ImageIcon leftIcon = new ImageIcon("icons/left.gif");
        ImageIcon rightIcon = new ImageIcon("icons/right.gif");
        ImageIcon inIcon = new ImageIcon("icons/in.gif");
        ImageIcon outIcon = new ImageIcon("icons/out.gif");

        decimalFormat = new DecimalFormat("0.###");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(wrapLoader3D, BorderLayout.CENTER);

        JPanel p1 = new JPanel();
        JLabel xPosLabel = new JLabel("X incr:");
        xPosLeftBut = new JButton(leftIcon);
        xPosLeftBut.addActionListener(this);
        xPosRightBut = new JButton(rightIcon);
        xPosRightBut.addActionListener(this);
        p1.add(xPosLabel);
        p1.add(xPosLeftBut);
        p1.add(xPosRightBut);

        JPanel p2 = new JPanel();
        JLabel yPosLabel = new JLabel("Y incr:");
        yPosLeftBut = new JButton(downIcon);
        yPosLeftBut.addActionListener(this);
        yPosRightBut = new JButton(upIcon);
        yPosRightBut.addActionListener(this);
        p2.add(yPosLabel);
        p2.add(yPosLeftBut);
        p2.add(yPosRightBut);

        JPanel p3 = new JPanel();
        JLabel zPosLabel = new JLabel("Z incr:");
        zPosLeftBut = new JButton(inIcon);
        zPosLeftBut.addActionListener(this);
        zPosRightBut = new JButton(outIcon);
        zPosRightBut.addActionListener(this);
        p3.add(zPosLabel);
        p3.add(zPosLeftBut);
        p3.add(zPosRightBut);

        JPanel p4 = new JPanel();
        JLabel xRotLabel = new JLabel("X rot:");
        xRotLeftBut = new JButton(leftIcon);
        xRotLeftBut.addActionListener(this);
        xRotRightBut = new JButton(rightIcon);
        xRotRightBut.addActionListener(this);
        p4.add(xRotLabel);
        p4.add(xRotLeftBut);
        p4.add(xRotRightBut);

        JPanel p5 = new JPanel();
        JLabel yRotLabel = new JLabel("Y rot:");
        yRotLeftBut = new JButton(leftIcon);
        yRotLeftBut.addActionListener(this);
        yRotRightBut = new JButton(rightIcon);
        yRotRightBut.addActionListener(this);
        p5.add(yRotLabel);
        p5.add(yRotLeftBut);
        p5.add(yRotRightBut);

        JPanel p6 = new JPanel();
        JLabel zRotLabel = new JLabel("Z rot:");
        zRotLeftBut = new JButton(leftIcon);
        zRotLeftBut.addActionListener(this);
        zRotRightBut = new JButton(rightIcon);
        zRotRightBut.addActionListener(this);
        p6.add(zRotLabel);
        p6.add(zRotLeftBut);
        p6.add(zRotRightBut);

        JPanel p7 = new JPanel();
        JLabel scaleLabel = new JLabel("Scale mult:");
        scaleTF = new JTextField("1.1", 4);
        scaleTF.addActionListener(this);
        p7.add(scaleLabel);
        p7.add(scaleTF);

        JPanel p8 = new JPanel();
        saveBut = new JButton("Save Coords");
        saveBut.addActionListener(this);
        p8.add(saveBut);

        JLabel xyzLabel = new JLabel("Pos (x, y, z):");
        xyzTF = new JTextField(10);
        xyzTF.setEditable(false);

        JLabel rotLabel = new JLabel("Rot (x, y, z):");
        rotTF = new JTextField(10);
        rotTF.setEditable(false);

        JPanel pScale = new JPanel();
        JLabel scaleRotLabel = new JLabel("Rot Scale :");
        scaleRotTF = new JTextField(4);
        scaleRotTF.setEditable(false);
        pScale.add(scaleRotLabel);
        pScale.add(scaleRotTF);

        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.Y_AXIS));
        ctrlPanel.add(p1);
        ctrlPanel.add(p2);
        ctrlPanel.add(p3);
        ctrlPanel.add(p4);
        ctrlPanel.add(p5);
        ctrlPanel.add(p6);
        ctrlPanel.add(p7);
        ctrlPanel.add(p8);
        ctrlPanel.add(javax.swing.Box.createVerticalStrut(15));
        ctrlPanel.add(xyzLabel);
        ctrlPanel.add(xyzTF);
        ctrlPanel.add(rotLabel);
        ctrlPanel.add(rotTF);
        ctrlPanel.add(pScale);

        JPanel ctrllp = new JPanel();
        ctrllp.add(ctrlPanel);

        container.add(ctrllp, BorderLayout.EAST);

        showPosInfo();
        showRotInfo();
        showScale();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveBut) {
            wrapLoader3D.saveCoordFile();
        } else if (e.getSource() == xPosLeftBut) {
            wrapLoader3D.movePos(X_AXIS, DECR);
        } else if (e.getSource() == xPosRightBut) {
            wrapLoader3D.movePos(X_AXIS, INCR);
        } else if (e.getSource() == yPosLeftBut) {
            wrapLoader3D.movePos(Y_AXIS, DECR);
        } else if (e.getSource() == yPosRightBut) {
            wrapLoader3D.movePos(Y_AXIS, INCR);
        } else if (e.getSource() == zPosLeftBut) {
            wrapLoader3D.movePos(Z_AXIS, DECR);
        } else if (e.getSource() == zPosRightBut) {
            wrapLoader3D.movePos(Z_AXIS, INCR);
        } else {
            if (e.getSource() == xRotLeftBut) {
                wrapLoader3D.rotate(X_AXIS, DECR);
            } else if (e.getSource() == xRotRightBut) {
                wrapLoader3D.rotate(X_AXIS, INCR);
            } else if (e.getSource() == yRotLeftBut) {
                wrapLoader3D.rotate(Y_AXIS, INCR);
            } else if (e.getSource() == yRotRightBut) {
                wrapLoader3D.rotate(Y_AXIS, DECR);
            } else if (e.getSource() == zRotLeftBut) {
                wrapLoader3D.rotate(Z_AXIS, INCR);
            } else if (e.getSource() == zRotRightBut) {
                wrapLoader3D.rotate(Z_AXIS, DECR);
            } else if (e.getSource() == scaleTF) {
                try {
                    double d = Double.parseDouble(e.getActionCommand());
                    wrapLoader3D.scale(d);
                } catch (NumberFormatException ex) {
                    System.out.println("Scale input was not a number");
                }
            }
        }
        showPosInfo();
        showRotInfo();
        showScale();
    }

    private void showPosInfo() {
        Vector3d loc = wrapLoader3D.getLoc();
        xyzTF.setText("(" + decimalFormat.format(loc.x) + ", " + decimalFormat.format(loc.y) + ", " + decimalFormat.format(loc.z) + ")");
    }

    private void showRotInfo() {
        Point3d rots = wrapLoader3D.getRotatiions();
        rotTF.setText("(" + decimalFormat.format(rots.x) + ", " + decimalFormat.format(rots.y) + ", " + decimalFormat.format(rots.z) + ")");
    }

    private void showScale() {
        double scale = wrapLoader3D.getScale();
        scaleRotTF.setText(decimalFormat.format(scale));
    }

    public static void main(String[] args) {
        new Loader3D(args);
    }
}
