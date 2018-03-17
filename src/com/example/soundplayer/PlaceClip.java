package com.example.soundplayer;

import javax.sound.sampled.*;
import java.io.IOException;
import java.text.DecimalFormat;

public class PlaceClip implements LineListener {

    private static final String SOUND_DIR = "Sounds/";

    private static final float NO_VOL_CHANGE = -1.0f;
    private static final float NO_PAN_CHANGE = 0.0f;

    private Clip clip = null;
    private AudioFormat format;
    private DecimalFormat decimalFormat;

    private float volume, pan;

    public PlaceClip(String[] args) {
        decimalFormat = new DecimalFormat("0.#");

        getSettings(args);
        loadClip(SOUND_DIR + args[0]);

        showControls();
        setVolume(volume);
        setPan(pan);

        play();

        try {
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            System.out.println("Sleep Interrupted");
        }
    }

    private void getSettings(String[] args) {
        if (args.length == 1) {
            volume = NO_VOL_CHANGE;
            pan = NO_PAN_CHANGE;
            System.out.println("No volume or pan settings supplied");
        } else if (args.length == 2) {
            getVolumeSetting(args[1]);
            pan = NO_PAN_CHANGE;
            System.out.println("No pan setting supplied");
        } else if (args.length == 3) {
            getVolumeSetting(args[1]);
            getPanSetting(args[2]);
        } else {
            System.out.println("Usage: java PlaceClip <clip file> [<volume> [<pan>]]");
            System.exit(0);
        }
    }

    private void getVolumeSetting(String volStr) {
        try {
            volume = Float.parseFloat(volStr);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect volume format");
            volume = NO_VOL_CHANGE;
        }

        if (volume == NO_VOL_CHANGE) {
            System.out.println("No volume change");
        } else if ((volume >= 0.0f) && (volume <= 1.0f)) {
            System.out.println("Volume setting : " + volume);
        } else {
            System.out.println("Volume out of range (0 - 1); volume not being changed");
            volume = NO_VOL_CHANGE;
        }
    }

    private void getPanSetting(String panStr) {
        try {
            pan = Float.parseFloat(panStr);
        } catch (NumberFormatException e) {
            System.out.println("Incorrect pan format");
            pan = NO_PAN_CHANGE;
        }

        if (pan == NO_PAN_CHANGE) {
            System.out.println("No pan change");
        } else if ((pan >= -1.0f) && (pan <= 1.0f)) {
            System.out.println("Pan setting : " + pan);
        } else {
            System.out.println("Pan out of range (-1 - 1); pan not being changed");
            pan = NO_PAN_CHANGE;
        }
    }

    private void loadClip(String fnm) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(getClass().getResource(fnm));
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

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Unsupported Clip file : " + fnm);
                System.exit(0);
            }

            clip = (Clip) AudioSystem.getLine(info);
            clip.addLineListener(this);
            clip.open(stream);
            stream.close();

            checkDuration();
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
            System.out.println("Problem with : " + fnm);
            System.exit(0);
        }
    }

    private void checkDuration() {
        double duration = clip.getMicrosecondLength() / 1000000;
        if (duration <= 1) {
            System.out.println("WARNING. Duration <= 1 sec : " + decimalFormat.format(duration) + " secs");
            System.out.println("         The clip may not play in J2SE 1.5 -- make it longer");
        } else {
            System.out.println("Duration : " + decimalFormat.format(duration) + " secs");
        }
    }

    private void showControls() {
        if (clip != null) {
            Control controls[] = clip.getControls();
            for (int i = 0; i < controls.length; i++) {
                System.out.println(i + ". " + controls[i].toString());
            }
        }
    }

    private void setVolume(float volume) {
        if ((clip != null) && (volume != NO_VOL_CHANGE)) {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float range = floatControl.getMaximum() - floatControl.getMinimum();
                float gain = (range * volume) + floatControl.getMinimum();
                System.out.println("Volume : " + volume + "; New gain : " + gain);
                floatControl.setValue(gain);
            } else {
                System.out.println("No volume controls available");
            }
        }
    }

    private void setPan(float pan) {
        if ((clip == null) || (pan == NO_PAN_CHANGE)) {
            return;
        }
        if (clip.isControlSupported(FloatControl.Type.PAN)) {
            FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            panControl.setValue(pan);
        } else if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
            FloatControl balanceControl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
            balanceControl.setValue(pan);
        } else {
            System.out.println("No pan or balance controls available");
            if (format.getChannels() == 1) {
                System.out.println("Your audio file is mono; try converting it to stereo");
            }
        }
    }

    private void play() {
        if (clip != null) {
            clip.start();
        }
    }

    @Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            clip.stop();
            event.getLine().close();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new PlaceClip(args);
        System.exit(0);
    }
}
