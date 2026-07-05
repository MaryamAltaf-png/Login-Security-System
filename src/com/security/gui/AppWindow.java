package com.security.gui;

import com.security.ai.AIModel;
import com.security.service.SecurityLog;
import com.security.service.SecuritySystem;
import com.security.service.UserDatabase;
import java.awt.*;
import javax.swing.*;

/**
 * AppWindow - the top-level JFrame that holds the GUI screens.
 * Demonstrates OOP: Composition (CardLayout panel switching), Encapsulation,
 * Dependency Injection of the backend security services.
 */
public class AppWindow extends JFrame {

    private static final String CARD_LOGIN     = "LOGIN";
    private static final String CARD_REGISTER  = "REGISTER";
    private static final String CARD_DASHBOARD = "DASHBOARD";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final DashboardPanel dashboardPanel;

    public AppWindow(SecuritySystem security, UserDatabase userDb,
                     SecurityLog log, AIModel ai) {
        super("AI-Based Device Login Security System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        cards.setBackground(Theme.BG_DARK);

        loginPanel     = new LoginPanel(this, security);
        registerPanel  = new RegisterPanel(this, userDb);
        dashboardPanel = new DashboardPanel(this, security, log, ai, userDb);

        cards.add(loginPanel,     CARD_LOGIN);
        cards.add(registerPanel,  CARD_REGISTER);
        cards.add(dashboardPanel, CARD_DASHBOARD);

        getContentPane().setBackground(Theme.BG_DARK);
        setContentPane(cards);

        showLogin();
    }

    public void showLogin() {
        loginPanel.clear();
        cardLayout.show(cards, CARD_LOGIN);
        loginPanel.focusUsername();
    }

    public void showRegister() {
        registerPanel.clear();
        cardLayout.show(cards, CARD_REGISTER);
    }

    public void showDashboard() {
        dashboardPanel.refresh();
        cardLayout.show(cards, CARD_DASHBOARD);
    }
}
