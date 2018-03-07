package com.example.soundplayer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class PlayClipBF implements LineListener {

    public static final String SOUND_DIR = "Sounds/";

    private Clip clip = null;
    private DecimalFormat decimalFormat;

    private int loopCount = 1;

    public PlayClipBF(String fnm) {
        decimalFormat = new DecimalFormat("0.#");

        loadClip(SOUND_DIR + fnm);
        play();

        System.out.println("Waiting");
        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
        }
    }

    private void loadClip(String fnm) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(fnm));
            AudioFormat format = stream.getFormat();

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

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Unsupported Clip File : " + fnm);
                System.exit(0);
            }

            clip = (Clip) AudioSystem.getLine(info);

            clip.addLineListener(this);

            clip.open(stream);
            stream.close();

            clip.setFramePosition(0);

            double duration = clip.getMicrosecondLength() / 1000000;
            System.out.println("Duration : " + decimalFormat.format(duration) + " secs");

            loopCount = (int) (1 / duration);
            System.out.println("loopCount : " + loopCount);
        } catch (UnsupportedAudioFileException audioException) {
            System.out.println("Unsupported audio file : " + fnm);
            System.exit(0);
        } catch (LineUnavailableException noLineException) {
            System.out.println("No audio line available for : " + fnm);
            System.exit(0);
        } catch (IOException ioException) {
            System.out.println("Could not read : " + fnm);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Problem with " + fnm);
            System.exit(0);
        }
    }

    private void play() {
        if (clip != null) {
            System.out.println("Playing...");
            clip.loop(loopCount);
        }
    }

    //@Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            System.out.println("Exiting...");
            clip.stop();
            clip.setFramePosition(0);

            event.getLine().close();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java PlayClipBF <clip file>");
            System.exit(0);
        }
        new PlayClipBF(args[0]);
        System.exit(0);
    }
}
