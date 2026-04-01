package com.still_processing.Application.MapPage;

import com.still_processing.DefaultSettings.Settings;
import com.still_processing.FlightData.FlightFetcher;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Utils.LiveDataHandler;
import com.still_processing.UILib.ImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public class MapSideOverlay extends JPanel implements Runnable{
    public final float TRANSPARENCY = 0.85f;
    public final int MARGIN = 10;
    public boolean isExpanded = false;
    public int maxWidth = 100;
    public int renderWidth = 0;

    private Thread animThread;

    private BufferedImage aircraftPanel;

    public MapSideOverlay(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));






    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY));
        g2d.setColor(Settings.TEXT_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        if (this.aircraftPanel != null){
            g2d.drawImage(this.aircraftPanel, MARGIN, MARGIN, getWidth()-MARGIN*2, 9*(getWidth()-MARGIN*2)/16, null);
        }
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
