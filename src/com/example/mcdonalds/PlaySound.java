/*
package com.example.mcdonalds;

import java.applet.Applet;
import java.applet.AudioClip;

public class PlaySound {

    public PlaySound(String fnm) {
        try {
            AudioClip clip = Applet.newAudioClip(getClass().getResource(fnm));
            clip.play();
        } catch (Exception e) {
            System.out.println("Problem with " + fnm);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PlaySound <sound file>");
            System.exit(0);
        }
        new PlaySound(args[0]);
    }
}
*/
