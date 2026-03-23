package com.still_processing.UILib;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import static com.still_processing.DefaultSettings.Settings.BOLD_FONT;
import static com.still_processing.DefaultSettings.Settings.GRAY;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT;
import static com.still_processing.DefaultSettings.Settings.HIGHLIGHT_90;

/**
 * @author Deea Zaharia
 */
public class ExpandablePanel extends JPanel implements Runnable, MouseListener {
    private final int FPS = 60;
    private JPanel defaultDisplay;
    private JPanel expandedDisplay;
    private boolean isExpanded = false;
    private int renderHeight = 0;
    private Thread expandableThread;
    private int padding = 20;
    private int borderRadius = 10;

    public ExpandablePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        defaultDisplay = new JPanel();
        expandedDisplay = new JPanel();

        FontMetrics metrics = getFontMetrics(BOLD_FONT.deriveFont(13f));
        int textHeight = metrics.getHeight() / 2 + metrics.getMaxAscent();

        defaultDisplay.setLayout(new BoxLayout(defaultDisplay, BoxLayout.X_AXIS));
        defaultDisplay.setOpaque(false);

        String flightTitleText = "Flight Number";
        String flightNumberText = "RYR123";
        JPanel flightNumberContainer = new JPanel();
        flightNumberContainer.setOpaque(false);
        flightNumberContainer.setLayout(new BoxLayout(flightNumberContainer, BoxLayout.Y_AXIS));
        JTextPane flightNumberTitle = new TextPaneBuilder()
                .setText(flightTitleText)
                .setFont(BOLD_FONT)
                .build();
        JTextPane flightNumber = new TextPaneBuilder()
                .setText(flightNumberText)
                .setFont(BOLD_FONT)
                .build();
        flightNumberTitle.setMaximumSize(new Dimension(metrics.stringWidth(flightTitleText), textHeight));
        flightNumber.setMaximumSize(new Dimension(metrics.stringWidth(flightNumberText), textHeight));
        flightNumberContainer.add(flightNumberTitle);
        flightNumberContainer.add(flightNumber);

        String depTime = "10:10";
        String arrTime = "12:20";
        String tripDuration = "2h10m";
        String originIATA = "DUB";
        String destIATA = "LHR";
        JPanel tripInfoContainer = new JPanel();
        JPanel tripDurationInfo = new JPanel();
        JPanel originInfo = new JPanel();
        JPanel destInfo = new JPanel();
        tripInfoContainer.setLayout(new BoxLayout(tripInfoContainer, BoxLayout.X_AXIS));
        originInfo.setLayout(new BoxLayout(originInfo, BoxLayout.Y_AXIS));
        tripDurationInfo.setLayout(new BoxLayout(tripDurationInfo, BoxLayout.Y_AXIS));
        destInfo.setLayout(new BoxLayout(destInfo, BoxLayout.Y_AXIS));
        tripInfoContainer.setOpaque(false);
        tripDurationInfo.setOpaque(false);
        originInfo.setOpaque(false);
        destInfo.setOpaque(false);

        JTextPane depTimePane = new TextPaneBuilder()
                .setText(depTime)
                .setFont(BOLD_FONT)
                .build();
        JTextPane originIATAPane = new TextPaneBuilder()
                .setText(originIATA)
                .setFont(BOLD_FONT)
                .build();
        depTimePane.setMaximumSize(new Dimension(metrics.stringWidth(depTime), textHeight));
        originIATAPane.setMaximumSize(new Dimension(metrics.stringWidth(originIATA), textHeight));
        originInfo.add(depTimePane);
        originInfo.add(originIATAPane);

        String arrow = "-------------------->";
        JTextPane tripDurationPane = new TextPaneBuilder()
                .setText(tripDuration)
                .setFont(BOLD_FONT)
                .build();
        JTextPane line = new TextPaneBuilder()
                .setText(arrow)
                .setFont(BOLD_FONT)
                .build();
        tripDurationPane.setMaximumSize(new Dimension(metrics.stringWidth(tripDuration), textHeight));
        line.setMaximumSize(new Dimension(metrics.stringWidth(arrow), textHeight));
        tripDurationInfo.add(tripDurationPane);
        tripDurationInfo.add(line);

        JTextPane arrTimePane = new TextPaneBuilder()
                .setText(arrTime)
                .setFont(BOLD_FONT)
                .build();
        JTextPane destIATAPane = new TextPaneBuilder()
                .setText(destIATA)
                .setFont(BOLD_FONT)
                .build();
        arrTimePane.setMaximumSize(new Dimension(metrics.stringWidth(arrTime), textHeight));
        destIATAPane.setMaximumSize(new Dimension(metrics.stringWidth(destIATA), textHeight));
        destInfo.add(arrTimePane);
        destInfo.add(destIATAPane);

        tripInfoContainer.add(originInfo);
        tripInfoContainer.add(Box.createRigidArea(new Dimension(20, 60)));
        tripInfoContainer.add(tripDurationInfo);
        tripInfoContainer.add(Box.createRigidArea(new Dimension(20, 60)));
        tripInfoContainer.add(destInfo);

        String latenessTitleText = "Lateness";
        String latenessText = "10m";
        JPanel latenessContainer = new JPanel();
        latenessContainer.setOpaque(false);
        latenessContainer.setLayout(new BoxLayout(latenessContainer, BoxLayout.Y_AXIS));
        JTextPane latenessTitle = new TextPaneBuilder()
                .setText(latenessTitleText)
                .setFont(BOLD_FONT)
                .setFontSize(12)
                .build();
        JTextPane lateness = new TextPaneBuilder()
                .setText(latenessText)
                .setFont(BOLD_FONT)
                .build();
        latenessTitle.setMaximumSize(new Dimension(metrics.stringWidth(latenessTitleText), textHeight));
        lateness.setMaximumSize(new Dimension(metrics.stringWidth(latenessText), textHeight));
        latenessContainer.add(latenessTitle);
        latenessContainer.add(lateness);

        ToggleButton toggleButton = new ToggleButton(40, 40);
        toggleButton.addMouseListener(this);

        defaultDisplay.add(Box.createRigidArea(new Dimension(20, 60)));
        defaultDisplay.add(flightNumberContainer);
        defaultDisplay.add(Box.createHorizontalGlue());
        defaultDisplay.add(tripInfoContainer);
        defaultDisplay.add(Box.createHorizontalGlue());
        defaultDisplay.add(latenessContainer);
        defaultDisplay.add(Box.createRigidArea(new Dimension(20, 0)));
        defaultDisplay.add(toggleButton);
        defaultDisplay.add(Box.createRigidArea(new Dimension(20, 0)));

        this.add(defaultDisplay);

        expandedDisplay.setOpaque(false);
        JTextPane number = new TextPaneBuilder()
                .setText("Top Secret.........!!!")
                .setFont(BOLD_FONT)
                .build();
        expandedDisplay.add(number);
        expandedDisplay.addMouseListener(this);
        expandedDisplay.setPreferredSize(new Dimension(Integer.MAX_VALUE, renderHeight));
        expandableThread = new Thread(this);
        expandableThread.start();
        this.add(expandedDisplay);
        revalidate();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        isExpanded = !isExpanded;
        repaint();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (expandableThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1_000_000.0; // Converts to milliseconds
                remainingTime = (remainingTime < 0) ? 0 : remainingTime;

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException error) {
                error.printStackTrace();
            }

        }
    }

    /**
     * increments the renderPercentage (asynchronously)
     *
     * @author Zhou Sun
     */
    private void update() {
        int maxHeight = 300;
        if (isExpanded) {
            renderHeight += (renderHeight < maxHeight) ? 20 : 0;
            renderHeight = (renderHeight > maxHeight) ? maxHeight : renderHeight;
        } else {
            renderHeight -= (renderHeight > 0) ? 20 : 0;
            renderHeight = (renderHeight < 0) ? 0 : renderHeight;
        }
        expandedDisplay.setPreferredSize(new Dimension(Integer.MAX_VALUE, renderHeight));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(GRAY);
        g2d.fillRoundRect(padding, padding, getWidth() - 2 * padding, getHeight() - padding, borderRadius,
                borderRadius);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private class ToggleButton extends JPanel {
        private int width;
        private int height;

        public ToggleButton(int width, int height) {
            this.setPreferredSize(new Dimension(width, height));
            this.setMaximumSize(new Dimension(width, height));
            this.setOpaque(false);
            this.width = width;
            this.height = height;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(HIGHLIGHT_90);
            g2d.fillOval(0, 0, width, height);
        }
    }
}
