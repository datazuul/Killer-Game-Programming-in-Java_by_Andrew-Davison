package com.example.test;

import java.applet.Applet;
import java.awt.*;

public class Test7 extends Applet {

    public void init() {
        play(getCodeBase(), "McDonald.mid");
    }

    public void paint(Graphics graphics) {
        graphics.drawString("Older McDonald", 25, 25);
    }
}
