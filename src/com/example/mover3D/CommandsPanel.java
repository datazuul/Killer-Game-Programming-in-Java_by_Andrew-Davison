package com.example.mover3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

public class CommandsPanel extends JPanel {

    private static final int FWD = 0;
    private static final int BACK = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int UP = 4;
    private static final int DOWN = 5;

    private static final int CLOCK = 0;
    private static final int CCLOCK = 1;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;
    private static final int Z_AXIS = 2;

    private Figure figure;

    public CommandsPanel(Figure fig) {
        figure = fig;

        setLayout(new FlowLayout());
        add(new JLabel("Commands : "));

        JTextField commsTF = new JTextField(35);
        add(commsTF);
        commsTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processComms(e.getActionCommand());
            }
        });

        JButton resetBut = new JButton("Reset");
        add(resetBut);
        resetBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                figure.reset();
            }
        });
    }

    private void processComms(String input) {
        if (input == null) {
            return;
        }

        String[] commands = input.split(",");

        StringTokenizer tokenizer;
        for (int i = 0; i < commands.length; i++) {
            tokenizer = new StringTokenizer(commands[i].trim());
            if (tokenizer.countTokens() == 3) {
                limbCommand(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
            } else if (tokenizer.countTokens() == 2) {
                limbCommand(tokenizer.nextToken(), tokenizer.nextToken(), "5");
            } else if (tokenizer.countTokens() == 1) {
                figCommand(tokenizer.nextToken());
            } else {
                System.out.println("Illegal command : " + commands[i]);
            }
        }
    }

    private void limbCommand(String limbName, String opStr, String angleStr) {
        int limbNo = -1;
        try {
            limbNo = figure.checkLimbNo(Integer.parseInt(limbName));
        } catch (NumberFormatException e) {
            limbNo = figure.findLimbNo(limbName);
        }

        if (limbNo == -1) {
            System.out.println("Illegal Limb name/no : " + limbName);
            return;
        }

        double angleChg = 0;
        try {
            angleChg = Double.parseDouble(angleStr);
        } catch (NumberFormatException e) {
            System.out.println("Illegal angle change : " + angleStr);
        }

        if (angleChg == 0) {
            System.out.println("Angle change is 0, so doing nothing");
            return;
        }

        int axis;
        if (opStr.equals("fwd") || opStr.equals("f")) {
            axis = X_AXIS;
        } else if (opStr.equals("turn") || opStr.equals("t")) {
            axis = Y_AXIS;
        } else if (opStr.equals("side") || opStr.equals("s")) {
            axis = Z_AXIS;
        } else {
            System.out.println("Unknown limb operation : " + opStr);
            return;
        }

        figure.updateLimb(limbNo, axis, angleChg);
    }

    private void figCommand(String opStr) {
        if (opStr.equals("fwd") || opStr.equals("f")) {
            figure.doMove(FWD);
        } else if (opStr.equals("back") || opStr.equals("b")) {
            figure.doMove(BACK);
        } else if (opStr.equals("left") || opStr.equals("l")) {
            figure.doMove(LEFT);
        } else if (opStr.equals("right") || opStr.equals("r")) {
            figure.doMove(RIGHT);
        } else if (opStr.equals("up") || opStr.equals("u")) {
            figure.doMove(UP);
        } else if (opStr.equals("down") || opStr.equals("d")) {
            figure.doMove(DOWN);
        } else if (opStr.equals("clock") || opStr.equals("c")) {
            figure.doRotateY(CLOCK);
        } else if (opStr.equals("cclock") || opStr.equals("cc")) {
            figure.doRotateY(CCLOCK);
        } else {
            System.out.println("Unknown figure operation : " + opStr);
            return;
        }
    }
}
