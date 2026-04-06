package com.still_processing.UILib;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.still_processing.Application.MapPage.ConfinedMapView;
import com.still_processing.Application.MapPage.MapContainer;
import com.still_processing.FlightData.FlightInfo;
import com.still_processing.FlightData.Utils.MapHandler;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia, Zhou Sun
 */
public class ExpandablePanel extends JPanel implements Runnable, MouseListener {
    private final int FPS = 60;
    private volatile Thread expandableThread;

    private JPanel defaultDisplay;
    private JPanel expandedDisplay;
    private ToggleButton toggleButton;
    private JTextPane latenessTitle;
    private JTextPane lateness;

    private boolean isExpanded = false;
    private int toggleArrowAngle = 0;
    private int renderHeight = 0;

    private int padding = 20;
    private int borderRadius = 10;
    private Font titleFont = BOLD_FONT;
    private Font textFont = REGULAR_FONT;
    private int titleFontSize = 12;
    private int textFontSize = 12;

    private FontMetrics titleFontMetrics;
    private FontMetrics textFontMetrics;

    private final String FLIGHT_TITLE_TEXT = "Flight Number:";
    private final String ORIGIN_NAME_TITLE = "Origin Airport:";
    private final String DEST_NAME_TITLE = "Destination Airport:";
    private final String LATENESS_TITLE_TEXT = "Lateness:";
    private final String FLIGHT_DATE_TITLE_TEXT = "Flight Date:";
    private final String SCH_DEPT_TIME_TITLE_TEXT = "Scheduled Departure Time:";
    private final String ACT_DEPT_TIME_TITLE_TEXT = "Actual Departure Time:";
    private final String FLIGHT_CANCELLED_TITLE_TEXT = "Flight Canceled:";
    private final String SCH_ARR_TITLE_TEXT = "Scheduled Arrival Time:";
    private final String ACT_ARR_TITLE_TEXT = "Actual Arrival Time:";

    private String flightNumberText;
    private String depTime;
    private String arrTime;
    private String tripDuration;
    private String originIATA;
    private String destIATA;
    private String latenessText;
    private String flightDateText;
    private String schDepTimeText;
    private String actDepTimeText;
    private String flightCanceledText;
    private String schArrTimeText;
    private String actArrTimeText;

    private String originName;
    private String destName;

    private FlightInfo fInfo;
    private MapContainer mapContainer;
    private ConfinedMapView map;

    public ExpandablePanel(FlightInfo data, boolean dynamicTextMode) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        this.fInfo = data;

        this.flightNumberText = (data.flightNumber == null) ? data.plane.icao24.toUpperCase() : data.iataCode + data.flightNumber;
        this.depTime = data.depTime;
        this.arrTime = data.arrTime;
        int depHour = Integer.parseInt(depTime.substring(0, 2));
        int depMinutes = Integer.parseInt(depTime.substring(3, 5));
        int arrHour = Integer.parseInt(arrTime.substring(0, 2));
        int arrMinutes = Integer.parseInt(arrTime.substring(3, 5));
        int tripDurationHour = arrHour - depHour;
        int tripDurationMinutes = arrMinutes - depMinutes;
        if (tripDurationMinutes < 0) {
            tripDurationHour--;
            tripDurationMinutes += 60;
        }
        if (tripDurationHour < 0) {
            tripDurationHour += 24;
        }
        this.tripDuration = String.format("%dh%dm  ", tripDurationHour, tripDurationMinutes);
        this.originIATA = data.origin.iataCode;
        this.destIATA = data.dest.iataCode;
        this.latenessText = String.format("%.0f min", data.lateness);
        this.flightDateText = data.flightDate;
        this.schDepTimeText = data.depTime;
        this.actDepTimeText = data.CRSDepTime;
        this.flightCanceledText = (data.cancelled) ? "True" : "False";
        this.schArrTimeText = (data.CRSArrTime == null) ? "Not Available" : data.CRSArrTime;
        this.actArrTimeText = (data.arrTime == null) ? "Not Available" : data.arrTime;
        this.originName = data.origin.name;
        this.destName = data.dest.name;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(padding, padding, 0, padding));

        defaultDisplay = new JPanel();
        expandedDisplay = new JPanel();
        titleFontMetrics = getFontMetrics(titleFont.deriveFont(Font.PLAIN, titleFontSize + 1));
        textFontMetrics = getFontMetrics(textFont.deriveFont(Font.PLAIN, textFontSize + 1));
        defaultDisplay.setLayout(new BoxLayout(defaultDisplay, BoxLayout.X_AXIS));
        defaultDisplay.setOpaque(false);

        int titleHeight = titleFontMetrics.getHeight() / 2 + titleFontMetrics.getMaxAscent();
        int textHeight = textFontMetrics.getHeight() / 2 + textFontMetrics.getMaxAscent();

        JPanel flightNumberContainer = new JPanel();
        flightNumberContainer.setOpaque(false);
        flightNumberContainer.setLayout(new BoxLayout(flightNumberContainer, BoxLayout.Y_AXIS));
        JTextPane flightNumberTitle = new TextPaneBuilder()
                .setText((data.flightNumber != null) ? FLIGHT_TITLE_TEXT : "Flight Code:")
                .setFont(titleFont)
                .build();
        JTextPane flightNumber = new TextPaneBuilder()
                .setText(flightNumberText)
                .setFont(textFont)
                .build();
        flightNumberTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        FLIGHT_TITLE_TEXT),
                        titleHeight));
        flightNumber.setMaximumSize(new Dimension(
                titleFontMetrics.stringWidth(flightNumberText),
                textHeight));
        flightNumberContainer.add(flightNumberTitle);
        flightNumberContainer.add(flightNumber);

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
                .setFont(titleFont)
                .build();
        JTextPane originIATAPane = new TextPaneBuilder()
                .setText(originIATA)
                .setFont(textFont)
                .build();
        depTimePane.setMaximumSize(new Dimension(titleFontMetrics.stringWidth(depTime) + 5,
                titleHeight));
        originIATAPane.setMaximumSize(new Dimension(titleFontMetrics.stringWidth(originIATA),
                titleHeight));
        originInfo.add(depTimePane);
        originInfo.add(originIATAPane);

        JTextPane tripDurationPane = new TextPaneBuilder()
                .setText(tripDuration)
                .setFont(titleFont)
                .build();
        tripDurationPane.setMaximumSize(new Dimension(
                titleFontMetrics.stringWidth(tripDuration),
                titleHeight));
        tripDurationInfo.add(tripDurationPane);
        tripDurationInfo.add(new CustomLine(150, 20));

        JTextPane arrTimePane = new TextPaneBuilder()
                .setText(arrTime)
                .setFont(titleFont)
                .build();
        JTextPane destIATAPane = new TextPaneBuilder()
                .setText(destIATA)
                .setFont(textFont)
                .build();
        arrTimePane.setMaximumSize(new Dimension(titleFontMetrics.stringWidth(arrTime) + 5,
                titleHeight));
        destIATAPane.setMaximumSize(new Dimension(titleFontMetrics.stringWidth(destIATA),
                titleHeight));
        destInfo.add(arrTimePane);
        destInfo.add(destIATAPane);

        tripInfoContainer.add(originInfo);
        tripInfoContainer.add(Box.createRigidArea(new Dimension(20, 60)));
        tripInfoContainer.add(tripDurationInfo);
        tripInfoContainer.add(Box.createRigidArea(new Dimension(20, 60)));
        tripInfoContainer.add(destInfo);

        JPanel latenessContainer = new JPanel();
        latenessContainer.setOpaque(false);
        latenessContainer.setLayout(new BoxLayout(latenessContainer, BoxLayout.Y_AXIS));
        latenessTitle = new TextPaneBuilder()
                .setText(LATENESS_TITLE_TEXT)
                .setFont(titleFont)
                .setFontSize(12)
                .build();
        lateness = new TextPaneBuilder()
                .setText(latenessText)
                .setFont(textFont)
                .build();

        latenessTitle.setText((dynamicTextMode) ? "Lateness:" : "Distance:");
        lateness.setText((dynamicTextMode) ? String.format("%.0f min", fInfo.lateness) : String.format("%.1f km", fInfo.distance));
        latenessTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        latenessTitle.getText() + 5),
                        titleHeight));
        lateness.setMaximumSize(new Dimension(titleFontMetrics.stringWidth(lateness.getText()) + 25,
                titleHeight));
        latenessContainer.add(latenessTitle);
        latenessContainer.add(lateness);

        toggleButton = new ToggleButton(40, 40);
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
        expandedDisplay.setLayout(new BoxLayout(expandedDisplay, BoxLayout.X_AXIS));
        MapContainer mapFrame = new MapContainer();
        mapFrame.setLayout(new BoxLayout(mapFrame, BoxLayout.Y_AXIS));
        mapFrame.setPreferredSize(new Dimension(360, 180));
        mapFrame.setMinimumSize(new Dimension(360, 180));
        mapFrame.setMaximumSize(new Dimension(360, 180));
        this.mapContainer = mapFrame;

        // mapFrame.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftColumnText = new JPanel();
        leftColumnText.setOpaque(false);
        leftColumnText.setLayout(new BoxLayout(leftColumnText, BoxLayout.Y_AXIS));
        JTextPane flightDateTitle = new TextPaneBuilder()
                .setText(FLIGHT_DATE_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane schDepTimeTitle = new TextPaneBuilder()
                .setText(SCH_DEPT_TIME_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane actDepTimeTitle = new TextPaneBuilder()
                .setText(ACT_DEPT_TIME_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane originNameTitle = new TextPaneBuilder()
                .setText(ORIGIN_NAME_TITLE)
                .setFont(titleFont)
                .build();
        JTextPane flightDate = new TextPaneBuilder()
                .setText(flightDateText)
                .setFont(textFont)
                .build();
        JTextPane originNameText = new TextPaneBuilder()
                .setText(this.originName)
                .setFont(textFont)
                .build();
        JTextPane schDepTime = new TextPaneBuilder()
                .setText(schDepTimeText)
                .setFont(textFont)
                .build();
        JTextPane actDepTime = new TextPaneBuilder()
                .setText(actDepTimeText)
                .setFont(textFont)
                .build();

        flightDateTitle
                .setMaximumSize(new Dimension(titleFontMetrics
                        .stringWidth(FLIGHT_DATE_TITLE_TEXT)
                        + 10,
                        titleHeight));
        schDepTimeTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        SCH_DEPT_TIME_TITLE_TEXT),
                        titleHeight));
        actDepTimeTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        ACT_DEPT_TIME_TITLE_TEXT),
                        titleHeight));
        flightDate.setMaximumSize(new Dimension(textFontMetrics.stringWidth(flightDateText),
                textHeight));
        schDepTime.setMaximumSize(new Dimension(textFontMetrics.stringWidth(schDepTimeText),
                textHeight));
        actDepTime.setMaximumSize(new Dimension(
                textFontMetrics.stringWidth(ACT_DEPT_TIME_TITLE_TEXT),
                textHeight));

        flightDateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        schDepTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        actDepTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        originNameTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        flightDate.setAlignmentX(Component.LEFT_ALIGNMENT);
        schDepTime.setAlignmentX(Component.LEFT_ALIGNMENT);
        actDepTime.setAlignmentX(Component.LEFT_ALIGNMENT);
        originNameText.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftColumnText.add(originNameTitle);
        leftColumnText.add(originNameText);
        leftColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        leftColumnText.add(flightDateTitle);
        leftColumnText.add(flightDate);
        leftColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        leftColumnText.add(schDepTimeTitle);
        leftColumnText.add(schDepTime);
        leftColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        leftColumnText.add(actDepTimeTitle);
        leftColumnText.add(actDepTime);
        leftColumnText.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel rightColumnText = new JPanel();
        rightColumnText.setOpaque(false);
        rightColumnText.setLayout(new BoxLayout(rightColumnText, BoxLayout.Y_AXIS));
        JTextPane destNameTitle = new TextPaneBuilder()
                .setText(DEST_NAME_TITLE)
                .setFont(titleFont)
                .build();
        JTextPane flightCanceledTitle = new TextPaneBuilder()
                .setText(FLIGHT_CANCELLED_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane schArrTimeTitle = new TextPaneBuilder()
                .setText(SCH_ARR_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane actArrTimeTitle = new TextPaneBuilder()
                .setText(ACT_ARR_TITLE_TEXT)
                .setFont(titleFont)
                .build();
        JTextPane flightCanceled = new TextPaneBuilder()
                .setText(flightCanceledText)
                .setFont(textFont)
                .build();
        JTextPane schArrTime = new TextPaneBuilder()
                .setText(schArrTimeText)
                .setFont(textFont)
                .build();
        JTextPane actArrTime = new TextPaneBuilder()
                .setText(actArrTimeText)
                .setFont(textFont)
                .build();
        JTextPane destNameText = new TextPaneBuilder()
                .setText(this.destName)
                .setFont(textFont)
                .build();

        flightCanceledTitle
                .setMaximumSize(new Dimension(
                        titleFontMetrics.stringWidth(FLIGHT_CANCELLED_TITLE_TEXT)
                                + 10,
                        titleHeight));
        schArrTimeTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        SCH_ARR_TITLE_TEXT),
                        titleHeight));
        actArrTimeTitle.setMaximumSize(
                new Dimension(titleFontMetrics.stringWidth(
                        ACT_ARR_TITLE_TEXT),
                        titleHeight));
        flightCanceled.setMaximumSize(
                new Dimension(textFontMetrics.stringWidth(
                        flightCanceledText),
                        textHeight));
        schArrTime.setMaximumSize(new Dimension(textFontMetrics.stringWidth(schArrTimeText),
                textHeight));
        actArrTime.setMaximumSize(new Dimension(
                textFontMetrics.stringWidth(ACT_ARR_TITLE_TEXT),
                textHeight));

        flightCanceledTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        schArrTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        actArrTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        destNameTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        flightCanceled.setAlignmentX(Component.LEFT_ALIGNMENT);
        schArrTime.setAlignmentX(Component.LEFT_ALIGNMENT);
        actArrTime.setAlignmentX(Component.LEFT_ALIGNMENT);
        destNameText.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightColumnText.add(destNameTitle);
        rightColumnText.add(destNameText);
        rightColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        rightColumnText.add(flightCanceledTitle);
        rightColumnText.add(flightCanceled);
        rightColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        rightColumnText.add(schArrTimeTitle);
        rightColumnText.add(schArrTime);
        rightColumnText.add(Box.createRigidArea(new Dimension(0, 10)));
        rightColumnText.add(actArrTimeTitle);
        rightColumnText.add(actArrTime);
        rightColumnText.add(Box.createRigidArea(new Dimension(0, 10)));

        expandedDisplay.add(Box.createRigidArea(new Dimension(20, 0)));
        expandedDisplay.add(leftColumnText);
        expandedDisplay.add(Box.createHorizontalGlue());
        expandedDisplay.add(rightColumnText);
        expandedDisplay.add(Box.createHorizontalGlue());
        expandedDisplay.add(mapFrame);
        expandedDisplay.add(Box.createRigidArea(new Dimension(20, 0)));

        expandedDisplay.setPreferredSize(new Dimension(Integer.MAX_VALUE, renderHeight));
        this.add(expandedDisplay);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (expandableThread == null) {
            expandableThread = new Thread(this);
            expandableThread.start();
        }

        isExpanded = !isExpanded;
        this.mapContainer.parentExpanded = !this.mapContainer.parentExpanded;
        if (!isExpanded) {
            MapHandler.cacheInfoMap(this.map, this.mapContainer);
        } else {
            this.map = MapHandler.getInfoMap(this.fInfo);
            this.mapContainer.add(this.map);
        }
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
                remainingTime /= 1_000_000.0; // Converts to
                                              // milliseconds
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
        int maxHeight = 220;
        if (isExpanded) {
            renderHeight += (renderHeight < maxHeight) ? 20 : 0;
            renderHeight = (renderHeight > maxHeight) ? maxHeight : renderHeight;
            toggleArrowAngle += (toggleArrowAngle < 180) ? 10 : 0;
            toggleArrowAngle = (toggleArrowAngle > 180) ? 90 : toggleArrowAngle;

            if (renderHeight == maxHeight && toggleArrowAngle == 180) {
                expandableThread = null;
            }
        } else {
            renderHeight -= (renderHeight > 0) ? 20 : 0;
            renderHeight = (renderHeight < 0) ? 0 : renderHeight;
            toggleArrowAngle -= (toggleArrowAngle > 0) ? 10 : 0;
            toggleArrowAngle = (toggleArrowAngle < 0) ? 0 : toggleArrowAngle;

            if (renderHeight == 0 && toggleArrowAngle == 0) {
                expandableThread = null;
            }
        }

        expandedDisplay.setPreferredSize(new Dimension(Integer.MAX_VALUE, renderHeight));
        expandedDisplay.revalidate();
        toggleButton.repaint();
        toggleButton.revalidate();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(GRAY);
        g2d.fillRoundRect(padding, padding, getWidth() - 2 * padding, getHeight() - padding,
                borderRadius,
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
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                BufferedImage image = ImageIO
                        .read(getClass().getResource("/Images/toggle-arrow-green.png"));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                AffineTransform original = g2d.getTransform();
                g2d.rotate(Math.toRadians(toggleArrowAngle), width / 2,
                        height / 2);
                g2d.drawImage(image, 0, 0, width, height, null);
                g2d.setTransform(original);
            } catch (Exception e) {
                e.printStackTrace();
            }

            g2d.setColor(HIGHLIGHT_90);
            g2d.fillOval(0, 0, width, height);
        }
    }

    private class CustomLine extends JPanel {
        private int width;
        private int height;

        public CustomLine(int width, int height) {
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
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                BufferedImage image = ImageIO.read(getClass()
                        .getResource("/Images/plane-black.png"));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                AffineTransform original = g2d.getTransform();
                g2d.rotate(Math.toRadians(90), width - height
                        + height / 2 - 5,
                        height / 2);
                g2d.drawImage(image, width - height - 5, 0, height,
                        height, null);
                g2d.setTransform(original);
            } catch (Exception e) {
                e.printStackTrace();
            }
            g2d.setColor(TEXT_COLOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(0, height / 2, width - height - 10, height / 2);
        }
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }
}
