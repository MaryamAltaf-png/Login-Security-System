package com.security.gui;

import com.security.ai.AIModel;
import com.security.model.LoginAttempt;
import com.security.model.User;
import com.security.service.SecurityLog;
import com.security.service.SecuritySystem;
import com.security.service.UserDatabase;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * DashboardPanel - main user dashboard shown after successful login.
 * Demonstrates OOP: Composition, Inheritance, Polymorphism (status rendering).
 */
public class DashboardPanel extends JPanel {

    private final AppWindow app;
    private final SecuritySystem security;
    private final SecurityLog log;
    private final AIModel ai;
    private final UserDatabase userDb;

    private JLabel welcomeLabel;
    private JLabel roleBadge;
    private JLabel lastLoginLabel;
    private JLabel statTotalUsers;
    private JLabel statLockedAccounts;
    private JLabel statIntruderAlerts;
    private JLabel statAiStatus;
    private JTextArea aiAssessmentArea;
    private JPanel logsContainer;

    public DashboardPanel(AppWindow app, SecuritySystem security,
                          SecurityLog log, AIModel ai, UserDatabase userDb) {
        this.app = app;
        this.security = security;
        this.log = log;
        this.ai = ai;
        this.userDb = userDb;

        setBackground(Theme.BG_DARK);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 28, 20, 28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_DARK);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        JLabel icon = UIFactory.label("\uD83D\uDEE1", Theme.HEADING.deriveFont(22f), Theme.CYAN);
        JLabel title = UIFactory.label("Security Control Center", Theme.HEADING.deriveFont(20f), Theme.TEXT_MAIN);
        titleRow.add(icon); titleRow.add(title);

        welcomeLabel = UIFactory.label("Welcome back", Theme.SUBTITLE, Theme.TEXT_DIM);
        roleBadge = UIFactory.badge("USER", Theme.CYAN_DARK, Color.BLACK);

        JPanel welcomeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        welcomeRow.setOpaque(false);
        welcomeRow.add(welcomeLabel);
        welcomeRow.add(roleBadge);

        left.add(titleRow);
        left.add(Box.createVerticalStrut(4));
        left.add(welcomeRow);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton refresh = UIFactory.ghostButton("⟳ Refresh");
        refresh.addActionListener(e -> refresh());
        JButton logout = UIFactory.dangerButton("Sign Out");
        logout.addActionListener(e -> {
            security.logout();
            app.showLogin();
        });
        right.add(refresh);
        right.add(logout);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.BG_DARK);

        // Stats grid (4 across)
        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        statTotalUsers     = makeStatCard(stats, "Total Users",     "0",  Theme.CYAN,   "\uD83D\uDC65");
        statLockedAccounts = makeStatCard(stats, "Locked Accounts", "0",  Theme.RED,    "\uD83D\uDD12");
        statIntruderAlerts = makeStatCard(stats, "Intruder Alerts", "0",  Theme.AMBER,  "\u26A0");
        statAiStatus       = makeStatCard(stats, "AI Engine",       "—",  Theme.VIOLET, "\uD83E\uDD16");
        body.add(stats);
        body.add(Box.createVerticalStrut(16));

        // AI assessment + recent activity
        JPanel twoCol = new JPanel(new GridLayout(1, 2, 14, 0));
        twoCol.setOpaque(false);
        twoCol.setAlignmentX(Component.LEFT_ALIGNMENT);

        twoCol.add(buildAiPanel());
        twoCol.add(buildLogsPanel());

        body.add(twoCol);

        JScrollPane scroll = new JScrollPane(body,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel makeStatCard(JPanel parent, String name, String value, Color accent, String emoji) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel emojiLbl = UIFactory.label(emoji, Theme.HEADING.deriveFont(18f), accent);
        JLabel nameLbl = UIFactory.label(name, Theme.SMALL, Theme.TEXT_DIM);
        topRow.add(emojiLbl, BorderLayout.WEST);
        topRow.add(nameLbl, BorderLayout.EAST);

        JLabel valueLbl = UIFactory.label(value, Theme.TITLE, Theme.TEXT_MAIN);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(topRow);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLbl);
        parent.add(card);
        return valueLbl;
    }

    private JPanel buildAiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel headLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headLeft.setOpaque(false);
        headLeft.add(UIFactory.label("\uD83E\uDD16", Theme.HEADING.deriveFont(18f), Theme.VIOLET));
        headLeft.add(UIFactory.label("AI Security Assessment", Theme.HEADING, Theme.TEXT_MAIN));

        JButton analyze = UIFactory.primaryButton("Run Analysis");
        analyze.addActionListener(e -> runAiAnalysis());

        head.add(headLeft, BorderLayout.WEST);
        head.add(analyze, BorderLayout.EAST);

        aiAssessmentArea = new JTextArea(
                "Click 'Run Analysis' to request a fresh AI security assessment.");
        aiAssessmentArea.setEditable(false);
        aiAssessmentArea.setLineWrap(true);
        aiAssessmentArea.setWrapStyleWord(true);
        aiAssessmentArea.setBackground(Theme.BG_INPUT);
        aiAssessmentArea.setForeground(Theme.TEXT_MAIN);
        aiAssessmentArea.setFont(Theme.BODY);
        aiAssessmentArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(aiAssessmentArea);
        scroll.setBorder(new LineBorder(Theme.BORDER, 1, true));
        scroll.setPreferredSize(new Dimension(0, 260));
        scroll.getViewport().setBackground(Theme.BG_INPUT);

        lastLoginLabel = UIFactory.label("Last login: —", Theme.SMALL, Theme.TEXT_MUTED);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(scroll, BorderLayout.CENTER);
        center.add(lastLoginLabel, BorderLayout.SOUTH);

        panel.add(head, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        head.setOpaque(false);
        head.add(UIFactory.label("\uD83D\uDCCB", Theme.HEADING.deriveFont(18f), Theme.CYAN));
        head.add(UIFactory.label("Recent Activity", Theme.HEADING, Theme.TEXT_MAIN));

        logsContainer = new JPanel();
        logsContainer.setLayout(new BoxLayout(logsContainer, BoxLayout.Y_AXIS));
        logsContainer.setBackground(Theme.BG_INPUT);
        logsContainer.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(logsContainer);
        scroll.setBorder(new LineBorder(Theme.BORDER, 1, true));
        scroll.setPreferredSize(new Dimension(0, 290));
        scroll.getViewport().setBackground(Theme.BG_INPUT);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(head, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** Refreshes all dynamic data. Called when this panel is shown. */
    public void refresh() {
        String username = security.getCurrentSessionUser();
        if (username == null) return;

        User u = userDb.findUser(username);
        if (u != null) {
            welcomeLabel.setText("Welcome back, " + u.getUsername());
            roleBadge.setText(u.getRole());
            roleBadge.setBackground(u.getRole().equals("ADMIN") ? Theme.AMBER : Theme.CYAN);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastLoginLabel.setText(u.getLastLoginAt() != null
                    ? "Last login: " + u.getLastLoginAt().format(fmt)
                    : "Last login: this is your first session");
        }

        statTotalUsers.setText(String.valueOf(userDb.getTotalUsers()));
        statLockedAccounts.setText(String.valueOf(userDb.getLockedAccountCount()));
        statIntruderAlerts.setText(String.valueOf(log.getIntruderAlertCount()));
        statAiStatus.setText(ai.isAvailable() ? "Online" : "Offline");
        statAiStatus.setForeground(ai.isAvailable() ? Theme.GREEN : Theme.AMBER);

        renderLogs();
    }

    private void renderLogs() {
        logsContainer.removeAll();
        List<LoginAttempt> attempts = log.getRecentAttempts(15);
        if (attempts.isEmpty()) {
            JLabel empty = UIFactory.label("No activity yet.", Theme.BODY, Theme.TEXT_MUTED);
            empty.setBorder(new EmptyBorder(16, 8, 16, 8));
            logsContainer.add(empty);
        } else {
            for (int i = attempts.size() - 1; i >= 0; i--) {
                logsContainer.add(buildLogRow(attempts.get(i)));
                logsContainer.add(Box.createVerticalStrut(4));
            }
        }
        logsContainer.revalidate();
        logsContainer.repaint();
    }

    private JPanel buildLogRow(LoginAttempt a) {
        Color accent;
        String label;
        switch (a.getStatus()) {
            case SUCCESS:        accent = Theme.GREEN; label = "SUCCESS"; break;
            case FAILED:         accent = Theme.AMBER; label = "FAILED"; break;
            case INTRUDER_ALERT: accent = Theme.RED;   label = "INTRUDER"; break;
            case ACCOUNT_LOCKED: accent = Theme.RED;   label = "LOCKED"; break;
            default:             accent = Theme.TEXT_DIM; label = a.getStatus().name();
        }

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Theme.BG_CARD);
        row.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accent, 0, true),
                new EmptyBorder(8, 10, 8, 10)));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);
        topRow.add(UIFactory.badge(label, accent, Color.BLACK));
        topRow.add(UIFactory.label(a.getUsername(), Theme.BODY_BOLD, Theme.TEXT_MAIN));
        topRow.add(UIFactory.label("IP: " + a.getIpAddress(), Theme.SMALL, Theme.TEXT_DIM));

        JLabel msg = UIFactory.label(a.getMessage(), Theme.SMALL, Theme.TEXT_DIM);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JLabel time = UIFactory.label(a.getTimestamp().format(fmt), Theme.SMALL, Theme.TEXT_MUTED);

        left.add(topRow);
        left.add(Box.createVerticalStrut(2));
        left.add(msg);
        left.add(time);

        row.add(left, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return row;
    }

    private void runAiAnalysis() {
        aiAssessmentArea.setText("Contacting AI security model...");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                return ai.getSecurityRecommendation(
                        userDb.getTotalUsers(),
                        (int) userDb.getLockedAccountCount(),
                        log.getIntruderAlertCount());
            }
            @Override protected void done() {
                try {
                    aiAssessmentArea.setText(get());
                    aiAssessmentArea.setCaretPosition(0);
                } catch (Exception ex) {
                    aiAssessmentArea.setText("AI assessment failed: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
