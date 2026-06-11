package com.database;


import java.sql.*;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    private static final String URL      = "jdbc:mysql://localhost:3306/sistemlostfound";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    public static String ERROR_MESSAGE = "";
    
    
    private DBConnection() {
        try {
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Koneksi Database berhasil.");
        } catch (SQLException e) {
            ERROR_MESSAGE = e.getMessage();
            System.out.println("Koneksi Database gagal: " + ERROR_MESSAGE);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Reconnect berhasil.");
            }
        } catch (SQLException e) {
            ERROR_MESSAGE = e.getMessage();
            System.out.println("[DB] Reconnect gagal: " + ERROR_MESSAGE);
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Koneksi ditutup.");
            }
        } catch (SQLException e) {
            ERROR_MESSAGE = e.getMessage();
            System.out.println("[DB] Gagal menutup koneksi: " + ERROR_MESSAGE);
        }
    }
}
