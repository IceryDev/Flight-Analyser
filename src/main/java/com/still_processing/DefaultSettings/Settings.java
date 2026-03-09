package com.still_processing.DefaultSettings;

import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class Settings {
    private Settings() {
    }

    public static final Font REGULAR_FONT = loadFont("/Orbitron/Orbitron-Regular.ttf");
    public static final Font BOLD_FONT = loadFont("/Orbitron/Orbitron-Bold.ttf");
    public static final Font SEMIBOLD_FONT = loadFont("/Orbitron/Orbitron-SemiBold.ttf");
    public static final Font MEDIUM_FONT = loadFont("/Orbitron/Orbitron-Medium.ttf");
    public static final Font BLACK_FONT = loadFont("/Orbitron/Orbitron-Black.ttf");

    public static final Color FOREGROUND = new Color(0x2e3440);
    public static final Color BACKGROUND = new Color(0xeceff4);
    public static final Color HIGHLIGHT = new Color(0x8fbcbb);
    public static final Color GRAY = new Color(0xa1acbf);

    private static Font loadFont(String path) {
        try {
            InputStream inputStream = Settings.class.getResourceAsStream(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return font.deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 16);
        }
    }
}
