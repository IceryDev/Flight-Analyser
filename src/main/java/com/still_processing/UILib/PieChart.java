package com.still_processing.UILib;

import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.RenderingHints;

public class PieChart extends JFrame implements runnable {
    public static final String chartTitle = "Pie Chart";
    Thread graphThread;
    private final int FPS = 60;
    private int height;

    public HashMap<String, Integer> getData() {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("RyanAir", 3);
        data.put("AerLingus", 5);
        data.put("Scoot", 3);
        data.put("Other Airlines", 3);
        return data;
    }

    private static class Slice {
        private String label;
        private float value;
        private Color color;

        private Slice(String label, float value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    private PiePanel piePanel;
    private JTextField  titleField;
    private DefaultTableModel tableModel;
    private JTable table;

    public PieChart() {
        super ("Name");

        this.setPreferredSize(new Dimension(0, height));
        this.setSize(Integer.MAX_VALUE, height);
        this.setDoubleBuffered(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(245, 246, 250));

        add(createTopBar(), BorderLayout.NORTH);
        add(createPiePanel(), BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);

        setSize(1000, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }
//    private JPanel createTopBar(){
//
//    }
//    private PiePanel createPiePanel(){
//        piePanel = new PiePanel();
//    }
//    private JPanel createInfoPanel(){
//
//    }



}
