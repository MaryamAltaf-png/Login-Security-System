package com.security.service;

import com.security.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * UserDatabase - in-memory user registry (simulates a persistent store).
 * Demonstrates OOP: Encapsulation, Abstraction, use of Generics/Collections
 */
public class UserDatabase {

    private final Map<String, User> users;

    public UserDatabase() {
        this.users = new HashMap<>();
        seedDefaultUsers();
    }

    private void seedDefaultUsers() {
        registerUser("admin", "Admin@1234", "ADMIN");
        registerUser("alice", "Alice@Pass1", "USER");
        registerUser("bob", "Bob@Secure2", "USER");
    }

    public boolean registerUser(String username, String password, String role) {
        if (username == null || username.isBlank()) return false;
        if (users.containsKey(username.toLowerCase())) return false;
        if (!isPasswordStrong(password)) return false;

        User user = new User(username.toLowerCase(), password, role);
        users.put(user.getUsername(), user);
        return true;
    }

    public User findUser(String username) {
        if (username == null) return null;
        return users.get(username.toLowerCase());
    }

    public boolean existsUser(String username) {
        return users.containsKey(username != null ? username.toLowerCase() : "");
    }

    public int getTotalUsers() { return users.size(); }

    public long getLockedAccountCount() {
        return users.values().stream().filter(u -> !u.isActive()).count();
    }

    public Collection<User> getAllUsers() { return users.values(); }

    /**
     * Basic password strength validation.
     * Must be at least 8 chars, contain digit and uppercase.
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpper = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasDigit;
    }
}
