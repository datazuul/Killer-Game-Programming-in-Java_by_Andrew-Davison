package com.example.alientiles;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import java.io.IOException;

public class MidiInfo {

    private static final String SOUND_DIR = "Sounds/";

    private String name, filename;
    private Sequencer sequencer;
    private Sequence seq = null;
    private boolean isLooping = false;

    public MidiInfo(String nm, String fnm, Sequencer sqr) {
        name = nm;
        filename = SOUND_DIR + fnm;
        sequencer = sqr;
        loadMidi();
    }

    private void loadMidi() {
        try {
            seq = MidiSystem.getSequence(getClass().getResource(filename));
        } catch (InvalidMidiDataException e) {
            System.out.println("Unreadable/unsupported midi file : " + filename);
        } catch (IOException e) {
            System.out.println("Could not read : " + filename);
        } catch (Exception e) {
            System.out.println("Problem with : " + filename);
        }
    }

    public void play(boolean toLoop) {
        if ((sequencer != null) && (seq != null)) {
            try {
                sequencer.setSequence(seq);
                sequencer.setTickPosition(0);
                isLooping = toLoop;
                sequencer.start();
            } catch (InvalidMidiDataException e) {
                System.out.println("Corrupted/invalid midi file : " + filename);
            }
        }
    }

    public void stop() {
        if ((sequencer != null) && (seq != null)) {
            isLooping = false;
            if (!sequencer.isRunning()) {
                sequencer.start();
            }
            sequencer.setTickPosition(sequencer.getTickLength());
        }
    }

    public void pause() {
        if ((sequencer != null) && (seq != null)) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
        }
    }

    public void resume() {
        if ((sequencer != null) && (seq != null)) {
            sequencer.start();
        }
    }

    public boolean tryLooping() {
        if ((sequencer != null) && (seq != null)) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            sequencer.setTickPosition(0);
            if (isLooping) {
                sequencer.start();
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
}
