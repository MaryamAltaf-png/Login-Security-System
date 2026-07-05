package com.security.util;

/**
 * ConsoleUI - handles all terminal display formatting.
 * Demonstrates OOP: Utility class, Separation of concerns
 */
public class ConsoleUI {

    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String WHITE  = "\u001B[37m";
    public static final String BOLD   = "\u001B[1m";

    private ConsoleUI() {
    }

    public static void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║      AI-BASED DEVICE LOGIN SECURITY SYSTEM           ║");
        System.out.println("║         Powered by OpenAI   |   Java OOP             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    public static void printDivider() {
        System.out.println(BLUE + "──────────────────────────────────────────────────────" + RESET);
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "✔  " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "✖  " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "⚠  " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "ℹ  " + msg + RESET);
    }

    public static void printAlert(String msg) {
        System.out.println(RED + BOLD + "🚨 ALERT: " + msg + RESET);
    }

    public static void printAI(String msg) {
        System.out.println(YELLOW + BOLD + "🤖 AI Analysis:\n" + RESET + YELLOW + msg + RESET);
    }

    public static void printSectionHeader(String title) {
        System.out.println(BLUE + BOLD + "\n▶ " + title + RESET);
        printDivider();
    }
}
