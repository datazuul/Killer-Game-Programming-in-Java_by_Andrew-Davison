package com.example.soundplayer;

import javax.sound.midi.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class FadeMidi implements MetaEventListener {

    private static final int END_OF_TRACK = 47;
    private static final int VOLUME_CONTROLLER = 7;
    private static final String SOUND_DIR = "Sounds/";

    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Sequence seq;
    private String filename;
    private DecimalFormat decimalFormat;

    private MidiChannel[] channels;

    public FadeMidi(String fnm) {
        decimalFormat = new DecimalFormat("0.#");
        filename = SOUND_DIR + fnm;
        initSequencer();
        loadMidi(filename);
        play();
    }

    private void initSequencer() {
        try {
            sequencer = obtainSequencer();

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

    private Sequencer obtainSequencer() {
        MidiDevice.Info[] mdi = MidiSystem.getMidiDeviceInfo();
        int seqPosn = -1;
        for (int i = 0; i < mdi.length; i++) {
            System.out.println(mdi[i].getName());
            if (mdi[i].getName().indexOf("Sequencer") != -1) {
                seqPosn = i;
                System.out.println(" Found sequencer");
            }
        }

        try {
            if (seqPosn != -1) {
                return (Sequencer) MidiSystem.getMidiDevice(mdi[seqPosn]);
            } else {
                return null;
            }
        } catch (MidiUnavailableException e) {
            return null;
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
            System.out.println("Problem with " + fnm);
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

    private void showChannelVolumes() {
        System.out.println("Synthesizer Channels : " + channels.length);
        System.out.print("Volumes : {");
        for (int i = 0; i < channels.length; i++) {
            System.out.print(channels[i].getController(VOLUME_CONTROLLER) + " ");
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

    public void startVolChanger(VolChanger vc) {
        vc.startChanging((int) (seq.getMicrosecondLength() / 1000));
    }

    public int getMaxVolume() {
        int maxVol = 0;
        int channelVol;
        for (int i = 0; i < channels.length; i++) {
            channelVol = channels[i].getController(VOLUME_CONTROLLER);
            if (maxVol < channelVol) {
                maxVol = channelVol;
            }
        }
        return maxVol;
    }

    public void setVolume(int volume) {
        for (int i = 0; i < channels.length; i++) {
            channels[i].controlChange(VOLUME_CONTROLLER, volume);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java FadeMidi <midi file>");
            System.exit(0);
        }

        FadeMidi player = new FadeMidi(args[0]);
        VolChanger vc = new VolChanger(player);

        player.startVolChanger(vc);
    }
}
