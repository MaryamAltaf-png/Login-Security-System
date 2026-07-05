package com.security.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * IntruderAlert - captures intruder detection events.
 * Demonstrates OOP: Composition, Encapsulation
 */
public class IntruderAlert {

    private final String username;
    private final String ipAddress;
    private final LocalDateTime detectedAt;
    private final int failedAttemptCount;
    private String aiAnalysis;
    private String snapshotFilePath;

    public IntruderAlert(String username, String ipAddress, int failedAttemptCount) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.detectedAt = LocalDateTime.now();
        this.failedAttemptCount = failedAttemptCount;
        this.aiAnalysis = "Pending AI analysis...";
    }

    public String generateFileName() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "intruder_" + username + "_" + detectedAt.format(fmt) + ".txt";
    }

    public String toReport() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "========== INTRUDER ALERT REPORT ==========\n" +
               "Username Attempted : " + username + "\n" +
               "Source IP          : " + ipAddress + "\n" +
               "Detected At        : " + detectedAt.format(fmt) + "\n" +
               "Failed Attempts    : " + failedAttemptCount + "\n" +
               "AI Security Analysis:\n" + aiAnalysis + "\n" +
               (snapshotFilePath != null ? "Snapshot Saved     : " + snapshotFilePath + "\n" : "") +
               "============================================";
    }

    public void setAiAnalysis(String aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    public void setSnapshotFilePath(String path) { this.snapshotFilePath = path; }

    public String getUsername() { return username; }
    public String getIpAddress() { return ipAddress; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public int getFailedAttemptCount() { return failedAttemptCount; }
    public String getAiAnalysis() { return aiAnalysis; }
}
