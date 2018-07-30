package com.example.loaderInfo3D;

import javax.swing.*;
import java.awt.*;

public class LoaderInfo3D extends JFrame {

    public LoaderInfo3D(String fn, int adaptNo) {
        super("LoaderInfo3D");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        WrapLoaderInfo3D wrapLoaderInfo3D = new WrapLoaderInfo3D(fn, adaptNo);
        container.add(wrapLoaderInfo3D, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setVisible(true);
        wrapLoaderInfo3D.requestFocus();
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            int adaptNo = 0;
            try {
                adaptNo = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Illegal adaptNo, using 0");
            }

            new LoaderInfo3D("models/" + args[0], adaptNo);
        } else {
            System.out.println("Usage: java -cp %CLASSPATH%;../portfolio.jar LoaderInfo3D  <file> <adaptNo>");
        }
    }
}