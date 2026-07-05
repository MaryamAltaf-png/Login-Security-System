package com.security.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * UIFactory - factory methods for creating themed Swing components.
 * Demonstrates OOP: Factory pattern, code reuse.
 */
public final class UIFactory {

    private UIFactory() {}

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JTextField textField(int columns) {
        JTextField f = new JTextField(columns);
        styleField(f);
        return f;
    }

    public static JPasswordField passwordField(int columns) {
        JPasswordField f = new JPasswordField(columns);
        styleField(f);
        return f;
    }

    private static void styleField(JTextField f) {
        f.setBackground(Theme.BG_INPUT);
        f.setForeground(Theme.TEXT_MAIN);
        f.setCaretColor(Theme.CYAN);
        f.setFont(Theme.BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    public static JButton primaryButton(String text) {
        return styledButton(text, Theme.CYAN_DARK, Theme.CYAN, Color.BLACK);
    }

    public static JButton successButton(String text) {
        return styledButton(text, Theme.GREEN_DARK, Theme.GREEN, Color.BLACK);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, Theme.RED_DARK, Theme.RED, Color.WHITE);
    }

    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BUTTON);
        b.setForeground(Theme.TEXT_DIM);
        b.setBackground(Theme.BG_PANEL);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER_LITE, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addHover(b, Theme.BG_PANEL, Theme.BG_CARD);
        return b;
    }

    private static JButton styledButton(String text, Color base, Color hover, Color fg) {
        JButton b = new JButton(text);
        b.setFont(Theme.BUTTON);
        b.setForeground(fg);
        b.setBackground(base);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addHover(b, base, hover);
        return b;
    }

    private static void addHover(JButton b, Color base, Color hover) {
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(base); }
        });
    }

    /** Card panel with rounded-look border and padding. */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(20, 24, 20, 24)));
        return p;
    }

    public static Border padding(int v, int h) {
        return new EmptyBorder(v, h, v, h);
    }

    /** Colored badge label (small pill). */
    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(Theme.SMALL.deriveFont(Font.BOLD));
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        return l;
    }
}
