package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.FlightFetcher;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Utils.LiveDataHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class MapSideOverlay extends JPanel implements Runnable{
    public final float TRANSPARENCY = 0.85f;
    public final int MARGIN = 15;
    public boolean isExpanded = false;
    public int maxWidth = 100;
    public int renderWidth = 0;

    private Thread animThread;

    private BufferedImage aircraftPanel;
    private final BufferedImage airlineLogo;
    private final BufferedImage planeMarker;
    private final MapViewFull mvf;

    public MapSideOverlay(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.mvf = LiveDataHandler.mvf;
        try {
            this.planeMarker = ImageIO.read(Objects.requireNonNull(getClass().getResource(Settings.PLANE_WHITE_ROT)));
            this.airlineLogo = ImageIO.read(Objects.requireNonNull(getClass().getResource(Settings.PLANE_TAIL)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
        g2d.setColor(Settings.TEXT_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int cumulativeY = MARGIN*2;
        int w = getWidth();
        FlightInfo tmpInfo = mvf.getSelectedInfo();
        g2d.setColor(Settings.BACKGROUND);
        g2d.setFont(Settings.REGULAR_FONT.deriveFont(18f));
        g2d.drawString("Flight: " + tmpInfo.plane.icao24.toUpperCase(), MARGIN, cumulativeY);
        int textHeight = g2d.getFontMetrics().getHeight()*2/3;
        cumulativeY += textHeight;

        if (this.aircraftPanel != null){
            int imgHeight = 9*(w-MARGIN*2)/16;
            g2d.drawImage(this.aircraftPanel, MARGIN, cumulativeY, w-MARGIN*2, imgHeight, null);
            cumulativeY += imgHeight + textHeight + MARGIN;
        }

        g2d.setFont(Settings.REGULAR_FONT.deriveFont(16f));
        g2d.drawString("Origin Airport:", MARGIN, cumulativeY);

        cumulativeY += (int) (textHeight*1.5);
        g2d.drawString(String.format("%s (%s)", tmpInfo.origin.name, tmpInfo.origin.iataCode), MARGIN, cumulativeY);

        if (this.planeMarker != null){
            g2d.drawImage(this.planeMarker, w/2-15, cumulativeY + (5*MARGIN-textHeight)/2, 30, 30, null);
        }

        cumulativeY += textHeight + 5*MARGIN;
        g2d.drawString("Destination Airport: ", MARGIN, cumulativeY);

        cumulativeY += (int) (textHeight*1.5);
        g2d.drawString(String.format("%s (%s)", tmpInfo.dest.name, tmpInfo.dest.iataCode), MARGIN, cumulativeY);

        if (this.airlineLogo != null){
            cumulativeY += 3*MARGIN;
            g2d.drawImage(this.airlineLogo, w/2-15, cumulativeY, 30, 30, null);
            cumulativeY += 30;
        }

        cumulativeY += MARGIN;
        g2d.drawString("Airline: ", MARGIN, cumulativeY);

        cumulativeY += (int) (textHeight*1.5);
        g2d.drawString(String.format("%s (%s)", tmpInfo.airline, tmpInfo.iataCode.toUpperCase()), MARGIN, cumulativeY);

        cumulativeY += textHeight/2 + MARGIN;
        g2d.drawLine(MARGIN, cumulativeY, w-MARGIN, cumulativeY);
        g2d.drawLine(w/2, cumulativeY, w/2, cumulativeY + 10*MARGIN);

        cumulativeY += (int) (textHeight*2) + MARGIN;
        g2d.drawString(String.format("Latitude: %.2f", tmpInfo.plane.latitude), MARGIN, cumulativeY);
        g2d.drawString(String.format("Angle: %.2f°", tmpInfo.plane.heading), w/2 + MARGIN, cumulativeY);

        cumulativeY += (int) (textHeight*2);
        g2d.drawString(String.format("Longitude: %.2f", tmpInfo.plane.longitude), MARGIN, cumulativeY);
        g2d.drawString(String.format("Vel: %.2f m/s", tmpInfo.plane.velocity), w/2 + MARGIN, cumulativeY);

        g2d.dispose();
        super.paintComponent(g);
        g.dispose();
    }

    private void update() {
        if (this.isExpanded) {
            this.renderWidth += (this.renderWidth < this.maxWidth) ? 20 : 0;
            this.renderWidth = Math.min(this.renderWidth, this.maxWidth);

            if (this.renderWidth == this.maxWidth) {
                this.animThread = null;
            }
        } else {
            this.renderWidth -= (this.renderWidth > 0) ? 20 : 0;
            this.renderWidth = Math.max(this.renderWidth, 0);

            if (this.renderWidth == 0) {
                this.animThread = null;
                LiveDataHandler.sidebarOverlay.setVisible(false);
            }
        }
    }

    public void setFlightToDisplay(FlightInfo fi){
        if (fi != null && fi.plane != null){
            System.out.println(fi.plane.icao24);
            BufferedImage tmp = FlightFetcher.fetchAircraftImage(fi.plane.icao24);
            if (tmp != null){
                this.aircraftPanel = tmp;
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void toggleDisplay(boolean show){
        if (show != this.isExpanded){
            if (show) LiveDataHandler.sidebarOverlay.setVisible(true);
            if (this.animThread == null) {
                this.animThread = new Thread(this);
                this.animThread.start();
            }

            this.isExpanded = !this.isExpanded;
        }
    }

    @Override
    public void run() {
        int FPS = 60;
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (this.animThread != null) {
            update();
            LiveDataHandler.ca.componentMoved(null);

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0; // Converts to milliseconds

                remainingTime = Math.max(remainingTime, 0);

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException error) {
                error.printStackTrace();
            }
        }
    }
}
