package com.still_processing.Application.HomePage;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Scrollable;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.still_processing.UILib.ButtonBuilder;
import com.still_processing.UILib.ImagePanel;
import com.still_processing.UILib.TextPaneBuilder;
import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Zhou Sun, Deea Zaharia
 */
public class BodyPanel extends JPanel implements Scrollable {

    public BodyPanel(ActionListener a) {
        System.out.println("=== Body Panel ===");

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setSize(new Dimension(200, 200));

        this.add(Box.createRigidArea(new Dimension(0, 50)));

        ImagePanel logo = new ImagePanel("/Images/logo.png", 200, 200);
        String title = "Flight Analyser";
        JTextPane textPane = new TextPaneBuilder()
                .setText(title)
                .setFontSize(48)
                .setFont(BOLD_FONT)
                .build();
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        FontMetrics metrics = getFontMetrics(BOLD_FONT.deriveFont(48f));
        int textHeight = metrics.getHeight() / 2 + metrics.getMaxAscent();
        int textWidth = metrics.stringWidth(title);
        textPane.setSize(new Dimension(textWidth, textHeight));
        textPane.setMaximumSize(new Dimension(textWidth, textHeight));

        titlePanel.setOpaque(false);
        titlePanel.add(Box.createHorizontalGlue());
        titlePanel.add(logo);
        titlePanel.add(Box.createRigidArea(new Dimension(20, 0)));
        titlePanel.add(textPane);
        titlePanel.add(Box.createHorizontalGlue());
        this.add(titlePanel);

        this.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton button1 = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Analyse")
                .setFontSize(18)
                .build();
        button1.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton button2 = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Map View")
                .setFontSize(18)
                .build();
        button2.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JButton button3 = new ButtonBuilder()
                .setSize(25, 25)
                .setForeground(BACKGROUND)
                .setBackground(HIGHLIGHT)
                .setText("Search")
                .setFontSize(18)
                .build();
        button3.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        button3.setBounds(10, 10, 5, 5);

        button1.addActionListener(a);
        button2.addActionListener(a);
        button3.addActionListener(a);

        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.X_AXIS));
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(button1);
        buttonContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonContainer.add(button2);
        buttonContainer.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonContainer.add(button3);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.setOpaque(false);
        this.add(buttonContainer);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 32;
    }
}
