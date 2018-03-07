package com.example.soundplayer;

import javax.sound.midi.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class PlayMidi implements MetaEventListener {

    public static final int END_OF_TRACK = 47;
    public static final String SOUND_DIR = "Sounds/";

    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Sequence seq = null;
    private String fileName;
    private DecimalFormat decimalFormat;

    public PlayMidi(String fnm) {
        decimalFormat = new DecimalFormat("0.#");

        fileName = SOUND_DIR + fnm;
        initSequencer();
        loadMidi(fileName);
        play();

        System.out.println("Waiting");
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
        }
    }

    private void initSequencer() {
        try {
            sequencer = MidiSystem.getSequencer();

            if (sequencer == null) {
                System.out.println("Cannot get a sequencer");
                System.exit(0);
            }

            sequencer.open();
            sequencer.addMetaEventListener(this);

            if (!(sequencer instanceof Synthesizer)) {
                System.out.println("Linking the sequencer to a synthesizer");
                synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                Receiver synthReceiver = synthesizer.getReceiver();
                Transmitter seqTransmitter = sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
            } else {
                synthesizer = (Synthesizer) sequencer;
            }
        } catch (MidiUnavailableException e) {
            System.out.println("No sequencer available");
            System.exit(0);
        }
    }

    private void loadMidi(String fnm) {
        try {
            seq = MidiSystem.getSequence(getClass().getResource(fnm));
            double duration = ((double) seq.getMicrosecondLength()) / 1000000;
            System.out.println("Duration : " + decimalFormat.format(duration) + " secs");
        } catch (InvalidMidiDataException e) {
            System.out.println("Unreadable/unsupported midi file : " + fnm);
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Could not read : " + fnm);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Problem with : " + fnm);
            System.exit(0);
        }
    }

    private void play() {
        if ((sequencer != null) && (seq != null)) {
            try {
                sequencer.setSequence(seq);
                sequencer.start();
            } catch (InvalidMidiDataException e) {
                System.out.println("Corrupted/invalid midi file : " + fileName);
                System.exit(0);
            }
        }
    }

    //@Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == END_OF_TRACK) {
            System.out.println("Exiting...");
            close();
            System.exit(0);
        }
    }

    private void close() {
        if (sequencer != null) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }

            sequencer.removeMetaEventListener(this);
            sequencer.close();

            if (synthesizer != null) {
                synthesizer.close();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java PlayMidi <midi file>");
            System.exit(0);
        }
        new PlayMidi(args[0]);
        System.exit(0);
    }
}
