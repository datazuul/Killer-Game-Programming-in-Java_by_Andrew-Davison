package com.example.loaderstests;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ImagesLoader {

    private static final String IMAGE_DIR = "Images/";

    private HashMap imagesMap;
    private HashMap gNamesMap;

    private GraphicsConfiguration graphicsConfiguration;

    public ImagesLoader(String fnm) {
        initLoader();
        loadImagesFile(fnm);
    }

    public ImagesLoader() {
        initLoader();
    }

    private void initLoader() {
        imagesMap = new HashMap();
        gNamesMap = new HashMap();

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
    }

    private void loadImagesFile(String fnm) {
        String imsFNm = IMAGE_DIR + fnm;
        System.out.println("Reading file : " + imsFNm);
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(imsFNm);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            char ch;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("//")) {
                    continue;
                }
                ch = Character.toLowerCase(line.charAt(0));
                if (ch == 'o') {
                    getFileNameImage(line);
                } else if (ch == 'n') {
                    getNumberedImages(line);
                } else if (ch == 's') {
                    getStripImages(line);
                } else if (ch == 'g') {
                    getGroupImages(line);
                } else {
                    System.out.println("Do not recognize line : " + line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error reading file : " + imsFNm);
            System.exit(1);
        }
    }

    private void getFileNameImage(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() != 2) {
            System.out.println("Wrong no. of arguments for " + line);
        } else {
            tokenizer.nextToken();
            System.out.print("o Line : ");
            loadSingleImage(tokenizer.nextToken());
        }
    }

    public boolean loadSingleImage(String fnm) {
        String name = getPrefix(fnm);

        if (imagesMap.containsKey(name)) {
            System.out.println("Error : " + name + " already used");
            return false;
        }

        BufferedImage bufferedImage = loadImage(fnm);
        if (bufferedImage != null) {
            ArrayList imsList = new ArrayList();
            imsList.add(bufferedImage);
            imagesMap.put(name, imsList);
            System.out.println(" Stored " + name + "/" + fnm);
            return true;
        } else {
            return false;
        }
    }

    private String getPrefix(String fnm) {
        int posn;
        if ((posn = fnm.lastIndexOf(".")) == -1) {
            System.out.println("No prefix found for filename : " + fnm);
            return fnm;
        } else {
            return fnm.substring(0, posn);
        }
    }

    private void getNumberedImages(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (tokenizer.countTokens() != 3) {
            System.out.println("Wrong no. of arguments for " + line);
        } else {
            tokenizer.nextToken();
            System.out.print("n Line : ");

            String fnm = tokenizer.nextToken();
            int number = -1;
            try {
                number = Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                System.out.println("Number is incorrect for " + line);
            }

            loadNumImages(fnm, number);
        }
    }

    public int loadNumImages(String fnm, int number) {
        String prefix = null;
        String postfix = null;
        int startPosn = fnm.lastIndexOf("*");
        if (startPosn == -1) {
            System.out.println("No '*' in filename : " + fnm);
            prefix = getPrefix(fnm);
        } else {
            prefix = fnm.substring(0, startPosn);
            postfix = fnm.substring(startPosn + 1);
        }
        if (imagesMap.containsKey(prefix)) {
            System.out.println("Error : " + prefix + " already used");
            return 0;
        }
        return loadNumImages(prefix, postfix, number);
    }

    private int loadNumImages(String prefix, String postfix, int number) {
        String imFnm;
        BufferedImage bufferedImage;
        ArrayList imsList = new ArrayList();
        int loadCount = 0;

        if (number <= 0) {
            System.out.println("Error : Number <= 0 : " + number);
            imFnm = prefix + postfix;
            if ((bufferedImage = loadImage(imFnm)) != null) {
                loadCount++;
                imsList.add(bufferedImage);
                System.out.println(" Stored " + prefix + "/" + imFnm);
            }
        } else {
            System.out.println(" Adding " + prefix + "/" + prefix + "*" + postfix + "... ");
            for (int i = 0; i < number; i++) {
                imFnm = prefix + i + postfix;
                if ((bufferedImage = loadImage(imFnm)) != null) {
                    loadCount++;
                    imsList.add(bufferedImage);
                    System.out.print(i + " ");
                }
            }
            System.out.println();
        }

        if (loadCount == 0) {
            System.out.println("No images loaded for " + prefix);
        } else {
            imagesMap.put(prefix, imsList);
        }
        return loadCount;
    }

    private void getStripImages(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (tokenizer.countTokens() != 3) {
            System.out.println("Wrong no. of arguments for " + line);
        } else {
            tokenizer.nextToken();
            System.out.print("s Line : ");

            String fnm = tokenizer.nextToken();
            int number = -1;
            try {
                number = Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                System.out.println("Number is incorrect for " + line);
            }
            loadStripImages(fnm, number);
        }
    }

    public int loadStripImages(String fnm, int number) {
        String name = getPrefix(fnm);
        if (imagesMap.containsKey(name)) {
            System.out.println("Error : " + name + " already used");
            return 0;
        }

        BufferedImage[] strip = loadStripImageArray(fnm, number);
        if (strip == null) {
            return 0;
        }

        ArrayList imsList = new ArrayList();
        int loadCount = 0;
        System.out.print(" Adding " + name + "/" + fnm + "... ");
        for (int i = 0; i < strip.length; i++) {
            loadCount++;
            imsList.add(strip[i]);
            System.out.print(i + " ");
        }
        System.out.println();

        if (loadCount == 0) {
            System.out.println("No images loaded for " + name);
        } else {
            imagesMap.put(name, imsList);
        }
        return loadCount;
    }

    private void getGroupImages(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (tokenizer.countTokens() < 3) {
            System.out.println("Wrong no. of arguments for " + line);
        } else {
            tokenizer.nextToken();
            System.out.print("g Line : ");

            String name = tokenizer.nextToken();

            ArrayList fnms = new ArrayList();
            fnms.add(tokenizer.nextToken());
            while (tokenizer.hasMoreTokens()) {
                fnms.add(tokenizer.nextToken());
            }

            loadGroupImages(name, fnms);
        }
    }

    public int loadGroupImages(String name, ArrayList fnms) {
        if (imagesMap.containsKey(name)) {
            System.out.println("Error : " + name + " already used");
            return 0;
        }
        if (fnms.size() == 0) {
            System.out.println("List of filenames is empty");
            return 0;
        }

        BufferedImage bufferedImage;
        ArrayList nms = new ArrayList();
        ArrayList imsList = new ArrayList();
        String nm, fnm;
        int loadCount = 0;

        System.out.println(" Adding to " + name + "... ");
        System.out.print(" ");
        for (int i = 0; i < fnms.size(); i++) {
            fnm = (String) fnms.get(i);
            nm = getPrefix(fnm);
            if ((bufferedImage = loadImage(fnm)) != null) {
                loadCount++;
                imsList.add(bufferedImage);
                nms.add(nm);
                System.out.print(nm + "/" + fnm + " ");
            }
        }
        System.out.println();

        if (loadCount == 0) {
            System.out.println("No images loaded for " + name);
        } else {
            imagesMap.put(name, imsList);
            gNamesMap.put(name, nms);
        }

        return loadCount;
    }

    public int loadGroupImages(String name, String[] fnms) {
        ArrayList arrayList = new ArrayList(Arrays.asList(fnms));
        return loadGroupImages(name, arrayList);
    }

    public BufferedImage getImage(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            System.out.println("No image(s) stored under " + name);
            return null;
        }

        return (BufferedImage) imsList.get(0);
    }

    public BufferedImage getImage(String name, int posn) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            System.out.println("No image(s) stored under " + name);
            return null;
        }

        int size = imsList.size();
        if (posn < 0) {
            return (BufferedImage) imsList.get(0);
        } else if (posn >= size) {
            int newPosn = posn % size;
            return (BufferedImage) imsList.get(newPosn);
        }

        return (BufferedImage) imsList.get(posn);
    }

    public BufferedImage getImage(String name, String fnmPrefix) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            System.out.println("No image(s) stored under " + name);
            return null;
        }

        int posn = getGroupPosition(name, fnmPrefix);
        if (posn < 0) {
            return (BufferedImage) imsList.get(0);
        }
        return (BufferedImage) imsList.get(posn);
    }

    private int getGroupPosition(String name, String fnmPrefix) {
        ArrayList groupNames = (ArrayList) gNamesMap.get(name);
        if (groupNames == null) {
            System.out.println("No group names for " + name);
            return -1;
        }

        String nm;
        for (int i = 0; i < groupNames.size(); i++) {
            nm = (String) groupNames.get(i);
            if (nm.equals(fnmPrefix)) {
                return i;
            }
        }
        System.out.println("No " + fnmPrefix + " group name found for " + name);
        return -1;
    }

    public ArrayList getImages(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            System.out.println("No images(s) stored under " + name);
            return null;
        }

        System.out.println("Returning all images stored under " + name);
        return imsList;
    }

    public boolean isLoaded(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            return false;
        }
        return true;
    }

    public int numImages(String name) {
        ArrayList imsList = (ArrayList) imagesMap.get(name);
        if (imsList == null) {
            System.out.println("No image(s) stored under " + name);
            return 0;
        }

        return imsList.size();
    }

    public BufferedImage loadImage(String fnm) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(IMAGE_DIR + fnm));
            int transparency = image.getColorModel().getTransparency();
            BufferedImage copy = graphicsConfiguration.createCompatibleImage(image.getWidth(), image.getHeight(), transparency);

            Graphics2D graphics2D = copy.createGraphics();
            graphics2D.drawImage(image, 0, 0, null);
            graphics2D.dispose();
            return copy;
        } catch (IOException e) {
            System.out.println("Load image error for " + IMAGE_DIR + "/" + fnm + ":\n" + e);
            return null;
        }
    }

    private void reportTransparency(String fnm, int transparency) {
        System.out.print(fnm + " transparency : ");
        switch (transparency) {
            case Transparency.OPAQUE:
                System.out.println("opaque");
                break;
            case Transparency.BITMASK:
                System.out.println("bitmask");
                break;
            case Transparency.TRANSLUCENT:
                System.out.println("translucent");
                break;
            default:
                System.out.println("unknown");
                break;
        }
    }

    public BufferedImage[] loadStripImageArray(String fnm, int number) {
        if (number <= 0) {
            System.out.println("number <= 0; returning null");
            return null;
        }

        BufferedImage stripIm;
        if ((stripIm = loadImage(fnm)) == null) {
            System.out.println("Returning null");
            return null;
        }

        int imWidth = (int) stripIm.getWidth() / number;
        int height = stripIm.getHeight();
        int transparency = stripIm.getColorModel().getTransparency();

        BufferedImage[] strip = new BufferedImage[number];
        Graphics2D graphics2D;

        for (int i = 0; i < number; i++) {
            strip[i] = graphicsConfiguration.createCompatibleImage(imWidth, height, transparency);
            graphics2D = strip[i].createGraphics();
            graphics2D.drawImage(stripIm, 0, 0, imWidth, height, i * imWidth, 0, (i * imWidth) + imWidth, height, null);
            graphics2D.dispose();
        }

        return strip;
    }
}
