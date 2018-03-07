package com.example.soundplayer;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class SoundPlayer extends JFrame implements ActionListener {

    private static final String SOUND_DIR = "Sounds/";
    private static final String[] soundFNms = {"spacemusic.au", "tiger.aiff", "mcdonald.mid", "dog.wav"};

    private HashMap soundsMap;
    private ArrayList playingClips;

    private JComboBox playListJcb;
    private JButton playButton, loopButton, stopButton;
    private JLabel statusLabel;

    public SoundPlayer() {
        super("Sound Application");

        playingClips = new ArrayList();
        initGUI();
        loadSounds();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(400, 100));
        setResizable(false);
        setVisible(true);
    }

    private void initGUI() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        playListJcb = new JComboBox(soundFNms);

        playButton = new JButton("Play");
        playButton.addActionListener(this);

        loopButton = new JButton("Loop");
        loopButton.addActionListener(this);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);

        statusLabel = new JLabel("Click Play or Loop to play the selected sound file");

        JPanel controlPanel = new JPanel();
        controlPanel.add(playListJcb);
        controlPanel.add(playButton);
        controlPanel.add(loopButton);
        controlPanel.add(stopButton);

        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);

        container.add(controlPanel, BorderLayout.CENTER);
        container.add(statusPanel, BorderLayout.SOUTH);
    }

    private void loadSounds() {
        soundsMap = new HashMap();
        for (int i = 0; i < soundFNms.length; i++) {
            AudioClip clip = Applet.newAudioClip(getClass().getResource(SOUND_DIR + soundFNms[i]));
            if (clip == null) {
                System.out.println("Problem loading " + SOUND_DIR + soundFNms[i]);
            } else {
                soundsMap.put(soundFNms[i], clip);
            }
        }
    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == playButton) {
            playMusic(false);
        } else if (source == loopButton) {
            playMusic(true);
        } else if (source == stopButton) {
            stopMusic();
        }
    }

    private void playMusic(boolean toLoop) {
        String chosenFile = (String) playListJcb.getSelectedItem();

        AudioClip audioClip = (AudioClip) soundsMap.get(chosenFile);
        if (audioClip == null) {
            statusLabel.setText("Sound " + chosenFile + " not loaded");
            return;
        }
        if (toLoop) {
            audioClip.loop();
        } else {
            audioClip.play();
        }

        playingClips.add(audioClip);
        String times = (toLoop) ? " repeatedly" : " once";
        statusLabel.setText("Playing sound " + chosenFile + times);
    }

    private void stopMusic() {
        if (playingClips.isEmpty()) {
            statusLabel.setText("Nothing to stop");
        } else {
            AudioClip audioClip;
            for (int i = 0; i < playingClips.size(); i++) {
                audioClip = (AudioClip) playingClips.get(i);
                audioClip.stop();
            }
            playingClips.clear();
            statusLabel.setText("Stopped all music");
        }
    }

    public static void main(String[] args) {
        new SoundPlayer();
    }
}
