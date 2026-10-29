package ftms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Update your database name, username, and password if they differ
    private static final String URL = "jdbc:mysql://localhost:3306/ftams_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Enter your MySQL root password here

    private static Connection connection = null;

    // Private constructor to prevent instantiation (Singleton Pattern)
    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in dependencies!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
        }
        return connection;
    }
}