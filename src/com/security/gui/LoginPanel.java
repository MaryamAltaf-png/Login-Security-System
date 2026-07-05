package com.security.gui;

import com.security.service.SecuritySystem;
import java.awt.*;
import javax.swing.*;

/**
 * LoginPanel - the sign-in screen.
 * Demonstrates OOP: Inheritance (extends JPanel), Composition, Event handling.
 */
public class LoginPanel extends JPanel {

    private final AppWindow app;
    private final SecuritySystem security;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginPanel(AppWindow app, SecuritySystem security) {
        this.app = app;
        this.security = security;

        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 640));

        // Header
        JLabel iconLabel = UIFactory.label("\uD83D\uDD12", Theme.TITLE.deriveFont(40f), Theme.CYAN);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UIFactory.label("Secure Access", Theme.TITLE, Theme.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIFactory.label(
                "AI-Powered Login Security System",
                Theme.SUBTITLE, Theme.TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username
        JLabel userLbl = UIFactory.label("USERNAME", Theme.SMALL.deriveFont(Font.BOLD), Theme.TEXT_DIM);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField = UIFactory.textField(24);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLbl = UIFactory.label("PASSWORD", Theme.SMALL.deriveFont(Font.BOLD), Theme.TEXT_DIM);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UIFactory.passwordField(24);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons
        JButton loginBtn = UIFactory.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> attemptLogin());

        JButton registerBtn = UIFactory.ghostButton("Create New Account");
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerBtn.addActionListener(e -> app.showRegister());

        // Status
        statusLabel = UIFactory.label(" ", Theme.SMALL, Theme.RED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Demo accounts hint
        JPanel demoBox = new JPanel();
        demoBox.setLayout(new BoxLayout(demoBox, BoxLayout.Y_AXIS));
        demoBox.setBackground(Theme.BG_CARD);
        demoBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                UIFactory.padding(10, 14)));
        demoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        demoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel demoTitle = UIFactory.label("DEMO ACCOUNTS",
                Theme.SMALL.deriveFont(Font.BOLD), Theme.CYAN);
        demoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        demoBox.add(demoTitle);
        demoBox.add(Box.createVerticalStrut(6));
        demoBox.add(demoLine("admin", "Admin@1234", "ADMIN"));
        demoBox.add(demoLine("alice", "Alice@Pass1", "USER"));
        demoBox.add(demoLine("bob",   "Bob@Secure2", "USER"));

        // Assemble
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(demoBox);

        // Allow Enter key to trigger login
        getRootPaneAction(loginBtn);

        add(card);
    }

    private JLabel demoLine(String user, String pass, String role) {
        JLabel l = new JLabel("• " + user + " / " + pass + "  (" + role + ")");
        l.setForeground(Theme.TEXT_DIM);
        l.setFont(Theme.MONO.deriveFont(11f));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void getRootPaneAction(JButton btn) {
        // Pressing Enter in either field triggers login
        java.awt.event.ActionListener enter = e -> btn.doClick();
        usernameField.addActionListener(enter);
        passwordField.addActionListener(enter);
    }

    public void clear() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(Theme.RED);
    }

    public void focusUsername() {
        SwingUtilities.invokeLater(usernameField::requestFocusInWindow);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.", Theme.RED);
            return;
        }

        showStatus("Verifying credentials...", Theme.CYAN);

        // Run on background thread so the UI doesn't freeze on AI calls
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                String ip = "127.0.0.1";
                return security.verifyPassword(username, password, ip);
            }

            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        showStatus("Access granted. Loading dashboard...", Theme.GREEN);
                        app.showDashboard();
                    } else {
                        var user = security.getUserDatabase().findUser(username);
                        if (user == null) {
                            showStatus("✗ Unknown username.", Theme.RED);
                        } else if (!user.isActive()) {
                            showStatus("✗ Account locked. Intruder protection engaged.", Theme.RED);
                        } else {
                            int remaining = 3 - user.getFailedAttempts();
                            showStatus("✗ Wrong password. " + remaining + " attempt(s) left.", Theme.RED);
                        }
                        passwordField.setText("");
                    }
                } catch (Exception ex) {
                    showStatus("Error: " + ex.getMessage(), Theme.RED);
                }
            }
        }.execute();
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}
