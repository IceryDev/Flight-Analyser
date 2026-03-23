package com.still_processing.UILib;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.still_processing.DefaultSettings.Settings.*;

/**
 * @author Deea Zaharia
 */


public class ExpandablePanel extends JPanel implements MouseListener {
    private JPanel defaultDisplay;
    private JPanel expandedDisplay;
    private boolean isExpanded = false;
    public ExpandablePanel(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        defaultDisplay = new JPanel();
        expandedDisplay = new JPanel();

        defaultDisplay.setSize(0, 300);
        defaultDisplay.setBackground(GRAY);
        JTextPane flightNumber = new TextPaneBuilder()
                .setText("Flight Number")
                .setFont(BOLD_FONT)
                .build();
        defaultDisplay.add(flightNumber);
        defaultDisplay.addMouseListener(this);
        this.add(defaultDisplay);

        expandedDisplay.setSize(0, 300);
        expandedDisplay.setBackground(GRAY);
        JTextPane number = new TextPaneBuilder()
                .setText("Number")
                .setFont(BOLD_FONT)
                .build();
        expandedDisplay.add(number);
        expandedDisplay.addMouseListener(this);
        this.add(expandedDisplay);
    }
    @Override
    public void mouseClicked(MouseEvent e){
        expandedDisplay.setVisible(false);
        isExpanded = !isExpanded;
        expandedDisplay.setVisible(isExpanded);

        revalidate();
        repaint();
    }
    @Override
    public  void mousePressed(MouseEvent e){
    }
    @Override
    public void mouseEntered(MouseEvent e){
    }
    @Override
    public void mouseExited(MouseEvent e){
    }
    @Override
    public void mouseReleased(MouseEvent e){
    }

}
