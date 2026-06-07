package com.campus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseInitializer
 *
 * Creates all tables and constraints on first run.
 * Call DatabaseInitializer.initialize() from MainApp.start() before showing any scene.
 */
public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // ── 1. students ─────────────────────────────────────────────────
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    student_id   VARCHAR(20)  PRIMARY KEY,
                    full_name    VARCHAR(100) NOT NULL,
                    course       VARCHAR(50)  NOT NULL,
                    year_level   VARCHAR(20)  NOT NULL,
                    contact      VARCHAR(20)  NOT NULL,
                    email        VARCHAR(100) NOT NULL UNIQUE,
                    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
                    CONSTRAINT chk_email   CHECK (email    ~* '^[^@]+@[^@]+\\.[^@]+$'),
                    CONSTRAINT chk_contact CHECK (contact  ~  '^[0-9]{10,15}$')
                )
            """);

            // ── 2. faculty ───────────────────────────────────────────────────
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS faculty (
                    id         SERIAL       PRIMARY KEY,
                    username   VARCHAR(50)  NOT NULL UNIQUE,
                    password   VARCHAR(255) NOT NULL,
                    full_name  VARCHAR(100),
                    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
                )
            """);

            // Seed default faculty account (password: admin123)
            stmt.executeUpdate("""
                INSERT INTO faculty (username, password, full_name)
                VALUES ('faculty', 'admin123', 'System Administrator')
                ON CONFLICT (username) DO NOTHING
            """);

            // ── 3. attendance ────────────────────────────────────────────────
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id          BIGSERIAL    PRIMARY KEY,
                    student_id  VARCHAR(20)  NOT NULL
                                REFERENCES students(student_id) ON DELETE CASCADE,
                    action      VARCHAR(10)  NOT NULL
                                CHECK (action IN ('TIME_IN', 'TIME_OUT')),
                    timestamp   TIMESTAMP    NOT NULL DEFAULT NOW(),
                    exported    BOOLEAN      NOT NULL DEFAULT FALSE
                )
            """);

            // Index for the common date+student filter used by status checks
            stmt.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_attendance_student_date
                ON attendance (student_id, DATE(timestamp))
            """);

            System.out.println("[DB] Tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
