package com.example.loaderstests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ClipsLoader {

    private static final String SOUND_DIR = "Sounds/";

    private HashMap clipsMap;

    public ClipsLoader(String soundsFnm) {
        clipsMap = new HashMap();
        loadSoundsFile(soundsFnm);
    }

    public ClipsLoader() {
        clipsMap = new HashMap();
    }

    private void loadSoundsFile(String soundsFnm) {
        String sndsFNM = SOUND_DIR + soundsFnm;
        System.out.println("Reading file : " + sndsFNM);
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(sndsFNM);
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
                    System.out.println("Wrong no. of arguments for : " + line);
                } else {
                    name = tokenizer.nextToken();
                    fnm = tokenizer.nextToken();
                    load(name, fnm);
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading file : " + sndsFNM);
            System.exit(1);
        }
    }

    public void load(String name, String fnm) {
        if (clipsMap.containsKey(name)) {
            System.out.println("Error : " + name + " already stored");
        } else {
            clipsMap.put(name, new ClipInfo(name, fnm));
            System.out.println("-- " + name + "/" + fnm);
        }
    }

    public void close(String name) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.close();
        }
    }

    public void play(String name, boolean toLoop) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.play(toLoop);
        }
    }

    public void stop(String name) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.stop();
        }
    }

    public void pause(String name) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.pause();
        }
    }

    public void resume(String name) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.resume();
        }
    }

    public void setWatcher(String name, SoundsWatcher soundsWatcher) {
        ClipInfo clipInfo = (ClipInfo) clipsMap.get(name);
        if (clipInfo == null) {
            System.out.println("Error : " + name + " not stored");
        } else {
            clipInfo.setWatcher(soundsWatcher);
        }
    }
}
