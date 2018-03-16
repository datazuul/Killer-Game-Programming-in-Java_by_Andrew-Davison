package com.example.loaderstests;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

public class LoadersTests extends JFrame implements ActionListener, ItemListener, SoundsWatcher {

    private static final String[] shortSongNames = {"farmer", "mary", "mcdonald", "baa"};
    private static final String[] songNames = {"The farmer in the dell", "Marry had a little lamb", "Old mcdonald had a farm", "Bach sheep"};
    private static final String[] names = {"dog", "cat", "sheep", "chicken"};

    private static final String SOUNDS_FILE = "clipsInfo.txt";
    private static final String MIDIS_FILE = "midisInfo.txt";

    private static final Color grayGreen = new Color(88, 110, 84);
    private static final Color lightGreen = new Color(90, 120, 20);

    private JButton playJButton, pauseJButton, stopJButton, loopJButton;
    private JCheckBox dogJCheckBox, catJCheckBox, sheepJCheckBox, chickenJCheckBox;
    private JComboBox namesJComboBox;

    private boolean[] clipLoops = {false, false, false, false};
    private boolean isPauseButton;

    private Color background;

    private ClipsLoader clipsLoader;
    private MidisLoader midisLoader;

    public LoadersTests() {
        super("Sounds Tests");
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        SoundsPanel soundsPanel = new SoundsPanel(this);
        container.add(soundsPanel, BorderLayout.CENTER);
        initGUI(container);

        clipsLoader = new ClipsLoader(SOUNDS_FILE);
        clipsLoader.setWatcher("dog", this);

        midisLoader = new MidisLoader(MIDIS_FILE);
        midisLoader.setWatcher(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //super.windowClosing(e);
                midisLoader.close();
                System.exit(0);
            }
        });

        pack();
        setResizable(false);
        centerFrame();
        setVisible(true);
    }

    private void centerFrame() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension sd = toolkit.getScreenSize();
        Dimension fd = getSize();
        setLocation(sd.width / 2 - fd.width / 2, sd.height / 2 - fd.height / 2);
    }

    private void initGUI(Container container) {
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(2, 2));

        dogJCheckBox = new JCheckBox(names[0]);
        dogJCheckBox.addItemListener(this);
        p1.add(dogJCheckBox);

        catJCheckBox = new JCheckBox(names[1]);
        catJCheckBox.addItemListener(this);
        p1.add(catJCheckBox);

        sheepJCheckBox = new JCheckBox(names[2]);
        sheepJCheckBox.addItemListener(this);
        p1.add(sheepJCheckBox);

        chickenJCheckBox = new JCheckBox(names[3]);
        chickenJCheckBox.addItemListener(this);
        p1.add(chickenJCheckBox);

        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder loopTitle = BorderFactory.createTitledBorder(blackline, "Clip Looping");

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(p1);
        rightPanel.setBorder(loopTitle);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));

        playJButton = new JButton("Play");
        playJButton.addActionListener(this);
        p2.add(playJButton);

        pauseJButton = new JButton("Resume");
        Dimension dimension = pauseJButton.getMaximumSize();
        pauseJButton.setPreferredSize(dimension);
        pauseJButton.setMinimumSize(dimension);
        pauseJButton.setMaximumSize(dimension);
        pauseJButton.setText("Pause");
        pauseJButton.setEnabled(false);
        pauseJButton.addActionListener(this);
        p2.add(pauseJButton);
        isPauseButton = true;

        stopJButton = new JButton("Stop");
        stopJButton.addActionListener(this);
        p2.add(stopJButton);
        stopJButton.setEnabled(false);

        leftPanel.add(p2);

        loopJButton = new JButton("Loop");
        loopJButton.addActionListener(this);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(loopJButton);
        leftPanel.add(p3);

        namesJComboBox = new JComboBox(songNames);
        leftPanel.add(namesJComboBox);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.add(leftPanel);
        controlPanel.add(rightPanel);

        container.add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    public void atSequenceEnd(String filename, int status) {
        if (status == SoundsWatcher.STOPPED) {
            System.out.println(filename + " stopped");
        } else if (status == SoundsWatcher.REPLAYED) {
            System.out.println(filename + " replayed");
        } else {
            System.out.println(filename + " status code : " + status);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String songName = shortSongNames[namesJComboBox.getSelectedIndex()];

        if (e.getSource() == playJButton) {
            midisLoader.play(songName, false);
            playJButton.setEnabled(false);
            loopJButton.setEnabled(false);
            pauseJButton.setEnabled(true);
            stopJButton.setEnabled(true);
        } else if (e.getSource() == loopJButton) {
            midisLoader.play(songName, true);
            playJButton.setEnabled(false);
            background = loopJButton.getBackground();
            loopJButton.setBackground(lightGreen);
            loopJButton.setEnabled(false);
            pauseJButton.setEnabled(true);
            stopJButton.setEnabled(true);
        } else if (e.getSource() == pauseJButton) {
            if (isPauseButton) {
                midisLoader.pause();
                pauseJButton.setText("Resume");
                stopJButton.setEnabled(false);
            } else {
                midisLoader.resume();
                pauseJButton.setText("Pause");
                stopJButton.setEnabled(true);
            }
            isPauseButton = !isPauseButton;
        } else if (e.getSource() == stopJButton) {
            midisLoader.stop();
            playJButton.setEnabled(true);
            loopJButton.setEnabled(true);
            loopJButton.setBackground(background);
            pauseJButton.setEnabled(false);
            stopJButton.setEnabled(false);
        } else {
            System.out.println("Action unknown");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        String name = ((JCheckBox) e.getItem()).getText();
        boolean isSelected = (e.getStateChange() == e.SELECTED) ? true : false;
        boolean switched = false;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                clipLoops[i] = !clipLoops[i];
                switched = true;
                break;
            }
        }
        if (!switched) {
            System.out.println("Item unknown");
        } else {
            if (!isSelected) {
                clipsLoader.stop(name);
            }
        }
    }

    public void playClip(String name, int i) {
        clipsLoader.play(name, clipLoops[i]);
    }

    public static void main(String[] args) {
        new LoadersTests();
    }
}
