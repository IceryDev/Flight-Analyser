package com.still_processing.DefaultSettings;

import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

/**
 * Some reuseable default settings
 * 
 * @author Zhou Sun, Jagoda Kocszwara-Szuba
 */
public class Settings {
    private Settings() {
    }

    public static final Font REGULAR_FONT = loadFont("/Orbitron/Orbitron-Regular.ttf");
    public static final Font BOLD_FONT = loadFont("/Orbitron/Orbitron-Bold.ttf");
    public static final Font SEMIBOLD_FONT = loadFont("/Orbitron/Orbitron-SemiBold.ttf");
    public static final Font MEDIUM_FONT = loadFont("/Orbitron/Orbitron-Medium.ttf");
    public static final Font BLACK_FONT = loadFont("/Orbitron/Orbitron-Black.ttf");

    public static final Color TEXT_COLOR = new Color(0x001917);
    public static final Color BACKGROUND = new Color(0xeff2f1);
    public static final Color HIGHLIGHT = new Color(0x01796f);
    public static final Color GRAY = new Color(0xd1d1d1);

    /**
     * Loads the font from the resouces folder
     *
     * @throws FontFormatException if problem with font file's structure or data
     * @throws IOException         if input file errors (usually file not found)
     *
     * @author Zhou Sun
     */
    private static Font loadFont(String path) {
        try {
            InputStream inputStream = Settings.class.getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 16);
        }
    }
}
