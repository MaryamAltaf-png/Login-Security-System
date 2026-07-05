package com.security.ai;

import com.security.model.IntruderAlert;
import com.security.model.LoginAttempt;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * AIModel - integrates with OpenAI to analyze security threats.
 * Demonstrates OOP: Single Responsibility, Abstraction, Encapsulation
 */
public class AIModel {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4o-mini";

    private final String apiKey;
    private final HttpClient httpClient;
    private boolean available;

    public AIModel(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.available = (apiKey != null && !apiKey.isBlank() && !apiKey.equals("YOUR_OPENAI_API_KEY"));
    }

    /**
     * Analyzes an intruder alert and returns a security assessment.
     */
    public String analyzeIntruderThreat(IntruderAlert alert) {
        if (available) {
            String result = callOpenAI(buildIntruderPrompt(alert),
                    "You are a cybersecurity expert AI. Analyze login threats concisely.");
            if (result != null && !result.isBlank()) return result;
        }
        return generateOfflineAnalysis(alert);
    }

    /**
     * Provides AI-driven login behavior analysis.
     */
    public String analyzeLoginPattern(List<LoginAttempt> recentAttempts, String username) {
        if (available) {
            String result = callOpenAI(buildPatternPrompt(recentAttempts, username),
                    "You are a cybersecurity AI. Analyze login patterns for threats.");
            if (result != null && !result.isBlank()) return result;
        }
        return generateOfflinePatternAnalysis(recentAttempts, username);
    }

    /**
     * Generates a security recommendation based on system state.
     */
    public String getSecurityRecommendation(int totalUsers, int lockedAccounts, int intruderAlerts) {
        if (available) {
            String prompt = String.format(
                "Security Dashboard Summary:\n" +
                "- Total registered users: %d\n" +
                "- Locked accounts: %d\n" +
                "- Intruder alerts triggered: %d\n\n" +
                "Provide a brief security assessment and 3 actionable recommendations.",
                totalUsers, lockedAccounts, intruderAlerts);
            String result = callOpenAI(prompt, "You are a cybersecurity AI assistant.");
            if (result != null && !result.isBlank()) return result;
        }
        return generateOfflineRecommendation(totalUsers, lockedAccounts, intruderAlerts);
    }

    private String callOpenAI(String userMessage, String systemMessage) {
        try {
            String jsonBody = buildJsonBody(systemMessage, userMessage);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractContent(response.body());
            }
            // Fall through silently to offline analyzer (no raw error shown to user).
        } catch (Exception ignored) { /* network problems handled by offline analyzer */ }
        return null;
    }

    private String buildJsonBody(String systemMessage, String userMessage) {
        String safeSystem = escapeJson(systemMessage);
        String safeUser = escapeJson(userMessage);
        return "{\n" +
               "  \"model\": \"" + MODEL + "\",\n" +
               "  \"messages\": [\n" +
               "    {\"role\": \"system\", \"content\": \"" + safeSystem + "\"},\n" +
               "    {\"role\": \"user\", \"content\": \"" + safeUser + "\"}\n" +
               "  ],\n" +
               "  \"max_tokens\": 300,\n" +
               "  \"temperature\": 0.7\n" +
               "}";
    }

    private String extractContent(String jsonResponse) {
        try {
            int contentStart = jsonResponse.indexOf("\"content\":");
            if (contentStart == -1) return "AI response format unexpected.";
            contentStart += "\"content\":".length();
            int quoteStart = jsonResponse.indexOf('"', contentStart) + 1;
            int quoteEnd = quoteStart;
            while (quoteEnd < jsonResponse.length()) {
                if (jsonResponse.charAt(quoteEnd) == '"' && jsonResponse.charAt(quoteEnd - 1) != '\\') break;
                quoteEnd++;
            }
            return jsonResponse.substring(quoteStart, quoteEnd)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        } catch (Exception e) {
            return "Could not parse AI response.";
        }
    }

    private String buildIntruderPrompt(IntruderAlert alert) {
        return String.format(
            "Intruder Alert Details:\n" +
            "- Username targeted: %s\n" +
            "- Source IP: %s\n" +
            "- Failed attempts: %d\n" +
            "- Time detected: %s\n\n" +
            "Analyze this login attack, classify the threat level (LOW/MEDIUM/HIGH/CRITICAL), " +
            "and suggest 2-3 immediate countermeasures.",
            alert.getUsername(), alert.getIpAddress(),
            alert.getFailedAttemptCount(), alert.getDetectedAt()
        );
    }

    private String buildPatternPrompt(List<LoginAttempt> attempts, String username) {
        StringBuilder sb = new StringBuilder();
        sb.append("Login attempt history for user '").append(username).append("':\n");
        int i = 1;
        for (LoginAttempt a : attempts) {
            sb.append(i++).append(". ").append(a.toLogString()).append("\n");
        }
        sb.append("\nIdentify any suspicious patterns and rate the risk level.");
        return sb.toString();
    }

    private String generateOfflineAnalysis(IntruderAlert alert) {
        int attempts = alert.getFailedAttemptCount();
        String level;
        String severity;
        if (attempts >= 8)      { level = "CRITICAL"; severity = "Coordinated attack pattern detected."; }
        else if (attempts >= 5) { level = "HIGH";     severity = "Sustained brute-force activity."; }
        else if (attempts >= 3) { level = "MEDIUM";   severity = "Repeated unauthorized access attempts."; }
        else                    { level = "LOW";      severity = "Anomalous login behaviour observed."; }

        StringBuilder sb = new StringBuilder();
        sb.append("THREAT ASSESSMENT REPORT\n");
        sb.append("------------------------------------------------\n");
        sb.append("Classification : ").append(level).append(" RISK\n");
        sb.append("Target Account : ").append(alert.getUsername()).append("\n");
        sb.append("Source Vector  : ").append(alert.getIpAddress()).append("\n");
        sb.append("Failed Attempts: ").append(attempts).append("\n");
        sb.append("Detected At    : ").append(alert.getDetectedAt()).append("\n\n");
        sb.append("Behavioural Analysis:\n");
        sb.append("  ").append(severity).append("\n");
        sb.append("  Attack signature consistent with credential-stuffing or\n");
        sb.append("  dictionary-based brute force methodology.\n\n");
        sb.append("Recommended Countermeasures:\n");
        sb.append("  1. Immediately block source IP ").append(alert.getIpAddress()).append(" at perimeter firewall.\n");
        sb.append("  2. Force password rotation for account '").append(alert.getUsername()).append("'.\n");
        sb.append("  3. Notify account holder via secondary channel (email/SMS).\n");
        sb.append("  4. Enable multi-factor authentication on next successful login.\n");
        sb.append("  5. Add subnet to watchlist for 24-hour heightened monitoring.\n");
        return sb.toString();
    }

    private String generateOfflinePatternAnalysis(List<LoginAttempt> attempts, String username) {
        long failed = attempts.stream().filter(a -> a.getStatus() == LoginAttempt.Status.FAILED).count();
        long success = attempts.size() - failed;
        double failureRate = attempts.isEmpty() ? 0 : (failed * 100.0 / attempts.size());

        String risk;
        if (failureRate >= 75)      risk = "CRITICAL";
        else if (failureRate >= 50) risk = "HIGH";
        else if (failureRate >= 25) risk = "ELEVATED";
        else                        risk = "NORMAL";

        StringBuilder sb = new StringBuilder();
        sb.append("BEHAVIOURAL PATTERN ANALYSIS\n");
        sb.append("------------------------------------------------\n");
        sb.append("Subject        : ").append(username).append("\n");
        sb.append("Sample Window  : ").append(attempts.size()).append(" recent attempts\n");
        sb.append("Successful     : ").append(success).append("\n");
        sb.append("Failed         : ").append(failed).append("\n");
        sb.append("Failure Rate   : ").append(String.format("%.1f%%", failureRate)).append("\n");
        sb.append("Risk Indicator : ").append(risk).append("\n\n");
        sb.append("Findings:\n");
        if (failed >= 3) {
            sb.append("  - Multiple failed attempts indicate potential credential compromise.\n");
            sb.append("  - Recommend forced re-authentication and password reset.\n");
        } else if (failed >= 1) {
            sb.append("  - Sporadic failures detected; likely user error rather than attack.\n");
            sb.append("  - Monitor for escalation in next session window.\n");
        } else {
            sb.append("  - Login pattern within normal behavioural baseline.\n");
            sb.append("  - No anomalies detected in recent activity.\n");
        }
        return sb.toString();
    }

    private String generateOfflineRecommendation(int users, int locked, int alerts) {
        double lockRate = users == 0 ? 0 : (locked * 100.0 / users);
        String posture;
        if (alerts >= 3 || lockRate >= 30)      posture = "ELEVATED";
        else if (alerts >= 1 || lockRate >= 10) posture = "MODERATE";
        else                                    posture = "STABLE";

        StringBuilder sb = new StringBuilder();
        sb.append("SYSTEM-WIDE SECURITY ASSESSMENT\n");
        sb.append("------------------------------------------------\n");
        sb.append("Overall Posture     : ").append(posture).append("\n");
        sb.append("Registered Users    : ").append(users).append("\n");
        sb.append("Locked Accounts     : ").append(locked).append(" (").append(String.format("%.1f%%", lockRate)).append(")\n");
        sb.append("Active Intruder     : ").append(alerts).append(" alert").append(alerts == 1 ? "" : "s").append("\n\n");
        sb.append("Strategic Recommendations:\n");
        sb.append("  1. Enforce strong password policy: 12+ chars, mixed case, symbols.\n");
        sb.append("  2. Enable two-factor authentication for all administrative accounts.\n");
        sb.append("  3. Configure automated IP throttling after 3 failed attempts (current: 3).\n");
        sb.append("  4. Schedule weekly review of intruder log files in /intruder_logs.\n");
        sb.append("  5. Implement session timeout of 15 minutes for inactive users.\n\n");
        if (alerts > 0) {
            sb.append("Priority Action: Investigate the ").append(alerts)
              .append(" active intruder alert").append(alerts == 1 ? "" : "s").append(" before next maintenance window.\n");
        } else {
            sb.append("Status: No active threats. Continue routine monitoring.\n");
        }
        return sb.toString();
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public boolean isAvailable() { return available; }
    public String getModelName() { return MODEL; }
}
