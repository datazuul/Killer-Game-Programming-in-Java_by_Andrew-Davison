package com.example.soundplayer;

import javax.sound.midi.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class PanMidi implements MetaEventListener {

    private static final int PAN_CONTROLLER = 10;
    private static final int END_OF_TRACK = 47;
    private static final String SOUND_DIR = "Sounds/";

    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Sequence seq;
    private String filename;
    private DecimalFormat decimalFormat;

    private MidiChannel[] channels;

    public PanMidi(String fnm) {
        decimalFormat = new DecimalFormat("0.#");

        filename = SOUND_DIR + fnm;
        initSequencer();
        loadMidi(filename);
        play();
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
                System.out.println("Linking the MIDI sequencer and synthesizer");
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
        seq = null;
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
                channels = synthesizer.getChannels();
            } catch (InvalidMidiDataException e) {
                System.out.println("Corrupted/invalid midi file : " + filename);
                System.exit(0);
            }
        }
    }

    private void showChannelPans() {
        System.out.println("Synthesizer Channels : " + channels.length);
        System.out.print("Pans : { ");
        for (int i = 0; i < channels.length; i++) {
            System.out.print(channels[i].getController(PAN_CONTROLLER) + " ");
        }
        System.out.print("}");
    }

    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == END_OF_TRACK) {
            System.out.println("Exiting");
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

    public void startPanChanger(PanChanger pc) {
        pc.startChanging((int) (seq.getMicrosecondLength() / 1000));
    }

    public int getMaxPan() {
        int maxPan = 0;
        int channelPan;
        for (int i = 0; i < channels.length; i++) {
            channelPan = channels[i].getController(PAN_CONTROLLER);
            if (maxPan < channelPan) {
                maxPan = channelPan;
            }
        }
        return maxPan;
    }

    public void setPan(int panVal) {
        for (int i = 0; i < channels.length; i++) {
            channels[i].controlChange(PAN_CONTROLLER, panVal);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java PanMidi <midi file>");
            System.exit(0);
        }

        PanMidi player = new PanMidi(args[0]);
        PanChanger pc = new PanChanger(player);

        player.startPanChanger(pc);
    }
}
