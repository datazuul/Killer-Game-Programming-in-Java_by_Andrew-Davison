package com.example.synthesizesound;

import javax.sound.midi.*;

public class SeqSynth implements MetaEventListener {

    private static final int END_OF_TRACK = 47;
    private static final int CHANNEL = 0;
    private static final int BANK_CONTROLLER = 0;
    private static final int VOLUME = 90;
    private static final int[] cOffsets = {9, 11, 0, 2, 4, 5, 7};
    private static final int C4_KEY = 60;
    private static final int OCTAVE = 12;

    private Sequencer sequencer;
    private Synthesizer synthesizer;
    private Sequence seq;
    private Track track;

    private int tickPos = 0;

    public SeqSynth() {
        createSequencer();
        createTrack(4);
        makeSong();
        startSequencer(60);

        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
        }

        System.exit(0);
    }

    private void createSequencer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

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
            e.printStackTrace();
        }
    }

    private void listInstruments() {
        Instrument[] instruments = synthesizer.getAvailableInstruments();
        System.out.println("No. of instruments : " + instruments.length);
        for (int i = 0; i < instruments.length; i++) {
            Patch patch = instruments[i].getPatch();
            System.out.print("( " + instruments[i].getName() + " < " + patch.getBank() + " , " + patch.getProgram() + " > )");
            if (i % 3 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    private void createTrack(int resolution) {
        try {
            seq = new Sequence(Sequence.PPQ, resolution);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        track = seq.createTrack();
    }

    private void makeScale(int baseNote) {
        for (int i = 0; i < 13; i++) {
            add(baseNote);
            baseNote++;
        }
        for (int i = 0; i < 13; i++) {
            add(baseNote);
            baseNote--;
        }
    }

    private void makeSong() {
        changeInstrument(0, 33);
        addRest(7);
        add("F4");
        add("F4#");
        add("F4");
        add("D4#");
        add("C4#");
        add("D4#", 3);
        add("F4");
        add("G4#");
        add("F4#");
        add("F4");
        add("D4#");
        add("F4#", 3);
        add("G4#");
        add("C5#");
        add("C5");
        add("A4#");
        add("G4#");
        add("A4#", 4);
        add("G4", 4);
        add("G4#", 2);
        changeInstrument(0, 15);
        addRest(1);
        add("C5");
        add("D5#");
        add("C5#");
        add("C5");
        add("A4#");
        add("C5", 2);
        add("C5#", 2);
        add("G4#", 2);
        add("G4#", 2);
        add("C4#", 2);
        add("D4#", 2);
        add("C4#", 2);
        addRest(1);
    }

    private void add(String noteStr) {
        add(noteStr, 1);
    }

    private void add(int note) {
        add(note, 1);
    }

    private void add(String noteStr, int period) {
        int note = getKey(noteStr);
        add(note, period);
    }

    private void add(int note, int period) {
        setMessage(ShortMessage.NOTE_ON, note, tickPos);
        tickPos += period;
        setMessage(ShortMessage.NOTE_OFF, note, tickPos);
    }

    private void addRest(int period) {
        tickPos += period;
    }

    private int getKey(String noteStr) {
        char[] letters = noteStr.toCharArray();

        if (letters.length < 2) {
            System.out.println("Incorrect note syntax; using C4");
            return C4_KEY;
        }

        int c_offset = 0;
        if ((letters[0] >= 'A') && (letters[0] <= 'G')) {
            c_offset = cOffsets[letters[0] - 'A'];
        } else {
            System.out.println("Incorrect letter : " + letters[0] + ", using C");
        }

        int range = C4_KEY;
        if ((letters[1] >= '0') && (letters[1] <= '9')) {
            range = OCTAVE * (letters[1] - '0' + 1);
        } else {
            System.out.println("Incorrect number : " + letters[1] + ", using 4");
        }

        int sharp = 0;
        if ((letters.length > 2) && (letters[2] == '#')) {
            sharp = 1;
        }

        int key = range + c_offset + sharp;
        return key;
    }

    private void setMessage(int onOrOff, int note, int tickPos) {
        if ((note < 0) || (note > 127)) {
            System.out.println("Note outside MIDI range(0-127) : " + note);
            return;
        }

        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(onOrOff, CHANNEL, note, VOLUME);
            MidiEvent event = new MidiEvent(message, tickPos);
            track.add(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void changeInstrument(int bank, int program) {
        Instrument[] instruments = synthesizer.getAvailableInstruments();
        for (int i = 0; i < instruments.length; i++) {
            Patch patch = instruments[i].getPatch();
            if ((bank == patch.getBank()) && (program == patch.getProgram())) {
                programChange(program);
                bankChange(bank);
                return;
            }
        }
        System.out.println("No instrument of type < " + bank + " , " + program + " >");
    }

    private void programChange(int program) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(ShortMessage.PROGRAM_CHANGE, CHANNEL, program, 0);
            MidiEvent event = new MidiEvent(message, tickPos);
            track.add(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void bankChange(int bank) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(ShortMessage.CONTROL_CHANGE, CHANNEL, BANK_CONTROLLER, bank);
            MidiEvent event = new MidiEvent(message, tickPos);
            track.add(event);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
    }

    private void startSequencer(int tempo) {
        try {
            sequencer.setSequence(seq);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        sequencer.addMetaEventListener(this);
        sequencer.start();
        sequencer.setTempoInBPM(tempo);
    }

    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == END_OF_TRACK) {
            System.out.println("End of the track");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new SeqSynth();
        System.exit(0);
    }
}
