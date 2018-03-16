package com.example.loaderstests;

import javax.sound.midi.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class MidisLoader implements MetaEventListener {

    private static final int END_OF_TRACK = 47;
    private static final String SOUND_DIR = "Sounds/";

    private Sequencer sequencer;
    private HashMap midisMap;
    private MidiInfo currentMidi = null;
    private SoundsWatcher watcher = null;

    public MidisLoader() {
        midisMap = new HashMap();
        initSequencer();
    }

    public MidisLoader(String soundsFnm) {
        midisMap = new HashMap();
        initSequencer();
        loadSoundsFile(soundsFnm);
    }

    private void initSequencer() {
        try {
            sequencer = MidiSystem.getSequencer();
            if (sequencer == null) {
                System.out.println("Cannot get a sequencer");
                return;
            }

            sequencer.open();
            sequencer.addMetaEventListener(this);

            if (!(sequencer instanceof Synthesizer)) {
                System.out.println("Linking the MIDI sequencer and synthesizer");
                Synthesizer synthesizer = MidiSystem.getSynthesizer();
                synthesizer.open();
                Receiver synthReceiver = synthesizer.getReceiver();
                Transmitter seqTransmitter = sequencer.getTransmitter();
                seqTransmitter.setReceiver(synthReceiver);
            }
        } catch (MidiUnavailableException e) {
            System.out.println("No sequencer available");
            sequencer = null;
        }
    }

    private void loadSoundsFile(String soundsFnm) {
        String sndsFNm = SOUND_DIR + soundsFnm;
        System.out.println("Reading file : " + sndsFNm);
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(sndsFNm);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringTokenizer tokenizer;
            String line, name, fnm;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }

                tokenizer = new StringTokenizer(line);
                if (tokenizer.countTokens() != 2) {
                    System.out.println("Wrong no. of arguments for " + line);
                } else {
                    name = tokenizer.nextToken();
                    fnm = tokenizer.nextToken();
                    load(name, fnm);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading file : " + sndsFNm);
            System.exit(1);
        }
    }

    public void close() {
        stop();
        if (sequencer != null) {
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            sequencer.removeMetaEventListener(this);
            sequencer.close();
            sequencer = null;
        }
    }

    public void setWatcher(SoundsWatcher soundsWatcher) {
        watcher = soundsWatcher;
    }

    public void load(String name, String fnm) {
        if (midisMap.containsKey(name)) {
            System.out.println("Error : " + name + " already stored");
        } else if (sequencer == null) {
            System.out.println("No sequencer for : " + name);
        } else {
            midisMap.put(name, new MidiInfo(name, fnm, sequencer));
            System.out.println("-- " + name + "/" + fnm);
        }
    }

    public void play(String name, boolean toLoop) {
        MidiInfo midiInfo = (MidiInfo) midisMap.get(name);
        if (midiInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            if (currentMidi != null) {
                System.out.println("Sorry, " + currentMidi.getName() + " already playing");
            } else {
                currentMidi = midiInfo;
                midiInfo.play(toLoop);
            }
        }
    }

    public void stop() {
        if (currentMidi != null) {
            currentMidi.stop();
        } else {
            System.out.println("No music playing");
        }
    }

    public void pause() {
        if (currentMidi != null) {
            currentMidi.pause();
        } else {
            System.out.println("No music to pause");
        }
    }

    public void resume() {
        if (currentMidi != null) {
            currentMidi.resume();
        } else {
            System.out.println("No music to resume");
        }
    }


    //@Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == END_OF_TRACK) {
            String name = currentMidi.getName();
            boolean hasLooped = currentMidi.tryLooping();
            if (!hasLooped) {
                currentMidi = null;
            }
            if (watcher != null) {
                if (hasLooped) {
                    watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
                } else {
                    watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
                }
            }
        }
    }
}
