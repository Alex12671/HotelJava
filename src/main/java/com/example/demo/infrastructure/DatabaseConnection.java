package com.example.demo.infrastructure;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connection;
    private static final String user = "root";
    private static final String password = "";
    private static final String url = "jdbc:mysql://localhost:3306/hotel";

    public static Connection connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        connection.close();
    }

}
