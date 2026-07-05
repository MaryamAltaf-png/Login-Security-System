package com.security;

import com.security.ai.AIModel;
import com.security.service.SecurityLog;
import com.security.service.SecuritySystem;
import com.security.service.UserDatabase;
import com.security.util.ConsoleUI;

/**
 * Main - application entry point.
 *
 * AI-Based Device Login Security System
 * ======================================
 * Demonstrates Object-Oriented Programming concepts:
 *   - Encapsulation  : All class fields are private with controlled access
 *   - Abstraction    : AIModel hides HTTP/API details behind simple method calls
 *   - Composition    : SecuritySystem composes UserDatabase, SecurityLog, AIModel
 *   - Single Resp.   : Each class has one clear responsibility
 *   - Constructor overloading (User class)
 *
 * HOW TO RUN:
 *   1. Set your OpenAI API key below (optional - system works offline without it)
 *   2. Compile: javac -d out src/com/security/**\/*.java src/com/security/*.java
 *   3. Run:     java -cp out com.security.Main
 *
 * DEFAULT ACCOUNTS:
 *   admin / Admin@1234  (ADMIN role)
 *   alice / Alice@Pass1 (USER  role)
 *   bob   / Bob@Secure2 (USER  role)
 */
public class Main {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            ConsoleUI.printWarning("OPENAI_API_KEY not set. Running in offline AI mode.");
            ConsoleUI.printInfo("Set it with: export OPENAI_API_KEY=sk-...");
            apiKey = "OFFLINE_MODE";
        }

        UserDatabase userDatabase = new UserDatabase();
        SecurityLog securityLog = new SecurityLog();
        AIModel aiModel = new AIModel(apiKey);
        SecuritySystem securitySystem = new SecuritySystem(userDatabase, securityLog, aiModel);
        LoginSystem loginSystem = new LoginSystem(securitySystem);

        loginSystem.start();

        securityLog.printSessionSummary();
    }
}
