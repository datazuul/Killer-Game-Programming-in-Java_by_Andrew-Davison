package com.example.netfourbyfour;

import javax.media.j3d.Canvas3D;
import java.awt.*;

public class OverlayCanvas extends Canvas3D {

    private static final int XPOS = 5;
    private static final int YPOS = 15;
    private static final Font MESSAGEFONT = new Font("SansSerif", Font.BOLD, 12);

    private NetFourByFour netFourByFour;
    private String status;

    public OverlayCanvas(GraphicsConfiguration graphicsConfiguration, NetFourByFour netFourByFour) {
        super(graphicsConfiguration);
        this.netFourByFour = netFourByFour;
    }

    public void postSwap() {
        Graphics2D graphics2D = (Graphics2D) getGraphics();
        graphics2D.setColor(Color.red);
        graphics2D.setFont(MESSAGEFONT);

        if ((status = netFourByFour.getStatus()) != null) {
            graphics2D.drawString(status, XPOS, YPOS);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    public void repaint() {
        Graphics2D graphics2D = (Graphics2D) getGraphics();
        paint(graphics2D);
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        Toolkit.getDefaultToolkit().sync();
    }
}
