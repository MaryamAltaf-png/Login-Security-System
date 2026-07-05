package com.security.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LoginAttempt - records each login event.
 * Demonstrates OOP: Immutable value object pattern, Encapsulation
 */
public class LoginAttempt {

    public enum Status {
        SUCCESS, FAILED, INTRUDER_ALERT, ACCOUNT_LOCKED
    }

    private final String username;
    private final LocalDateTime timestamp;
    private final Status status;
    private final String ipAddress;
    private final String message;

    public LoginAttempt(String username, Status status, String ipAddress, String message) {
        this.username = username;
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.ipAddress = ipAddress;
        this.message = message;
    }

    public String getUsername() { return username; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Status getStatus() { return status; }
    public String getIpAddress() { return ipAddress; }
    public String getMessage() { return message; }

    public String toLogString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] [%s] User: %-15s IP: %-15s | %s",
                timestamp.format(fmt), status.name(), username, ipAddress, message);
    }

    @Override
    public String toString() {
        return toLogString();
    }
}
