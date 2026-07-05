package com.security.service;

import com.security.ai.AIModel;
import com.security.model.IntruderAlert;
import com.security.model.LoginAttempt;
import com.security.model.User;
import com.security.util.ConsoleUI;
import java.util.List;

/**
 * SecuritySystem - core authentication and threat detection engine.
 * Demonstrates OOP: Composition, Dependency Injection, Single Responsibility
 */
public class SecuritySystem {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final UserDatabase userDatabase;
    private final SecurityLog securityLog;
    private final AIModel aiModel;
    private String currentSessionUser;

    public SecuritySystem(UserDatabase userDatabase, SecurityLog securityLog, AIModel aiModel) {
        this.userDatabase = userDatabase;
        this.securityLog = securityLog;
        this.aiModel = aiModel;
        this.currentSessionUser = null;
    }

    /**
     * Verifies password credentials and returns authentication result.
     */
    public boolean verifyPassword(String username, String password, String ipAddress) {
        User user = userDatabase.findUser(username);

        if (user == null) {
            LoginAttempt attempt = new LoginAttempt(username, LoginAttempt.Status.FAILED, ipAddress,
                    "Login failed: user does not exist.");
            securityLog.logAttempt(attempt);
            ConsoleUI.printError("Access Denied: Unknown username.");
            return false;
        }

        if (!user.isActive()) {
            LoginAttempt attempt = new LoginAttempt(username, LoginAttempt.Status.ACCOUNT_LOCKED, ipAddress,
                    "Login attempt on locked account.");
            securityLog.logAttempt(attempt);
            ConsoleUI.printError("Access Denied: Account is locked due to too many failed attempts.");
            return false;
        }

        if (!user.verifyPassword(password)) {
            user.recordFailedAttempt();
            int remaining = MAX_FAILED_ATTEMPTS - user.getFailedAttempts();

            if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                detectIntruder(user, ipAddress);
                return false;
            }

            LoginAttempt attempt = new LoginAttempt(username, LoginAttempt.Status.FAILED, ipAddress,
                    "Wrong password. Attempts remaining: " + remaining);
            securityLog.logAttempt(attempt);
            ConsoleUI.printError("Access Denied: Wrong password. " + remaining + " attempt(s) remaining.");
            return false;
        }

        user.recordLogin();
        this.currentSessionUser = user.getUsername();

        LoginAttempt attempt = new LoginAttempt(username, LoginAttempt.Status.SUCCESS, ipAddress,
                "Login successful. Role: " + user.getRole());
        securityLog.logAttempt(attempt);

        ConsoleUI.printSuccess("Access Granted! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");

        List<LoginAttempt> history = securityLog.getAttemptsForUser(username);
        if (history.size() > 1) {
            analyzeLoginPattern(history, username);
        }

        return true;
    }

    /**
     * Detects intruder and triggers AI-powered threat analysis.
     */
    public void detectIntruder(User user, String ipAddress) {
        ConsoleUI.printAlert("Intruder detected for account: " + user.getUsername());

        IntruderAlert alert = new IntruderAlert(user.getUsername(), ipAddress, user.getFailedAttempts());

        ConsoleUI.printInfo("Running AI threat analysis...");
        String aiAnalysis = aiModel.analyzeIntruderThreat(alert);
        alert.setAiAnalysis(aiAnalysis);

        securityLog.logIntruderAlert(alert);

        LoginAttempt attempt = new LoginAttempt(user.getUsername(), LoginAttempt.Status.INTRUDER_ALERT, ipAddress,
                "Account locked after " + MAX_FAILED_ATTEMPTS + " failed attempts.");
        securityLog.logAttempt(attempt);

        System.out.println();
        ConsoleUI.printAlert(alert.toReport());
    }

    /**
     * Analyzes login patterns using AI.
     */
    private void analyzeLoginPattern(List<LoginAttempt> history, String username) {
        if (history.stream().anyMatch(a -> a.getStatus() == LoginAttempt.Status.FAILED)) {
            ConsoleUI.printInfo("Analyzing login patterns with AI...");
            String analysis = aiModel.analyzeLoginPattern(history, username);
            ConsoleUI.printAI(analysis);
        }
    }

    /**
     * Logs the current session user out.
     */
    public boolean logout() {
        if (currentSessionUser == null) {
            ConsoleUI.printWarning("No active session to log out from.");
            return false;
        }
        ConsoleUI.printSuccess("User '" + currentSessionUser + "' has been logged out.");
        currentSessionUser = null;
        return true;
    }

    /**
     * Returns AI-generated security dashboard recommendation.
     */
    public void printSecurityDashboard() {
        ConsoleUI.printSectionHeader("Security Dashboard");
        int total = userDatabase.getTotalUsers();
        int locked = (int) userDatabase.getLockedAccountCount();
        int alerts = securityLog.getIntruderAlertCount();

        System.out.println("  Total Users     : " + total);
        System.out.println("  Locked Accounts : " + locked);
        System.out.println("  Intruder Alerts : " + alerts);
        System.out.println("  AI Model        : " + aiModel.getModelName() +
                (aiModel.isAvailable() ? " (Online)" : " (Offline mode)"));

        ConsoleUI.printInfo("Requesting AI security assessment...");
        String recommendation = aiModel.getSecurityRecommendation(total, locked, alerts);
        ConsoleUI.printAI(recommendation);
    }

    public boolean isLoggedIn() { return currentSessionUser != null; }
    public String getCurrentSessionUser() { return currentSessionUser; }
    public UserDatabase getUserDatabase() { return userDatabase; }
}
