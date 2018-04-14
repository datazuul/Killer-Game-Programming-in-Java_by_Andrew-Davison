package com.example.alientiles;

import javax.sound.sampled.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class ClipInfo implements LineListener {

    private static final String SOUND_DIR = "Sounds/";

    private String name, filename;
    private Clip clip = null;
    private boolean isLooping = false;
    private SoundsWatcher watcher = null;
    private DecimalFormat decimalFormat;

    public ClipInfo(String nm, String fnm) {
        name = nm;
        filename = SOUND_DIR + fnm;
        decimalFormat = new DecimalFormat("0.#");

        loadClip(filename);
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
                return;
            }

            clip = (Clip) AudioSystem.getLine(info);
            clip.addLineListener(this);
            clip.open(stream);
            stream.close();

            checkDuration();
        } catch (UnsupportedAudioFileException audioException) {
            System.out.println("Unsupported audio file : " + fnm);
        } catch (LineUnavailableException noLineException) {
            System.out.println("No audio line available for : " + fnm);
        } catch (IOException ioException) {
            System.out.println("Could not read : " + fnm);
        } catch (Exception e) {
            System.out.println("Problem with : " + fnm);
        }
    }

    private void checkDuration() {
        double duration = clip.getMicrosecondLength() / 1000000;
        if (duration <= 1) {
            System.out.println("WARNING. Duration <= 1 sec : " + decimalFormat.format(duration) + " secs");
            System.out.println("The clip in " + filename + " may not play in J2SE 1.5 -- make it longer");
        } else {
            System.out.println(filename + " : Duration : " + decimalFormat.format(duration) + " secs");
        }
    }

    @Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            clip.stop();
            clip.setFramePosition(0);
            if (!isLooping) {
                if (watcher != null) {
                    watcher.atSequenceEnd(name, SoundsWatcher.STOPPED);
                }
            } else {
                clip.start();
                if (watcher != null) {
                    watcher.atSequenceEnd(name, SoundsWatcher.REPLAYED);
                }
            }
        }
    }

    public void close() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }

    public void play(boolean toLoop) {
        if (clip != null) {
            isLooping = toLoop;
            clip.start();
        }
    }

    public void stop() {
        if (clip != null) {
            isLooping = false;
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    public void pause() {
        if (clip != null) {
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null) {
            clip.start();
        }
    }

    public void setWatcher(SoundsWatcher watcher) {
        this.watcher = watcher;
    }

    public String getName() {
        return name;
    }
}
