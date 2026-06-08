-- =============================================================
-- Campus Entry & Exit Monitoring System — Database Schema
-- PostgreSQL 14+
--
-- Run this script once to create the database and user, then
-- let DatabaseInitializer.initialize() create the tables on
-- first application start-up.
-- =============================================================

-- 1. Create the database (run as postgres superuser)
-- CREATE DATABASE campus_db;
-- \c campus_db

-- =============================================================
-- 2. students
-- =============================================================
CREATE TABLE IF NOT EXISTS students (
    student_id   VARCHAR(20)  PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    course       VARCHAR(50)  NOT NULL,
    year_level   VARCHAR(20)  NOT NULL,
    contact      VARCHAR(20)  NOT NULL
                     CONSTRAINT chk_contact CHECK (contact ~ '^[0-9]{10,15}$'),
    email        VARCHAR(100) NOT NULL UNIQUE
                     CONSTRAINT chk_email   CHECK (email ~* '^[^@]+@[^@]+\.[^@]+$'),
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  students IS 'Registered students in the campus system.';
COMMENT ON COLUMN students.student_id IS 'Primary key — e.g. 2024-0001.';
COMMENT ON COLUMN students.contact    IS 'Digits only, 10–15 characters.';

INSERT INTO students (student_id, full_name, course, year_level, contact, email)
VALUES ('2024-0001', 'Johnny Bravo', 'BSIT', '4th', '0912345678', 'jbravo@gmail.com');

-- =============================================================
-- 3. faculty
-- =============================================================
CREATE TABLE IF NOT EXISTS faculty (
    id         SERIAL       PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,            -- store BCrypt hash in production
    full_name  VARCHAR(100),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Default admin account (password: admin123) — change in production
INSERT INTO faculty (username, password, full_name)
VALUES ('faculty', 'admin123', 'System Administrator')
ON CONFLICT (username) DO NOTHING;

-- =============================================================
-- 4. attendance
-- =============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id          BIGSERIAL    PRIMARY KEY,
    student_id  VARCHAR(20)  NOT NULL
                    REFERENCES students(student_id) ON DELETE CASCADE,
    action      VARCHAR(10)  NOT NULL
                    CONSTRAINT chk_action CHECK (action IN ('TIME_IN', 'TIME_OUT')),
    timestamp   TIMESTAMP    NOT NULL DEFAULT NOW(),
    exported    BOOLEAN      NOT NULL DEFAULT FALSE
);

COMMENT ON TABLE  attendance IS 'Campus entry/exit log.';
COMMENT ON COLUMN attendance.action   IS 'TIME_IN or TIME_OUT.';
COMMENT ON COLUMN attendance.exported IS 'Marked TRUE after CSV export.';

-- Index — speed up the daily status queries
CREATE INDEX IF NOT EXISTS idx_attendance_student_date
    ON attendance (student_id, DATE(timestamp));
