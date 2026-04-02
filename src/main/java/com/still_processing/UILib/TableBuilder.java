package com.still_processing.UILib;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import static com.still_processing.DefaultSettings.Settings.*;

public class TableBuilder {
    private JTable table;
    private JTableHeader header;
    private Color background = BACKGROUND;
    private Color headerBackground = BACKGROUND;
    private Color foreground = TEXT_COLOR;
    private Font font = REGULAR_FONT;
    private float fontSize = FONT_SIZE;

    /**
     * This build a table for the end user
     *
     * @param data        2d array of the table data
     * @param columnNames string array for the name of the column entries
     * 
     * @author Zhou Sun
     */
    public TableBuilder(Object[][] data, Object[] columnNames) {
        table = new JTable(data, columnNames) {
            // Disable editing of the table content
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Disable drag and drop
        table.getTableHeader().setReorderingAllowed(false);
        table.setDragEnabled(false);

        table.setShowGrid(false);
        table.setRowHeight(48);
        table.setIntercellSpacing(new Dimension(0, 0));

        for (int col = 0; col < table.getModel().getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setCellRenderer(defaultRenderer);
        }

        header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 48));
        header.setBackground(headerBackground);
        header.setForeground(TEXT_COLOR);
        header.setFont(font.deriveFont(fontSize));
    }

    public JTable build() {
        return table;
    }

    public JScrollPane buildPane() {
        JScrollPane scrollPane = ScrollPaneFactory.createHideScroll();
        scrollPane.getViewport().setBackground(background);
        scrollPane.setBorder(null);
        scrollPane.setViewportView(table);
        return scrollPane;
    }

    DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            super.getTableCellRendererComponent(t, val, sel, foc, row, col);

            setBackground(background);
            setForeground(foreground);
            setHorizontalAlignment(JLabel.CENTER);

            setFont(font.deriveFont(fontSize));

            setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, GRAY));
            setOpaque(true);
            return this;
        }
    };

    public TableBuilder setColumnWidth(int[] width) {
        for (int widthIndex = 0; widthIndex < width.length; widthIndex++) {
            table.getColumnModel().getColumn(widthIndex).setPreferredWidth(width[widthIndex]);
        }
        return this;
    }

    public TableBuilder setFontSize(float fontSize) {
        this.fontSize = fontSize;
        table.setFont(font.deriveFont(fontSize));
        header.setFont(font.deriveFont(fontSize));
        return this;
    }

    public TableBuilder setFont(Font font) {
        this.font = font;
        table.setFont(font.deriveFont(fontSize));
        header.setFont(font.deriveFont(fontSize));
        return this;
    }

}
