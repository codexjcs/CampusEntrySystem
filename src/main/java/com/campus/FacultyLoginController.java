package com.campus;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * FacultyLoginController
 *
 * Authenticates faculty against the faculty table in the database.
 * Replaces the old hard-coded credential check.
 */
public class FacultyLoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // ── Null/blank guard ─────────────────────────────────────────────────
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please enter your username and password.");
            return;
        }

        // ── DB authentication ────────────────────────────────────────────────
        try {
            if (authenticate(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                        "Welcome, " + username + "!");
                MainApp.switchScene("dashboard.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                        "Invalid username or password.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Could not verify credentials: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back() {
        try {
            MainApp.switchScene("role-selection.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Check username + password against the faculty table.
     * Password comparison is plain-text here; swap for BCrypt when ready.
     */
    private boolean authenticate(String username,
                                 String password) throws SQLException {
        String sql = "SELECT 1 FROM faculty WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
