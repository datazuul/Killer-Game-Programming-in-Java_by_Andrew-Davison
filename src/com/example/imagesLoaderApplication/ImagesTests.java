package com.example.imagesLoaderApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class ImagesTests extends JPanel implements ActionListener, ImagesPlayerWatcher {

    private static final String IMS_FILE = "imsInfo.txt";
    private static final int PERIOD = 100;
    private static final int PANEL_WIDTH = 850;
    private static final int PANEL_HEIGHT = 400;

    private ImagesLoader imagesLoader;
    private int counter;
    private boolean justStarted;
    private ImageSFXs imageSFXs;

    private GraphicsDevice graphicsDevice;
    private int accelMemory;
    private DecimalFormat decimalFormat;

    private BufferedImage atomic, balls, bee, cheese, eyeChart, house, pumpkin, scooter, fighter, ufo, owl, basn8, basn16;
    private ImagesPlayer numbersPlayer, figurePlayer, carsPlayer, catsPlayer, kaboomPlayer;

    private BufferedImage teleImage = null;
    private BufferedImage zapImage = null;

    public ImagesTests() {
        decimalFormat = new DecimalFormat("0.0");

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

        accelMemory = graphicsDevice.getAvailableAcceleratedMemory();
        System.out.println("Initial Acc. Mem. : " + decimalFormat.format(((double) accelMemory) / (1024 * 1024)) + " MB");

        setBackground(Color.white);
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        imagesLoader = new ImagesLoader(IMS_FILE);
        imageSFXs = new ImageSFXs();
        initImages();

        counter = 0;
        justStarted = true;

        new Timer(PERIOD, this).start();
    }

    private void initImages() {
        atomic = imagesLoader.getImage("atomic");
        balls = imagesLoader.getImage("balls");
        bee = imagesLoader.getImage("bee");
        cheese = imagesLoader.getImage("cheese");
        eyeChart = imagesLoader.getImage("eyeChart");
        house = imagesLoader.getImage("house");
        pumpkin = imagesLoader.getImage("pumpkin");
        scooter = imagesLoader.getImage("scooter");
        ufo = imagesLoader.getImage("ufo");
        owl = imagesLoader.getImage("owl");
        basn8 = imagesLoader.getImage("basn6a08");
        basn16 = imagesLoader.getImage("basn6a16");

        numbersPlayer = new ImagesPlayer("numbers", PERIOD, 1, false, imagesLoader);
        numbersPlayer.setWatcher(this);

        figurePlayer = new ImagesPlayer("figure", PERIOD, 2, true, imagesLoader);
        carsPlayer = new ImagesPlayer("cars", PERIOD, 1, true, imagesLoader);
        catsPlayer = new ImagesPlayer("cats", PERIOD, 0.5, true, imagesLoader);
        kaboomPlayer = new ImagesPlayer("kaboom", PERIOD, 1.5, true, imagesLoader);

        fighter = imagesLoader.getImage("fighter", "left");
    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        if (justStarted) {
            justStarted = false;
        } else {
            imagesUpdate();
        }

        repaint();
    }

    //@Override
    public void sequenceEnded(String imageName) {
        System.out.println(imageName + " sequence has ended");
    }

    private void imagesUpdate() {
        numbersPlayer.updateTick();
        if (counter % 30 == 0) {
            numbersPlayer.restartAt(2);
        }

        figurePlayer.updateTick();

        carsPlayer.updateTick();
        catsPlayer.updateTick();
        kaboomPlayer.updateTick();

        updateFighter();
    }

    private void updateFighter() {
        int posn = counter % 4;
        switch (posn) {
            case 0:
                fighter = imagesLoader.getImage("fighter", "left");
                break;
            case 1:
                fighter = imagesLoader.getImage("fighter", "right");
                break;
            case 2:
                fighter = imagesLoader.getImage("fighter", "still");
                break;
            case 3:
                fighter = imagesLoader.getImage("fighter", "up");
                break;
            default:
                System.out.println("Unknown fighter group name");
                fighter = imagesLoader.getImage("fighter", "left");
                break;
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        graphics2D.setColor(Color.blue);
        graphics2D.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        rotatingImage(graphics2D, atomic, 10, 25);
        mixedImage(graphics2D, balls, 110, 25);
        teleImage = teleportImage(graphics2D, bee, teleImage, 210, 25);
        flippingImage(graphics2D, cheese, 310, 25);
        blurringImage(graphics2D, eyeChart, 410, 25);
        reddenImage(graphics2D, house, 540, 25);
        zapImage = zapImage(graphics2D, pumpkin, zapImage, 710, 25);
        brighteningImage(graphics2D, scooter, 10, 160);
        fadingImage(graphics2D, ufo, 110, 140);
        negatingImage(graphics2D, owl, 450, 250);
        mixedImage(graphics2D, basn8, 650, 250);
        resizingImage(graphics2D, basn16, 750, 250);

        drawImage(graphics2D, numbersPlayer.getCurrentImage(), 280, 140);
        drawImage(graphics2D, figurePlayer.getCurrentImage(), 550, 140);

        drawImage(graphics2D, catsPlayer.getCurrentImage(), 10, 235);
        drawImage(graphics2D, kaboomPlayer.getCurrentImage(), 150, 250);
        drawImage(graphics2D, carsPlayer.getCurrentImage(), 250, 250);

        drawImage(graphics2D, fighter, 350, 250);

        reportAccelMemory();

        counter = (counter + 1) % 100;
    }

    private void drawImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            graphics2D.setColor(Color.yellow);
            graphics2D.fillRect(x, y, 20, 20);
            graphics2D.setColor(Color.black);
            graphics2D.drawString("??", x + 10, y + 10);
        } else {
            graphics2D.drawImage(bufferedImage, x, y, this);
        }
    }

    private void reportAccelMemory() {
        int mem = graphicsDevice.getAvailableAcceleratedMemory();
        int memChange = mem - accelMemory;

        if (memChange != 0) {
            System.out.println(counter + ". Acc. Mem. : " + decimalFormat.format(((double) accelMemory) / (1024 * 1024)) +
                    " MB; Change : " + decimalFormat.format(((double) memChange) / 1024) + " K");
        }
        accelMemory = mem;
    }

    private void resizingImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        double sizeChange = (counter % 6) / 2.0 + 0.5;
        imageSFXs.drawResizedImage(graphics2D, bufferedImage, x, y, sizeChange, sizeChange);
    }

    private void flippingImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        BufferedImage flipIm = null;
        if (counter % 4 == 0) {
            flipIm = bufferedImage;
        } else if (counter % 4 == 1) {
            flipIm = imageSFXs.getFlippedImage(bufferedImage, ImageSFXs.HORIZONTAL_FLIP);
        } else if (counter % 4 == 2) {
            flipIm = imageSFXs.getFlippedImage(bufferedImage, ImageSFXs.VERTICAL_FLIP);
        } else {
            flipIm = imageSFXs.getFlippedImage(bufferedImage, ImageSFXs.DOUBLE_FLIP);
        }
        drawImage(graphics2D, flipIm, x, y);
    }

    private void fadingImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        float alpha = 1.0f - (((counter * 4) % 100) / 100.0f);
        imageSFXs.drawFadedImage(graphics2D, ufo, x, y, alpha);
    }

    private void rotatingImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        int angle = (counter * 10) % 360;
        BufferedImage rotIm = imageSFXs.getRotatedImage(bufferedImage, angle);
        drawImage(graphics2D, rotIm, x, y);
    }

    private void blurringImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        int fadeSize = (counter % 8) * 2 + 1;
        if (fadeSize == 1) {
            drawImage(graphics2D, bufferedImage, x, y);
        } else {
            imageSFXs.drawBlurredImage(graphics2D, bufferedImage, x, y, fadeSize);
        }
    }

    private void reddenImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        float brightness = 1.0f + (((float) counter % 21) / 10.0f);
        if (brightness == 1.0f) {
            drawImage(graphics2D, bufferedImage, x, y);
        } else {
            imageSFXs.drawRedderImage(graphics2D, bufferedImage, x, y, (float) brightness);
        }
    }

    private void brighteningImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        int brightness = counter % 9;
        if (brightness == 0) {
            drawImage(graphics2D, bufferedImage, x, y);
        } else {
            imageSFXs.drawBrighterImage(graphics2D, bufferedImage, x, y, (float) brightness);
        }
    }

    private void negatingImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (counter % 10 < 5) {
            imageSFXs.drawNegatedImage(graphics2D, bufferedImage, x, y);
        } else {
            drawImage(graphics2D, bufferedImage, x, y);
        }
    }

    private void mixedImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (counter % 10 < 5) {
            imageSFXs.drawMixedColouredImage(graphics2D, bufferedImage, x, y);
        } else {
            drawImage(graphics2D, bufferedImage, x, y);
        }
    }

    private BufferedImage teleportImage(Graphics2D graphics2D, BufferedImage bufferedImage, BufferedImage teleIm, int x, int y) {
        if (teleIm == null) {
            if (imageSFXs.hasAlpha(bufferedImage)) {
                teleIm = imageSFXs.copyImage(bufferedImage);
            } else {
                teleIm = imageSFXs.makeTransImage(bufferedImage);
            }
        }

        int eraseSteps = counter % 7;
        switch (eraseSteps) {
            case 0:
                if (imageSFXs.hasAlpha(bufferedImage)) {
                    teleIm = imageSFXs.copyImage(bufferedImage);
                } else {
                    teleIm = imageSFXs.makeTransImage(bufferedImage);
                }
                break;
            case 1:
                imageSFXs.eraseImageparts(teleImage, 11);
                break;
            case 2:
                imageSFXs.eraseImageparts(teleImage, 7);
                break;
            case 3:
                imageSFXs.eraseImageparts(teleImage, 5);
                break;
            case 4:
                imageSFXs.eraseImageparts(teleImage, 3);
                break;
            case 5:
                imageSFXs.eraseImageparts(teleImage, 2);
                break;
            case 6:
                imageSFXs.eraseImageparts(teleImage, 1);
                break;
            default:
                System.out.println("Unknown count for teleport");
                break;
        }
        drawImage(graphics2D, teleImage, x, y);
        return teleIm;
    }

    private BufferedImage zapImage(Graphics2D graphics2D, BufferedImage bufferedImage, BufferedImage zapIm, int x, int y) {
        if ((zapIm == null) || (counter % 11 == 0)) {
            zapIm = imageSFXs.copyImage(bufferedImage);
        } else {
            double likelihood = (counter % 11) / 10.0;
            imageSFXs.zapImageParts(zapIm, likelihood);
        }
        drawImage(graphics2D, zapIm, x, y);
        return zapIm;
    }

    public static void main(String args[]) {
        System.setProperty("sun.java2d.translaccel", "true");

        ImagesTests ttPanel = new ImagesTests();

        JFrame app = new JFrame("Images Tests");
        app.getContentPane().add(ttPanel, BorderLayout.CENTER);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        app.pack();
        app.setResizable(false);
        app.setVisible(true);
    }
}
