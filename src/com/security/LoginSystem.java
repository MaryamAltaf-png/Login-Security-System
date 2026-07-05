package com.security;

import com.security.ai.AIModel;
import com.security.service.SecurityLog;
import com.security.service.SecuritySystem;
import com.security.service.UserDatabase;
import com.security.util.ConsoleUI;
import java.util.Scanner;

/**
 * LoginSystem - interactive CLI login interface.
 * Demonstrates OOP: Composition, User Interaction Loop, System Orchestration
 *
 * Default accounts:
 *   admin / Admin@1234  (ADMIN role)
 *   alice / Alice@Pass1 (USER  role)
 *   bob   / Bob@Secure2 (USER  role)
 */
public class LoginSystem {

    private final SecuritySystem securitySystem;
    private final Scanner scanner;

    public LoginSystem(SecuritySystem securitySystem) {
        this.securitySystem = securitySystem;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        ConsoleUI.printBanner();
        ConsoleUI.printInfo("Type 'help' at any menu to see available options.");

        boolean running = true;
        while (running) {
            ConsoleUI.printSectionHeader("Main Menu");
            System.out.println("  [1] Login");
            System.out.println("  [2] Register new user");
            System.out.println("  [3] Security dashboard (AI)");
            System.out.println("  [4] View session log");
            System.out.println("  [5] Exit");
            System.out.print("\nChoose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "3" -> securitySystem.printSecurityDashboard();
                case "4" -> securitySystem.getUserDatabase()
                        .getAllUsers()
                        .forEach(u -> ConsoleUI.printInfo(u.toString()));
                case "5" -> {
                    ConsoleUI.printInfo("Shutting down security system. Goodbye.");
                    running = false;
                }
                case "help" -> printHelp();
                default -> ConsoleUI.printWarning("Invalid option. Try again.");
            }
        }

        securitySystem.getUserDatabase();
    }

    private void handleLogin() {
        ConsoleUI.printSectionHeader("User Login");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String ip = simulateIpAddress();
        ConsoleUI.printInfo("Authenticating from IP: " + ip);

        boolean success = securitySystem.verifyPassword(username, password, ip);

        if (success) {
            showUserMenu(username);
        }
    }

    private void showUserMenu(String username) {
        ConsoleUI.printSectionHeader("User Session — " + username);
        System.out.println("  [1] View profile");
        System.out.println("  [2] Logout");
        System.out.print("\nChoose option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                var user = securitySystem.getUserDatabase().findUser(username);
                if (user != null) {
                    System.out.println("\n  Username    : " + user.getUsername());
                    System.out.println("  Role        : " + user.getRole());
                    System.out.println("  Last Login  : " + user.getLastLoginAt());
                    System.out.println("  Account Active: " + user.isActive());
                }
                securitySystem.logout();
            }
            case "2" -> securitySystem.logout();
            default -> {
                ConsoleUI.printWarning("Invalid option.");
                securitySystem.logout();
            }
        }
    }

    private void handleRegister() {
        ConsoleUI.printSectionHeader("Register New User");
        System.out.print("New username: ");
        String username = scanner.nextLine().trim();

        System.out.print("New password (min 8 chars, 1 uppercase, 1 digit): ");
        String password = scanner.nextLine().trim();

        boolean success = securitySystem.getUserDatabase().registerUser(username, password, "USER");
        if (success) {
            ConsoleUI.printSuccess("User '" + username + "' registered successfully!");
        } else {
            ConsoleUI.printError("Registration failed: username taken, invalid password, or blank username.");
            ConsoleUI.printInfo("Password must be at least 8 characters with 1 uppercase letter and 1 digit.");
        }
    }

    private void printHelp() {
        ConsoleUI.printSectionHeader("Help");
        System.out.println("  Default test accounts:");
        System.out.println("    admin / Admin@1234  (Admin role)");
        System.out.println("    alice / Alice@Pass1 (User role)");
        System.out.println("    bob   / Bob@Secure2 (User role)");
        System.out.println();
        System.out.println("  Security rules:");
        System.out.println("    - 3 wrong passwords locks the account");
        System.out.println("    - AI analyzes each intruder alert");
        System.out.println("    - All events are logged to logs/login_attempts.txt");
    }

    private String simulateIpAddress() {
        int[] octets = {192, 168, (int)(Math.random() * 255), (int)(Math.random() * 255)};
        return octets[0] + "." + octets[1] + "." + octets[2] + "." + octets[3];
    }
}
