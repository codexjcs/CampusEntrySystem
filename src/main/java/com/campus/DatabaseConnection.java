package com.campus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "5432";
    private static final String DATABASE = "log_IN_OUT";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "db_pass";

    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}