package com.example.imagesloaderapplication;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.Random;

public class ImageSFXs {

    public static final int VERTICAL_FLIP = 0;
    public static final int HORIZONTAL_FLIP = 1;
    public static final int DOUBLE_FLIP = 2;

    private GraphicsConfiguration graphicsConfiguration;

    private RescaleOp negetiveOp, negetiveOpTrans;
    private ConvolveOp blurOp;

    public ImageSFXs() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();

        initEffects();
    }

    private void initEffects() {
        negetiveOp = new RescaleOp(-1.0f, 255f, null);

        float[] negFactors = {-1.0f, -1.0f, -1.0f, 1.0f};
        float[] offsets = {255f, 255f, 255f, 0.0f};
        negetiveOpTrans = new RescaleOp(negFactors, offsets, null);

        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
                ninth, ninth, ninth,
                ninth, ninth, ninth,
                ninth, ninth, ninth
        };
        blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null);
    }

    public void drawResizedImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y, double widthChange, double heightChange) {
        if (bufferedImage == null) {
            System.out.println("drawResizedImage : input image is null");
            return;
        }
        if (widthChange <= 0) {
            System.out.println("width change can not <= 0");
            widthChange = 1;
        }
        if (heightChange <= 0) {
            System.out.println("height change can not <= 0");
            heightChange = 1;
        }

        int destWidth = (int) (bufferedImage.getWidth() * widthChange);
        int destHeight = (int) (bufferedImage.getHeight() * heightChange);

        int destX = x + bufferedImage.getWidth() / 2 - destWidth / 2;
        int destY = y + bufferedImage.getHeight() / 2 - destHeight / 2;

        graphics2D.drawImage(bufferedImage, destX, destY, destWidth, destHeight, null);
    }

    public BufferedImage getFlippedImage(BufferedImage bufferedImage, int flipKind) {
        if (bufferedImage == null) {
            System.out.println("getFlippedImage : input image is null");
            return null;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int transparency = bufferedImage.getColorModel().getTransparency();

        BufferedImage copy = graphicsConfiguration.createCompatibleImage(imageWidth, imageHeight, transparency);
        Graphics2D graphics2D = copy.createGraphics();

        renderFlip(graphics2D, bufferedImage, imageWidth, imageHeight, flipKind);
        graphics2D.dispose();

        return copy;
    }

    private void renderFlip(Graphics2D graphics2D, BufferedImage bufferedImage, int imageWidth, int imageHeight, int flipKind) {
        if (flipKind == VERTICAL_FLIP) {
            graphics2D.drawImage(bufferedImage, imageWidth, 0, 0, imageHeight, 0, 0, imageWidth, imageHeight, null);
        } else if (flipKind == HORIZONTAL_FLIP) {
            graphics2D.drawImage(bufferedImage, 0, imageHeight, imageWidth, 0, 0, 0, imageWidth, imageHeight, null);
        } else {
            graphics2D.drawImage(bufferedImage, imageWidth, imageHeight, 0, 0, 0, 0, imageWidth, imageHeight, null);
        }
    }

    public void drawVerticalFlip(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            System.out.println("drawVerticalFlip: input image is null");
            return;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        graphics2D.drawImage(bufferedImage, x + imageWidth, y, x, y + imageHeight, 0, 0, imageWidth, imageHeight, null);
    }

    public void drawHorizontalFlip(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            System.out.println("drawHorizontalFlip: input image is null");
            return;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        graphics2D.drawImage(bufferedImage, x, y + imageHeight, x + imageWidth, y, 0, 0, imageWidth, imageHeight, null);
    }

    public void drawFadedImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y, float alpha) {
        if (bufferedImage == null) {
            System.out.println("drawFadedImage : input image is null");
            return;
        }
        if (alpha < 0.0f) {
            System.out.println("Alpha must be >= 0; setting to 0");
            alpha = 0.0f;
        } else if (alpha > 1.0f) {
            System.out.println("Alpha must be <= 1.0f; setting to 1.0f");
            alpha = 1.0f;
        }

        Composite composite = graphics2D.getComposite();
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        graphics2D.drawImage(bufferedImage, x, y, null);
        graphics2D.setComposite(composite);
    }

    public BufferedImage getRotatedImage(BufferedImage src, int angle) {
        if (src == null) {
            System.out.println("getRotatedImage : input image is null");
            return null;
        }

        int transparency = src.getColorModel().getTransparency();
        BufferedImage dest = graphicsConfiguration.createCompatibleImage(src.getWidth(), src.getHeight(), transparency);
        Graphics2D graphics2D = dest.createGraphics();

        AffineTransform origAT = graphics2D.getTransform();
        AffineTransform rotation = new AffineTransform();

        rotation.rotate(Math.toRadians(angle), src.getWidth() / 2, src.getHeight() / 2);

        graphics2D.transform(rotation);
        graphics2D.drawImage(src, 0, 0, null);
        graphics2D.setTransform(origAT);
        graphics2D.dispose();

        return dest;
    }

    public void drawBlurredImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            System.out.println("getBlurredImage : input image is null");
            return;
        }
        graphics2D.drawImage(bufferedImage, blurOp, x, y);
    }

    public void drawBlurredImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y, int size) {
        if (bufferedImage == null) {
            System.out.println("getBlurredImage : input image is null");
            return;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        int maxSize = (imageWidth > imageHeight) ? imageWidth : imageHeight;

        if ((maxSize % 2) == 0) {
            maxSize--;
        }
        if ((size % 2) == 0) {
            size++;
            System.out.println("Blur size must be odd; adding 1 to make size = " + size);
        }
        if (size < 3) {
            System.out.println("Minimum blur size is 3");
            size = 3;
        } else if (size > maxSize) {
            System.out.println("Maximum blur size is " + maxSize);
            size = maxSize;
        }

        int numCoords = size * size;
        float blurFactor = 1.0f / (float) numCoords;

        float[] blurKernel = new float[numCoords];
        for (int i = 0; i < numCoords; i++) {
            blurKernel[i] = blurFactor;
        }

        ConvolveOp blurringOp = new ConvolveOp(new Kernel(size, size, blurKernel), ConvolveOp.EDGE_NO_OP, null);
        graphics2D.drawImage(bufferedImage, blurringOp, x, y);
    }

    public void drawRedderImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y, float brightness) {
        if (bufferedImage == null) {
            System.out.println("drawRedderImage : input image is null");
            return;
        }
        if (brightness < 0.0f) {
            System.out.println("Brightness must be >= 0; setting to 0");
            brightness = 0.0f;
        }

        short[] brighten = new short[256];
        short[] lessen = new short[256];
        short[] noChange = new short[256];

        for (int i = 0; i < 256; i++) {
            float brightValue = 64.0f + (brightness * i);
            if (brightValue > 255) {
                brightValue = 255.0f;
            }
            brighten[i] = (short) brightValue;
            lessen[i] = (short) ((float) i / brightness);
            noChange[i] = (short) i;
        }

        short[][] brightenRed;
        if (hasAlpha(bufferedImage)) {
            brightenRed = new short[4][];
            brightenRed[0] = brighten;
            brightenRed[1] = lessen;
            brightenRed[2] = lessen;
            brightenRed[3] = noChange;
        } else {
            brightenRed = new short[3][];
            brightenRed[0] = brighten;
            brightenRed[1] = lessen;
            brightenRed[2] = lessen;
        }

        LookupTable lookupTable = new ShortLookupTable(0, brightenRed);
        LookupOp brightenRedOp = new LookupOp(lookupTable, null);

        graphics2D.drawImage(bufferedImage, brightenRedOp, x, y);
    }

    public void drawBrighterImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y, float brightness) {
        if (bufferedImage == null) {
            System.out.println("drawBrighterImage : input image is null");
            return;
        }
        if (brightness < 0.0f) {
            System.out.println("Brightness must be >= 0; setting to 0.5");
            brightness = 0.5f;
        }

        RescaleOp brighterOp;
        if (hasAlpha(bufferedImage)) {
            float[] scaleFactors = {brightness, brightness, brightness, 1.0f};
            float[] offsets = {0.0f, 0.0f, 0.0f, 0.0f};
            brighterOp = new RescaleOp(scaleFactors, offsets, null);
        } else {
            brighterOp = new RescaleOp(brightness, 0, null);
        }
        graphics2D.drawImage(bufferedImage, brighterOp, x, y);
    }

    public void drawNegatedImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            System.out.println("drawNegatedImage : input image is null");
            return;
        }
        if (hasAlpha(bufferedImage)) {
            graphics2D.drawImage(bufferedImage, negetiveOpTrans, x, y);
        } else {
            graphics2D.drawImage(bufferedImage, negetiveOp, x, y);
        }
    }

    public void drawMixedColouredImage(Graphics2D graphics2D, BufferedImage bufferedImage, int x, int y) {
        if (bufferedImage == null) {
            System.out.println("drawMixedColouredImage: input image is null");
            return;
        }

        BandCombineOp changeColoursOp;
        Random random = new Random();

        if (hasAlpha(bufferedImage)) {
            float[][] colourMatrix = {
                    {1.0f, 0.0f, 0.0f, 0.0f},
                    {random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.0f},
                    {random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.0f},
                    {0.0f, 0.0f, 0.0f, 1.0f}
            };
            changeColoursOp = new BandCombineOp(colourMatrix, null);
        } else {
            float[][] colourMatrix = {
                    {1.0f, 0.0f, 0.0f},
                    {random.nextFloat(), random.nextFloat(), random.nextFloat()},
                    {random.nextFloat(), random.nextFloat(), random.nextFloat()},
            };
            changeColoursOp = new BandCombineOp(colourMatrix, null);
        }

        Raster sourceRaster = bufferedImage.getRaster();
        WritableRaster destRaster = changeColoursOp.filter(sourceRaster, null);
        BufferedImage newImage = new BufferedImage(bufferedImage.getColorModel(), destRaster, false, null);
        graphics2D.drawImage(newImage, x, y, null);
    }

    public void eraseImageparts(BufferedImage bufferedImage, int spacing) {
        if (bufferedImage == null) {
            System.out.println("eraseImageParts : input image is null");
            return;
        }

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        int[] pixels = new int[imageWidth * imageHeight];
        bufferedImage.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        int i = 0;
        while (i < pixels.length) {
            pixels[i] = 0;
            i = i + spacing;
        }
        bufferedImage.setRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
    }

    public void zapImageParts(BufferedImage bufferedImage, double likelihood) {
        if (bufferedImage == null) {
            System.out.println("zapImageParts: input image is null");
            return;
        }
        if ((likelihood < 0) || (likelihood > 1)) {
            System.out.println("likelihood must be in the range 0 to 1");
            likelihood = 0.5;
        }

        int redColor = 0xf90000;
        int yellowColor = 0xf9fd00;

        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();

        int[] pixels = new int[imageWidth * imageHeight];
        bufferedImage.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

        double random;
        for (int i = 0; i < pixels.length; i++) {
            random = Math.random();
            if (random <= likelihood) {
                if (random <= 15 * likelihood / 16) {
                    pixels[i] = pixels[i] | redColor;
                } else {
                    pixels[i] = pixels[i] | yellowColor;
                }
            }
        }
        bufferedImage.setRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
    }

    public boolean hasAlpha(BufferedImage bufferedImage) {
        if (bufferedImage == null) {
            return false;
        }

        int transparency = bufferedImage.getColorModel().getTransparency();

        if ((transparency == Transparency.BITMASK) || (transparency == Transparency.TRANSLUCENT)) {
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage copyImage(BufferedImage src) {
        if (src == null) {
            System.out.println("copyImage: input image is null");
            return null;
        }

        int transparency = src.getColorModel().getTransparency();
        BufferedImage copy = graphicsConfiguration.createCompatibleImage(src.getWidth(), src.getHeight(), transparency);

        Graphics2D graphics2D = copy.createGraphics();
        graphics2D.drawImage(src, 0, 0, null);
        graphics2D.dispose();

        return copy;
    }

    public BufferedImage makeTransImage(BufferedImage src) {
        if (src == null) {
            System.out.println("makeTransImage: input image is null");
            return null;
        }

        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = dest.createGraphics();

        graphics2D.drawImage(src, 0, 0, null);
        graphics2D.dispose();

        return dest;
    }
}
