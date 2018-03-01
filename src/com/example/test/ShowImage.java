/*
package com.example.test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;

public class ShowImage extends JApplet {

    private Graphics2D graphics2D;
    private GraphicsConfiguration graphicsConfiguration;
    //private Image image;
    private BufferedImage bufferedImage;

    public void init() {
        //image = getImage(getDocumentBase(), "ball.gif");
        //image = Toolkit.getDefaultToolkit().getImage("...");
        //image = new ImageIcon(getDocumentBase() + "ball.gif").getImage();
        //image = new ImageIcon(getClass().getResource("ball.gif")).getImage();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("ball.gif"));
        bufferedImage = makeBIM(imageIcon.getImage(), imageIcon.getIconWidth(), imageIcon.getIconHeight());

        */
/*RescaleOp rescaleOp = new RescaleOp(-1.0f, 255f, null);
        BufferedImage destination = rescaleOp.filter(source, null);*//*


 */
/*GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();

        bufferedImage = loadImage("ball.gif");*//*


 */
/*try {
            bufferedImage = ImageIO.read(getClass().getResource("ball.gif"));
        } catch (IOException e) {
            System.out.println("Load Image Error : ");
        }*//*


 */
/*MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(image, 0);

        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            System.out.println("Download Error");
        }*//*

    }

    public BufferedImage loadImage(String fnm) {
        try {
            bufferedImage = ImageIO.read(getClass().getResource(fnm));

            int transparency = bufferedImage.getColorModel().getTransparency();
            BufferedImage copy = graphicsConfiguration.createCompatibleImage(bufferedImage.getWidth(), bufferedImage.getHeight(), transparency);

            graphics2D = copy.createGraphics();
            graphics2D.drawImage(bufferedImage, 0, 0, null);
            graphics2D.dispose();
            return copy;
        } catch (IOException e) {
            System.out.println("Load Image error for " + fnm + " :\n" + e);
            return null;
        }
    }

    private BufferedImage makeBIM(Image image, int width, int height) {
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        graphics2D = copy.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();
        return copy;
    }

    public void paint(Graphics graphics) {
        //graphics.drawImage(image, 0, 0, this);
        //graphics.drawImage(image, 0, 0, null);
        graphics.drawImage(bufferedImage, 0, 0, this);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponents(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        graphics2D.setPaint(Color.blue);
        Rectangle2D.Double square = new Rectangle2D.Double(10, 10, 350, 350);
        graphics2D.fill(square);
    }

    private void gameRender() {
        if (image == null) {
            image = createImage(PWIDTH, PHEIGHT);
            if (image == null) {
                System.out.println("image is null");
                return;
            } else {
                graphics2D = (Graphics2D) image.getGraphics();
            }
        }
        if (gameOver) {
            gameOverMessage(graphics2D);
        }
    }

    private void screenUpdate() {
        try {
            graphics2D = (Graphics2D) bufferStrategy.getDrawGraphics();
            gameRender(graphics2D);
            graphics2D.dispose();
        }
    }
}
*/
