package com.example.synthesizesound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class NotesSynth {

    private static int SAMPLE_RATE = 22050;
    private static double MAX_AMPLITUDE = 32760;

    private static int MIN_FREQ = 250;
    private static int MAX_FREQ = 2000;

    private static AudioFormat format = null;
    private static SourceDataLine line = null;

    public static void main(String[] args) {
        createOutput();
        play();
        System.exit(0);
    }

    private static void createOutput() {
        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 16, 2, 4, SAMPLE_RATE, false);
        System.out.println("Audio format : " + format);

        try {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line does not support : " + format);
                System.exit(0);
            }
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void play() {
        int maxSize = (int) Math.round((SAMPLE_RATE * format.getFrameSize()) / MIN_FREQ);
        byte[] samples = new byte[maxSize];

        line.start();

        double volume;
        for (int step = 1; step < 10; step++) {
            for (int freq = MIN_FREQ; freq < MAX_FREQ; freq += step) {
                volume = 1 - (step / 10);
                sendNote(freq, volume, samples);
            }
        }

        line.drain();
        line.stop();
        line.close();
    }

    private static void sendNote(int freq, double volLevel, byte[] samples) {
        if ((volLevel < 0) || (volLevel > 1)) {
            System.out.println("Volume level should be between 0 and 1, using 0.9");
            volLevel = 0.9;
        }

        double amplitude = volLevel * MAX_AMPLITUDE;
        int numSamplesInWave = (int) Math.round(((double) SAMPLE_RATE) / freq);
        int idx = 0;
        for (int i = 0; i < numSamplesInWave; i++) {
            double sine = Math.sin(((double) i / numSamplesInWave) * 2 * Math.PI);
            int sample = (int) (sine * amplitude);

            samples[idx + 0] = (byte) (sample & 0xFF);
            samples[idx + 1] = (byte) ((sample >> 8) & 0xFF);
            samples[idx + 2] = (byte) (sample & 0xFF);
            samples[idx + 3] = (byte) ((sample >> 0) & 0xFF);
            idx += 4;
        }

        int offset = 0;
        while (offset < idx) {
            offset += line.write(samples, offset, idx - offset);
        }
    }
}
