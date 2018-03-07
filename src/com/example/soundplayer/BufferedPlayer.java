package com.example.soundplayer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BufferedPlayer {

    private static AudioInputStream stream;
    private static AudioFormat format = null;
    private static SourceDataLine line = null;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java BufferedPlayer <clip file>");
            System.exit(0);
        }

        createInput("Sounds/" + args[0]);
        createOutput();

        int numBytes = (int) (stream.getFrameLength() * format.getFrameSize());
        System.out.println("Size in bytes : " + numBytes);

        checkDuration();
        play();

        System.exit(0);
    }

    private static void checkDuration() {
        long milliseconds = (long) ((stream.getFrameLength() * 1000) / stream.getFormat().getFrameRate());
        double duration = milliseconds / 1000;

        if (duration <= 1) {
            System.out.println("WARNING. Duration <= 1 sec : " + duration + " secs");
            System.out.println("         The sample may not play in J2SE 1.5 -- make it longer");
        } else {
            System.out.println("Duration : " + duration + " secs");
        }
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

    private static void play() {
        int numRead = 0;
        byte[] buffer = new byte[line.getBufferSize()];

        line.start();

        try {
            int offset;
            while ((numRead = stream.read(buffer, 0, buffer.length)) >= 0) {
                offset = 0;
                while (offset < numRead) {
                    offset += line.write(buffer, offset, numRead - offset);
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
