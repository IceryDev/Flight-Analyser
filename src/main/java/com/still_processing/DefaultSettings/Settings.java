package com.still_processing.DefaultSettings;

import java.awt.Font;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;

/**
 * Some reuseable default settings
 * 
 * @author Zhou Sun, Jagoda Kocszwara-Szuba, Ulaş İçer
 */
public class Settings {
    private Settings() {
    }

    public static final Font REGULAR_FONT = loadFont("/Orbitron/Orbitron-Regular.ttf");
    public static final Font BOLD_FONT = loadFont("/Orbitron/Orbitron-Bold.ttf");
    public static final Font SEMIBOLD_FONT = loadFont("/Orbitron/Orbitron-SemiBold.ttf");
    public static final Font MEDIUM_FONT = loadFont("/Orbitron/Orbitron-Medium.ttf");
    public static final Font BLACK_FONT = loadFont("/Orbitron/Orbitron-Black.ttf");
    public static final int FONT_SIZE = 12;

    public static final String ICON_PATH = "/Images/logo.png";
    public static final String PLANE_RED = "/Images/plane-red.png";

    public static final Color TEXT_COLOR = new Color(0x001917);
    public static final Color BACKGROUND = new Color(0xeff2f1);
    public static final Color HIGHLIGHT = new Color(0x01796f);
    public static final Color HIGHLIGHT_90 = new Color(1, 121, 111, 90);
    public static final Color GRAY = new Color(0xaaaaaa);
    public static final Color LABEL_COLOR = new Color(0x3A3939);

    public static final Color LIGHT_BLUE = new Color(0x4ECDC4);
    public static final Color LIGHT_GREEN = new Color(0x569C62);
    public static final Color CYAN = new Color(0x46A3F8);
    public static final Color DARK_CYAN = new Color(0x16599C);
    public static final Color LIGHT_BURGUNDY = new Color(0xFFC17676);
    public static final Color BURGUNDY = new Color(0x431928);

    public static final String API_KEY = Dotenv.load().get("FLIGHT_API");
    public static final String CLIENT_SECRET = Dotenv.load().get("CLIENT_SECRET"); // For OpenSky API
    public static final String USER_NAME_OPENSKY = Dotenv.load().get("CLIENT_ID"); // For OpenSky API

    /**
     * Loads the font from the resouces folder
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
