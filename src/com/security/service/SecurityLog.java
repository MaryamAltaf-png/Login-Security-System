package com.security.service;

import com.security.model.IntruderAlert;
import com.security.model.LoginAttempt;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SecurityLog - manages logging of all login events and intruder alerts.
 * Demonstrates OOP: Single Responsibility, File I/O, Encapsulation
 */
public class SecurityLog {

    private static final String LOG_DIR = "logs";
    private static final String INTRUDER_DIR = "intruder_logs";
    private static final String LOG_FILE = LOG_DIR + "/login_attempts.txt";

    private final List<LoginAttempt> sessionAttempts;
    private int intruderAlertCount;

    public SecurityLog() {
        this.sessionAttempts = new ArrayList<>();
        this.intruderAlertCount = 0;
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(Paths.get(LOG_DIR));
            Files.createDirectories(Paths.get(INTRUDER_DIR));
        } catch (IOException e) {
            System.err.println("Warning: Could not create log directories: " + e.getMessage());
        }
    }

    public void logAttempt(LoginAttempt attempt) {
        sessionAttempts.add(attempt);
        appendToFile(LOG_FILE, attempt.toLogString());
    }

    public void logIntruderAlert(IntruderAlert alert) {
        intruderAlertCount++;
        String filePath = INTRUDER_DIR + "/" + alert.generateFileName();
        writeToFile(filePath, alert.toReport());
        appendToFile(LOG_FILE, "[INTRUDER ALERT] Account '" + alert.getUsername() +
                "' locked. Report saved to: " + filePath);
    }

    public void printSessionSummary() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║           SESSION ACTIVITY LOG           ║");
        System.out.println("╚══════════════════════════════════════════╝");

        if (sessionAttempts.isEmpty()) {
            System.out.println("  No login activity in this session.");
        } else {
            for (LoginAttempt a : sessionAttempts) {
                System.out.println("  " + a.toLogString());
            }
        }

        System.out.println("\n  Total attempts  : " + sessionAttempts.size());
        System.out.println("  Intruder alerts : " + intruderAlertCount);
        System.out.println("  Log file        : " + LOG_FILE);
    }

    public List<LoginAttempt> getAttemptsForUser(String username) {
        List<LoginAttempt> result = new ArrayList<>();
        for (LoginAttempt a : sessionAttempts) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                result.add(a);
            }
        }
        return result;
    }

    private void appendToFile(String path, String content) {
        try (FileWriter fw = new FileWriter(path, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(content);
        } catch (IOException e) {
            System.err.println("Warning: Could not write to log: " + e.getMessage());
        }
    }

    private void writeToFile(String path, String content) {
        try (FileWriter fw = new FileWriter(path, false);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(content);
        } catch (IOException e) {
            System.err.println("Warning: Could not write intruder report: " + e.getMessage());
        }
    }

    public int getIntruderAlertCount() { return intruderAlertCount; }
    public List<LoginAttempt> getSessionAttempts() { return sessionAttempts; }

    /**
     * Returns up to {@code limit} most recent login attempts (oldest first).
     */
    public List<LoginAttempt> getRecentAttempts(int limit) {
        int size = sessionAttempts.size();
        int from = Math.max(0, size - limit);
        return new ArrayList<>(sessionAttempts.subList(from, size));
    }
}
