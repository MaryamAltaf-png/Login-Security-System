package com.security.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Theme - centralized color and font palette for the Swing GUI.
 * Matches the dark cybersecurity theme from the web preview.
 * Demonstrates OOP: Utility class pattern, Encapsulation of constants.
 */
public final class Theme {

    private Theme() {}

    // Backgrounds
    public static final Color BG_DARK     = new Color(0x0A, 0x0E, 0x1A);
    public static final Color BG_PANEL    = new Color(0x16, 0x1B, 0x2E);
    public static final Color BG_CARD     = new Color(0x1F, 0x26, 0x3F);
    public static final Color BG_INPUT    = new Color(0x0F, 0x14, 0x22);

    // Borders / dividers
    public static final Color BORDER      = new Color(0x2A, 0x33, 0x4F);
    public static final Color BORDER_LITE = new Color(0x3A, 0x45, 0x66);

    // Text
    public static final Color TEXT_MAIN   = new Color(0xE5, 0xE7, 0xEB);
    public static final Color TEXT_DIM    = new Color(0x94, 0xA3, 0xB8);
    public static final Color TEXT_MUTED  = new Color(0x64, 0x74, 0x8B);

    // Accents
    public static final Color CYAN        = new Color(0x22, 0xD3, 0xEE);
    public static final Color CYAN_DARK   = new Color(0x06, 0xB6, 0xD4);
    public static final Color GREEN       = new Color(0x10, 0xB9, 0x81);
    public static final Color GREEN_DARK  = new Color(0x05, 0x96, 0x69);
    public static final Color RED         = new Color(0xEF, 0x44, 0x44);
    public static final Color RED_DARK    = new Color(0xDC, 0x26, 0x26);
    public static final Color AMBER       = new Color(0xF5, 0x9E, 0x0B);
    public static final Color VIOLET      = new Color(0xA7, 0x8B, 0xFA);

    // Fonts
    public static final Font  TITLE       = new Font("SansSerif", Font.BOLD, 26);
    public static final Font  SUBTITLE    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font  HEADING     = new Font("SansSerif", Font.BOLD, 16);
    public static final Font  BODY        = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font  BODY_BOLD   = new Font("SansSerif", Font.BOLD, 13);
    public static final Font  SMALL       = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font  MONO        = new Font("Monospaced", Font.PLAIN, 12);
    public static final Font  MONO_BOLD   = new Font("Monospaced", Font.BOLD, 12);
    public static final Font  BUTTON      = new Font("SansSerif", Font.BOLD, 13);
}
