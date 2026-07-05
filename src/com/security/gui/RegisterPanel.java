package com.security.gui;

import com.security.service.UserDatabase;
import java.awt.*;
import javax.swing.*;

/**
 * RegisterPanel - new account creation screen.
 * Demonstrates OOP: Inheritance, Encapsulation, Composition.
 */
public class RegisterPanel extends JPanel {

    private final AppWindow app;
    private final UserDatabase userDb;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JComboBox<String> roleBox;
    private final JLabel statusLabel;
    private final JLabel strengthLabel;

    public RegisterPanel(AppWindow app, UserDatabase userDb) {
        this.app = app;
        this.userDb = userDb;

        setBackground(Theme.BG_DARK);
        setLayout(new GridBagLayout());

        JPanel card = UIFactory.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 740));

        JLabel icon = UIFactory.label("\uD83D\uDC64", Theme.TITLE.deriveFont(36f), Theme.VIOLET);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UIFactory.label("Create Account", Theme.TITLE, Theme.TEXT_MAIN);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIFactory.label(
                "Register a new user with strong password protection",
                Theme.SUBTITLE, Theme.TEXT_DIM);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = UIFactory.textField(24);
        passwordField = UIFactory.passwordField(24);
        confirmField  = UIFactory.passwordField(24);
        roleBox = new JComboBox<>(new String[]{"USER", "ADMIN"});
        roleBox.setBackground(Theme.BG_INPUT);
        roleBox.setForeground(Theme.TEXT_MAIN);
        roleBox.setFont(Theme.BODY);
        roleBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);

        strengthLabel = UIFactory.label("Password must have 8+ chars, 1 uppercase, 1 digit",
                Theme.SMALL, Theme.TEXT_MUTED);
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateStrength(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
        });

        statusLabel = UIFactory.label(" ", Theme.SMALL, Theme.RED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton submit = UIFactory.successButton("Create Account");
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        submit.addActionListener(e -> doRegister());

        JButton back = UIFactory.ghostButton("← Back to Login");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        back.addActionListener(e -> app.showLogin());

        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(fieldLabel("USERNAME"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("PASSWORD"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(4));
        card.add(strengthLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("CONFIRM PASSWORD"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("ROLE"));
        card.add(Box.createVerticalStrut(4));
        card.add(roleBox);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(submit);
        card.add(Box.createVerticalStrut(8));
        card.add(back);

        add(card);
    }

    private JLabel fieldLabel(String text) {
        JLabel l = UIFactory.label(text, Theme.SMALL.deriveFont(Font.BOLD), Theme.TEXT_DIM);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void updateStrength() {
        String pw = new String(passwordField.getPassword());
        if (pw.isEmpty()) {
            strengthLabel.setText("Password must have 8+ chars, 1 uppercase, 1 digit");
            strengthLabel.setForeground(Theme.TEXT_MUTED);
            return;
        }
        boolean lenOk = pw.length() >= 8;
        boolean upper = pw.chars().anyMatch(Character::isUpperCase);
        boolean digit = pw.chars().anyMatch(Character::isDigit);
        int score = (lenOk ? 1 : 0) + (upper ? 1 : 0) + (digit ? 1 : 0);
        if (score == 3) {
            strengthLabel.setText("✓ Strong password");
            strengthLabel.setForeground(Theme.GREEN);
        } else if (score == 2) {
            strengthLabel.setText("○ Medium — add the missing requirement");
            strengthLabel.setForeground(Theme.AMBER);
        } else {
            strengthLabel.setText("✗ Weak — needs 8+ chars, uppercase, digit");
            strengthLabel.setForeground(Theme.RED);
        }
    }

    public void clear() {
        usernameField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        roleBox.setSelectedIndex(0);
        statusLabel.setText(" ");
        updateStrength();
    }

    private void doRegister() {
        String user = usernameField.getText().trim();
        String pw   = new String(passwordField.getPassword());
        String pw2  = new String(confirmField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (user.isEmpty()) { fail("Username is required."); return; }
        if (userDb.existsUser(user)) { fail("Username already taken."); return; }
        if (!pw.equals(pw2)) { fail("Passwords do not match."); return; }
        if (!userDb.isPasswordStrong(pw)) {
            fail("Password too weak (need 8+ chars, uppercase, digit).");
            return;
        }

        boolean ok = userDb.registerUser(user, pw, role);
        if (ok) {
            statusLabel.setForeground(Theme.GREEN);
            statusLabel.setText("✓ Account created. Returning to login...");
            Timer t = new Timer(1200, e -> app.showLogin());
            t.setRepeats(false);
            t.start();
        } else {
            fail("Registration failed.");
        }
    }

    private void fail(String msg) {
        statusLabel.setForeground(Theme.RED);
        statusLabel.setText("✗ " + msg);
    }
}
