package com.example.soundplayer;

import javax.sound.sampled.*;
import java.io.*;

public class EchoSamplesPlayer {

    private static final int ECHO_NUMBER = 4;
    private static final double DECAY = 0.5;

    private static AudioInputStream stream;
    private static AudioFormat format = null;
    private static SourceDataLine line = null;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java EchoSamplesPlayer <clip file>");
            System.exit(0);
        }

        createInput("Sounds/" + args[0]);

        if (!isRequiredFormat()) {
            System.out.println("Format unsuitable for echoing");
            System.exit(0);
        }

        createOutput();

        int numBytes = (int) (stream.getFrameLength() * format.getFrameSize());
        System.out.println("Size in bytes : " + numBytes);

        byte[] samples = getSamples(numBytes);
        play(samples);

        System.exit(0);
    }

    private static void createInput(String fnm) {
        try {
            stream = AudioSystem.getAudioInputStream(new File(fnm));
            format = stream.getFormat();
            System.out.println("Audio format : " + format);

            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                    (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat newFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        format.getSampleRate(),
                        format.getSampleSizeInBits() * 2,
                        format.getChannels(),
                        format.getFrameSize() * 2,
                        format.getFrameRate(), true);

                stream = AudioSystem.getAudioInputStream(newFormat, stream);
                System.out.println("Converted Audio format : " + newFormat);
                format = newFormat;
            }
        } catch (UnsupportedAudioFileException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static boolean isRequiredFormat() {
        if (((format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) ||
                (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED)) &&
                (format.getSampleSizeInBits() == 8)) {
            return true;
        } else {
            return false;
        }
    }

    private static void createOutput() {
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

    private static byte[] getSamples(int numBytes) {
        byte[] samples = new byte[numBytes];
        DataInputStream dataInputStream = new DataInputStream(stream);
        try {
            dataInputStream.readFully(samples);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        return echoSamples(samples, numBytes);
    }

    private static byte[] echoSamples(byte[] samples, int numBytes) {
        int numTimes = ECHO_NUMBER + 1;
        double currentDecay = 1;
        short sample, newSample;
        byte[] newSamples = new byte[numBytes * numTimes];

        for (int j = 0; j < numTimes; j++) {
            for (int i = 0; i < numBytes; i++) {
                newSamples[i + (numBytes * j)] = echoSample(samples[i], currentDecay);
            }
            currentDecay *= DECAY;
        }
        return newSamples;
    }

    private static byte echoSample(byte sampleByte, double currentDecay) {
        short sample, newSample;
        if (format.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
            sample = (short) (sampleByte & 0xff);
            newSample = (short) (sample * currentDecay);
            return (byte) newSample;
        } else if (format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
            sample = (short) sampleByte;
            newSample = (short) (sample * currentDecay);
            return (byte) newSample;
        } else {
            return sampleByte;
        }
    }

    private static void play(byte[] samples) {
        InputStream source = new ByteArrayInputStream(samples);

        int numRead = 0;
        byte[] buf = new byte[line.getBufferSize()];

        line.start();

        try {
            while ((numRead = source.read(buf, 0, buf.length)) >= 0) {
                int offset = 0;
                while (offset < numRead) {
                    offset += line.write(buf, offset, numRead - offset);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        line.drain();
        line.stop();
        line.close();
    }
}
