package com.security.model;

import com.security.util.PasswordHasher;
import java.time.LocalDateTime;

/**
 * User class - represents a registered user in the security system.
 * Demonstrates OOP: Encapsulation, Constructor overloading
 */
public class User {

    private String username;
    private String hashedPassword;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private int failedAttempts;

    public User(String username, String plainPassword) {
        this(username, plainPassword, "USER");
    }

    public User(String username, String plainPassword, String role) {
        this.username = username;
        this.hashedPassword = PasswordHasher.hash(plainPassword);
        this.role = role;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.failedAttempts = 0;
    }

    public boolean verifyPassword(String plainPassword) {
        return PasswordHasher.verify(plainPassword, this.hashedPassword);
    }

    public void recordFailedAttempt() {
        this.failedAttempts++;
        if (this.failedAttempts >= 3) {
            this.isActive = false;
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.isActive = true;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        resetFailedAttempts();
    }

    public String getUsername() { return username; }
    public String getHashedPassword() { return hashedPassword; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public int getFailedAttempts() { return failedAttempts; }

    @Override
    public String toString() {
        return "User{username='" + username + "', role='" + role +
               "', active=" + isActive + ", failedAttempts=" + failedAttempts + "}";
    }
}
