package com.security;

import com.security.ai.AIModel;
import com.security.gui.AppWindow;
import com.security.service.SecurityLog;
import com.security.service.SecuritySystem;
import com.security.service.UserDatabase;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * MainGUI - entry point for the Swing graphical interface.
 * Demonstrates OOP: Composition root, Dependency Injection.
 */
public class MainGUI {

    public static void main(String[] args) {
        // Use the system look and feel where available; otherwise Nimbus.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { /* fall back to default */ }

        // Build backend dependencies (the same OOP services used by the CLI).
        // Prefer environment variable; fall back to embedded key for offline demo.
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = "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";
        }
        UserDatabase userDb = new UserDatabase();
        SecurityLog  log    = new SecurityLog();
        AIModel      ai     = new AIModel(apiKey);
        SecuritySystem sec  = new SecuritySystem(userDb, log, ai);

        SwingUtilities.invokeLater(() -> {
            AppWindow window = new AppWindow(sec, userDb, log, ai);
            window.setVisible(true);
        });
    }
}
