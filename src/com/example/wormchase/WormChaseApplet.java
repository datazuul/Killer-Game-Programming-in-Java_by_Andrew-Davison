package com.example.wormchase;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;

//public class WormChaseApplet extends JFrame implements WindowListener {
public class WormChaseApplet extends JApplet {
    private static int DEFAULT_FPS = 80;

    private WormPanel wp;
    private JTextField jtfBox;
    private JTextField jtfTime;

    //public WormChaseApplet(/*long period*/int period) {
        //super("The Worm Chase");
        //makeGUI(period);

        //addWindowListener(this);
        //pack();
        //setResizable(false);
        //setVisible(true);
    //}

    public void init() {
        String str = getParameter("fps");
        int fps = (str != null) ? Integer.parseInt(str) : DEFAULT_FPS;

        long period = (long) 1000 / fps;
        System.out.println("fps : " + fps + "; period : " + period + " ms");

        makeGUI(period);
        wp.startGame();
    }

    private void makeGUI(long/*int*/ period) {
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        wp = new WormPanel(this, period * 1000000L);
        c.add(wp, "Center");

        JPanel ctrls = new JPanel();
        ctrls.setLayout(new BoxLayout(ctrls, BoxLayout.X_AXIS));

        jtfBox = new JTextField("Boxes used : 0");
        jtfBox.setEditable(false);
        ctrls.add(jtfBox);

        jtfTime = new JTextField("Time Spent : 0 secs");
        jtfTime.setEditable(false);
        ctrls.add(jtfTime);

        c.add(ctrls, "South");
    }

    public void setBoxNumber(int no) {
        jtfBox.setText("Boxes used : " + no);
    }

    public void setTimeSpent(long t) {
        jtfTime.setText("Time spent : " + t + " secs");
    }

    /**public void windowActivated(WindowEvent e) {
        wp.resumeGame();
    }

    public void windowDeactivated(WindowEvent e) {
        wp.pauseGame();
    }

    public void windowDeiconified(WindowEvent e) {
        wp.resumeGame();
    }

    public void windowIconified(WindowEvent e) {
        wp.pauseGame();
    }

    public void windowClosing(WindowEvent e) {
        wp.stopGame();
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowOpened(WindowEvent e) {

    }

    public static void main(String[] args) {
        int fps = DEFAULT_FPS;
        if (args.length != 0) {
            fps = Integer.parseInt(args[0]);
        }
        //long period = (long) 1000 / fps;
        int period = (int) 1000 / fps;
        System.out.println("fps : " + fps + "; period : " + period + " ms");
        //new WormChaseApplet(period * 1000000L);
        new WormChaseApplet(period);
    }**/

    public void start() {
        wp.resumeGame();
    }

    public void stop() {
        wp.pauseGame();
    }

    public void destroy() {
        wp.stopGame();
    }
}
